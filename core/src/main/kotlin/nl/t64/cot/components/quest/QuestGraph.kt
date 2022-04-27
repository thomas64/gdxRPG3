package nl.t64.cot.components.quest

import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.loot.Loot


private val DEFAULT_STATE = QuestState.UNKNOWN

data class QuestGraph(
    val id: String = "",
    val title: String = "",
    val entityId: String = "",
    val summary: String = "",
    val isHidden: Boolean = false,
    val linkedWith: String? = null,
    val tasks: Map<String, QuestTask> = emptyMap()
) {
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

    fun getAllQuestTasksForVisual(): Array<QuestTask> {
        return tasks.entries
            .sortedBy { it.key }
            .map { it.value }
            .filter { !it.isHidden }
            .toTypedArray()
    }

    fun accept() {
        if (isCurrentStateEqualOrLowerThan(QuestState.KNOWN)) {
            currentState = QuestState.ACCEPTED
            showMessageTooltipQuestNew()
        }
        possibleCompleteQuest()
    }

    fun possibleSetFindItemTaskComplete() {
        tasks.filter { it.value.type == QuestTaskType.FIND_ITEM }
            .filter { it.value.hasTargetInInventoryOrEquipment() }
            .forEach { setTaskComplete(it.key) }
    }

    fun possibleSetShowItemTaskComplete() {
        accept()
        tasks.filter { it.value.type == QuestTaskType.SHOW_ITEM }
            .filter { it.value.hasTargetInInventoryOrEquipment() }
            .forEach { setTaskComplete(it.key) }
    }

    fun possibleSetWearItemTaskComplete() {
        accept()
        tasks.filter { it.value.type == QuestTaskType.WEAR_ITEM }
            .filter { it.value.hasTargetInPlayerEquipment() }
            .forEach { setTaskComplete(it.key) }
    }

    fun possibleSetGiveItemTaskComplete() {
        accept()
        tasks.filter { it.value.type == QuestTaskType.GIVE_ITEM }
            .filter { it.value.hasTargetInInventory() }
            .forEach { setTaskComplete(it.key) }
    }

    fun setSayTheRightThingTaskComplete() {
        accept()
        tasks.filter { it.value.type == QuestTaskType.SAY_THE_RIGHT_THING }
            .forEach { setTaskComplete(it.key) }
    }

    fun setKillTaskComplete() {
        accept()
        tasks.filter { it.value.type == QuestTaskType.KILL }
            .forEach { setTaskComplete(it.key) }
    }

    fun setTaskComplete(taskId: String) {
        if (!isTaskComplete(taskId)) {
            val questTask = tasks[taskId]!!
            questTask.setComplete()
            unhideTaskWithLinkedTask(questTask)
            showMessageTooltipQuestUpdated()
            possibleCompleteQuest()
        }
    }

    private fun unhideTaskWithLinkedTask(questTask: QuestTask) {
        questTask.isHidden = false
        handleLinked(questTask)
    }

    private fun handleLinked(questTask: QuestTask) {
        questTask.linkedWith?.let {
            val nextTask = tasks[it]!!
            nextTask.handleLinked()
            if (nextTask.isComplete) {
                handleLinked(nextTask)
            }
        }
    }

    fun isTaskComplete(taskId: String?): Boolean {
        return tasks[taskId]?.isComplete ?: false
    }

    fun isTaskActive(taskId: String): Boolean {
        val questTask = tasks[taskId]!!
        check(!questTask.isOptional) { "An optional task cannot be the active one." }
        if (questTask.isHidden) return false
        if (questTask.isComplete) return false
        return tasks
            .filter { !it.value.isOptional }
            .filter { it.key.toInt() < taskId.toInt() }
            .all { it.value.isComplete }
    }

//    fun handleReceive() {
//        val receiveLoot = tasks.values
//            .filter { it.type == QuestTaskType.ITEM_DELIVERY }
//            .map { Loot(it.target as MutableMap<String, Int>) }
//            .first()
//    }
//
//    private fun partyGainXp(reward: Loot): String? {
//        if (reward.isXpGained()) {
//            return null
//        }
//        val levelUpMessage = StringBuilder()
//        gameData.party.gainXp(reward.xp, levelUpMessage)
//        showMessageTooltipRewardXp(reward)
//        reward.clearXp()
//        return levelUpMessage.toString().trim().ifEmpty { null }
//    }

    private fun possibleCompleteQuest() {
        if (isCurrentStateEqualOrHigherThan(QuestState.ACCEPTED)
            && currentState != QuestState.FINISHED
            && areAllQuestTasksComplete()
        ) {
            completeQuest()
        }
    }

    private fun completeQuest() {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_REWARD)
        currentState = QuestState.FINISHED
        showMessageTooltipQuestCompleted()
    }

    private fun areAllQuestTasksComplete(): Boolean {
        return tasks.values
            .filter { !it.isOptional }
            .all { it.isComplete }
    }

    private fun showMessageTooltipQuestNew() {
        if (!isHidden) {
            brokerManager.questObservers.notifyShowMessageTooltip("New quest:" + System.lineSeparator() + title)
        }
    }

    private fun showMessageTooltipQuestUpdated() {
        if (!isHidden && currentState == QuestState.ACCEPTED) {
            brokerManager.questObservers.notifyShowMessageTooltip("Quest updated:" + System.lineSeparator() + title)
        }
    }

    private fun showMessageTooltipQuestCompleted() {
        if (!isHidden) {
            brokerManager.questObservers.notifyShowMessageTooltip("Quest completed:" + System.lineSeparator() + title)
        }
    }

    private fun showMessageTooltipQuestFailed() {
        brokerManager.questObservers.notifyShowMessageTooltip("Quest failed:" + System.lineSeparator() + title)
    }

    private fun showMessageTooltipRewardXp(reward: Loot) {
        brokerManager.questObservers.notifyShowMessageTooltip("+ ${reward.xp} XP")
    }

}
