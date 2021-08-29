package nl.t64.cot.components.quest

import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.conversation.ConversationSubject


private val DEFAULT_STATE = QuestState.UNKNOWN

class QuestGraph(
    val title: String = "",
    val entityId: String = "",
    val summary: String = "",
    val isHidden: Boolean = false,
    val linkedWith: String? = null,
    val tasks: Map<String, QuestTask> = emptyMap()
) {
    lateinit var id: String
    var currentState: QuestState = DEFAULT_STATE
    var isFailed: Boolean = false

    override fun toString(): String {
        return when {
            isFailed -> "x  $title"
            currentState == QuestState.FINISHED -> "v  $title"
            currentState == QuestState.UNCLAIMED -> "o   $title"
            else -> "     $title"
        }
    }

    fun isCurrentStateEqualOrHigherThan(questState: QuestState): Boolean = currentState.isEqualOrHigherThan(questState)
    fun isCurrentStateEqualOrLowerThan(questState: QuestState): Boolean = currentState.isEqualOrLowerThan(questState)

    fun getAllTasksForVisual(): Array<QuestTask> = tasks.entries
        .sortedBy { it.key }
        .map { it.value }
        .onEach { possibleUnhide(it) }
        .filter { !it.isHidden }
        .toTypedArray()

    private fun possibleUnhide(it: QuestTask) {
        if (it.isCompleteForReturn()) {
            it.isHidden = false
            it.linkedWith?.let {
                tasks[it]!!.isHidden = false
            }
        }
    }

    fun handleShowQuestItem() {
        tasks.entries
            .filter { it.value.type == QuestTaskType.SHOW_ITEM }
            .forEach { setTaskComplete(it.key) }
    }

    fun setTaskComplete(taskId: String) {
        tasks[taskId]!!.setComplete()
        if (currentState == QuestState.ACCEPTED) {
            showMessageTooltipQuestUpdated()
        }
        checkIfAllTasksAreComplete()
    }

    fun isTaskComplete(taskId: String?): Boolean {
        if (taskId == null) return false
        return tasks[taskId]!!.isComplete
    }

    fun handleAccept(continueConversation: (String) -> Unit) {
        handleTolerate()
        val phraseId =
            if (doesReturnMeetDemand()) Constant.PHRASE_ID_QUEST_IMMEDIATE_SUCCESS else Constant.PHRASE_ID_QUEST_ACCEPT
        continueConversation.invoke(phraseId)
    }

    fun handleTolerate() {
        know()
        accept()
        if (areAllQuestTasksComplete()) {
            completeQuest()
        }
    }

    fun handleReceive(observers: ConversationSubject) {
        know()
        val receiveLoot = getAllQuestTasks()
            .filter { it.type == QuestTaskType.ITEM_DELIVERY }
            .map { Loot(it.target as MutableMap<String, Int>) }
            .first()
        observers.notifyShowReceiveDialog(receiveLoot)
    }

    fun handleCheckIfLinkedIsKnown(phraseId: String, continueConversation: (String) -> Unit) {
        val newPhraseId =
            if (linkedWith != null && gameData.quests.isCurrentStateEqualOrHigherThan(linkedWith, QuestState.KNOWN)) {
                Constant.PHRASE_ID_QUEST_LINKED
            } else phraseId
        continueConversation.invoke(newPhraseId)
    }

    fun handleCheckIfAccepted(
        phraseId: String,
        continueConversation: (String) -> Unit,
        endConversation: (String) -> Unit
    ) {
        if (currentState == QuestState.ACCEPTED) {
            continueConversation.invoke(Constant.PHRASE_ID_QUEST_DELIVERY)
        } else {
            endConversation.invoke(phraseId)
        }
    }

    fun handleCheckIfAcceptedInventory(
        taskId: String,
        phraseId: String,
        continueConversation: (String) -> Unit,
        endConversation: (String) -> Unit
    ) {
        if (currentState == QuestState.ACCEPTED && tasks[taskId]!!.hasTargetInInventory()) {
            continueConversation.invoke(Constant.PHRASE_ID_QUEST_DELIVERY)
        } else {
            endConversation.invoke(phraseId)
        }
    }

    fun handleReturn(continueConversation: (String) -> Unit) {
        val phraseId =
            if (doesReturnMeetDemand()) Constant.PHRASE_ID_QUEST_SUCCESS else Constant.PHRASE_ID_QUEST_NO_SUCCESS
        continueConversation.invoke(phraseId)
    }

    fun handleAcceptOrReturn(continueConversation: (String) -> Unit) {
        if (isCurrentStateEqualOrLowerThan(QuestState.KNOWN)) {
            handleAccept(continueConversation)
        } else if (currentState == QuestState.ACCEPTED) {
            handleReturn(continueConversation)
        } else {
            throw IllegalStateException("You've messed up the conversation quest flow.")
        }
    }

    fun handleReward(endConversation: (String) -> Unit, observers: ConversationSubject) {
        if (currentState == QuestState.ACCEPTED) {
            handleRewardPart1()
        }
        if (currentState == QuestState.UNCLAIMED) {
            handleRewardPart2(observers, endConversation)
        } else {
            throw IllegalStateException("You've messed up the conversation quest flow.")
        }
    }

    fun handleFail() {
        isFailed = true
        showMessageTooltipQuestFailed()
    }

    fun know() {
        if (currentState == QuestState.UNKNOWN) {
            currentState = QuestState.KNOWN
        }
    }

    fun accept() {
        if (currentState == QuestState.KNOWN) {
            currentState = QuestState.ACCEPTED
            if (!isHidden) showMessageTooltipQuestNew()
        }
    }

    fun unclaim() {
        if (currentState == QuestState.ACCEPTED) {
            currentState = QuestState.UNCLAIMED
        } else {
            throw IllegalStateException("Only quest ACCEPTED can be UNCLAIMED.")
        }
    }

    fun finish() {
        if (currentState == QuestState.UNCLAIMED) {
            currentState = QuestState.FINISHED
        } else {
            throw IllegalStateException("Only quest UNCLAIMED can be FINISHED.")
        }
    }

    private fun handleRewardPart1() {
        takeDemands()
        unclaim()
        getAllQuestTasks().forEach { it.forceFinished() }
        if (!isHidden) showMessageTooltipQuestCompleted()
    }

    private fun handleRewardPart2(observers: ConversationSubject, endConversation: (String) -> Unit) {
        val reward = gameData.loot.getLoot(id)
        reward.removeBonus()
        val levelUpMessage = partyGainXp(reward)
        if (reward.isTaken()) {
            finish()
            endConversation.invoke(Constant.PHRASE_ID_QUEST_FINISHED)
            levelUpMessage?.let { observers.notifyShowLevelUpDialog(it) }
        } else {
            observers.notifyShowRewardDialog(reward, levelUpMessage)
        }
    }

    private fun takeDemands() {
        getAllQuestTasks()
            .filter { it.type == QuestTaskType.FETCH_ITEM }
            .forEach { it.removeTargetFromInventory() }
    }

    private fun partyGainXp(reward: Loot): String? {
        if (reward.isXpGained()) {
            return null
        }
        val levelUpMessage = StringBuilder()
        gameData.party.gainXp(reward.xp, levelUpMessage)
        showMessageTooltipRewardXp(reward)
        reward.clearXp()
        return levelUpMessage.toString().trim().ifEmpty { null }
    }

    private fun doesReturnMeetDemand(): Boolean {
        val anyOfs = getAllQuestTasks().filter { it.isAnyOf }
        return when {
            anyOfs.isEmpty() -> doesReturnMeetDemandWithoutAnyOfs()
            else -> doesReturnMeetDemandWithAnyOfs(anyOfs)
        }
    }

    private fun doesReturnMeetDemandWithAnyOfs(anyOfs: List<QuestTask>): Boolean {
        return when {
            anyOfs.none { it.isCompleteForReturn() } -> false
            else -> doesReturnMeetDemandWithoutAnyOfs()
        }
    }

    private fun doesReturnMeetDemandWithoutAnyOfs(): Boolean {
        return getAllQuestTasks()
            .filter { !it.isOptional }
            .filter { !it.isAnyOf }
            .all { it.isCompleteForReturn() }
    }

    private fun checkIfAllTasksAreComplete() {
        if (isCurrentStateEqualOrHigherThan(QuestState.ACCEPTED)
            && currentState != QuestState.FINISHED
            && areAllQuestTasksComplete()
        ) {
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_REWARD)
            completeQuest()
        }
    }

    private fun completeQuest() {
        unclaim()
        finish()
        showMessageTooltipQuestCompleted()
    }

    private fun areAllQuestTasksComplete(): Boolean {
        return getAllQuestTasks()
            .filter { !it.isOptional }
            .all { it.isComplete }
    }

    private fun getAllQuestTasks(): List<QuestTask> = ArrayList(tasks.values)

    private fun showMessageTooltipQuestNew() {
        brokerManager.questObservers.notifyShowMessageTooltip("New quest:" + System.lineSeparator() + System.lineSeparator() + title)
    }

    private fun showMessageTooltipQuestUpdated() {
        brokerManager.questObservers.notifyShowMessageTooltip("Quest updated:" + System.lineSeparator() + System.lineSeparator() + title)
    }

    private fun showMessageTooltipQuestCompleted() {
        brokerManager.questObservers.notifyShowMessageTooltip("Quest completed:" + System.lineSeparator() + System.lineSeparator() + title)
    }

    private fun showMessageTooltipQuestFailed() {
        brokerManager.questObservers.notifyShowMessageTooltip("Quest failed:" + System.lineSeparator() + System.lineSeparator() + title)
    }

    private fun showMessageTooltipRewardXp(reward: Loot) {
        brokerManager.questObservers.notifyShowMessageTooltip("+ ${reward.xp} XP")
    }

}
