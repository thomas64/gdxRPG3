package nl.t64.cot.components.quest

import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.components.party.XpRewarder


data class QuestGraph(
    val id: String = "",
    val title: String = "",
    val entityId: String = "",
    val summary: String = "",
    val isSubQuest: Boolean = false,
    val isHidden: Boolean = false,
    val linkedWith: List<String> = emptyList(),
    val tasks: Map<String, QuestTask> = emptyMap()
) {
    var currentState: QuestState = QuestState.UNKNOWN
    var resetState: QuestState = QuestState.UNKNOWN
    var isFailed: Boolean = false

    override fun toString(): String {
        return when {
            isFailed -> "x  $title"
            currentState == QuestState.FINISHED -> "v  $title"
            currentState == QuestState.UNCLAIMED -> "o   $title"
            resetState == QuestState.FINISHED -> "r  $title"
            else -> "     $title"
        }
    }

    fun isOneOfBothStatesEqualOrHigherThan(questState: QuestState): Boolean =
        resetState.isEqualOrHigherThan(questState) || currentState.isEqualOrHigherThan(questState)

    fun isCurrentStateEqualOrHigherThan(questState: QuestState): Boolean = currentState.isEqualOrHigherThan(questState)
    fun isCurrentStateEqualOrLowerThan(questState: QuestState): Boolean = currentState.isEqualOrLowerThan(questState)

    fun getAllQuestTasksForVisual(): Array<QuestTask> {
        return (tasks + getTasksOfAcceptedSubQuests())
            .toSortedMap(compareBy<String> { it.length }.thenBy { it })
            .map { it.value }
            .filter { !it.isHidden }
            .toTypedArray()
    }

    private fun getTasksOfAcceptedSubQuests(): Map<String, QuestTask> {
        return linkedWith
            .map { gameData.quests.getQuestById(it) }
            .filter { it.isCurrentStateEqualOrHigherThan(QuestState.ACCEPTED) }
            .map { it.tasks }
            .flatMap { it.entries }
            .associate { it.key to it.value }
    }

    fun reset() {
        if (resetState.isLowerThan(currentState)) {
            resetState = currentState
        }
        currentState = QuestState.UNKNOWN
        tasks.values.forEach { it.possibleReset() }
    }

    fun know() {
        currentState = QuestState.KNOWN
    }

    fun accept() {
        if (currentState.isEqualOrLowerThan(QuestState.KNOWN)) {
            setAcceptedAndPossiblyShowMessage()
        }
        possibleFinish(true)
    }

    private fun setAcceptedAndPossiblyShowMessage() {
        currentState = QuestState.ACCEPTED
        if (isSubQuest) {
            showMessageTooltipQuestUpdated()
        } else {
            showMessageTooltipQuestNew()
        }
    }

    fun unclaim() {
        currentState = QuestState.UNCLAIMED
    }

    fun possibleSetFindItemTaskComplete() {
        if (currentState.isEqualOrLowerThan(QuestState.ACCEPTED)) {
            tasks.filterValues { it.type == QuestTaskType.FIND_ITEM }
                .filterValues { it.hasTargetInInventoryOrEquipment() }
                .forEach { setTaskComplete(it.key) }
        }
    }

    fun possibleSetShowItemTaskComplete() {
        accept()
        tasks.filterValues { it.type == QuestTaskType.SHOW_ITEM }
            .filterValues { it.hasTargetInInventoryOrEquipment() }
            .forEach { setTaskComplete(it.key) }
    }

    fun possibleSetWearItemTaskComplete() {
        accept()
        tasks.filterValues { it.type == QuestTaskType.WEAR_ITEM }
            .filterValues { it.hasTargetInPlayerEquipment() }
            .forEach { setTaskComplete(it.key) }
    }

    fun possibleSetGiveItemTaskComplete() {
        accept()
        tasks.toSortedMap(compareBy<String> { it.length }.thenBy { it })
            .filterValues { it.type == QuestTaskType.GIVE_ITEM }
            .filterValues { !it.isComplete }
            .filterValues { it.hasTargetInInventory() }
            .entries
            .first()
            .let { setTaskComplete(it.key) }
    }

    fun possibleSetTradeItemsTaskComplete(): Loot {
        accept()
        val loot: List<Loot> = tasks
            .filterValues { it.type == QuestTaskType.TRADE_ITEMS }
            .filterValues { it.hasTargetInInventory() }
            .onEach { setTaskComplete(it.key) }
            .map { Loot(it.value.receive.toMutableMap()) }
        return if (loot.size == 1) loot[0]
        else throw IllegalStateException("Can only contain 1 TRADE_ITEMS QuestTaskType.")
    }

    fun getReceiveItemsForgottenTradeItemsTask(): Loot {
        return tasks.values
            .first { it.type == QuestTaskType.TRADE_ITEMS }
            .let { Loot(it.receive.toMutableMap()) }
    }

    fun setSayTheRightThingTaskComplete() {
        accept()
        tasks.filterValues { it.type == QuestTaskType.SAY_THE_RIGHT_THING }
            .forEach { setTaskComplete(it.key) }
    }

    fun setKillTaskComplete() {
        if (resetState.isEqualOrHigherThan(QuestState.ACCEPTED)) {
            accept()
        }
        tasks.filterValues { it.type == QuestTaskType.KILL }
            .forEach { setTaskComplete(it.key) }
    }

    fun setTaskComplete(taskId: String, showTooltip: Boolean = true) {
        if (!isTaskComplete(taskId)) {
            val questTask = tasks[taskId]!!
            questTask.setComplete()
            unhideTaskWithLinkedTask(questTask)
            if (showTooltip) showMessageTooltipQuestUpdated()
            possibleFinish(showTooltip)
        }
    }

    private fun unhideTaskWithLinkedTask(questTask: QuestTask) {
        questTask.isHidden = false
        handleLinkedTasksOf(questTask)
    }

    private fun handleLinkedTasksOf(questTask: QuestTask) {
        questTask.linkedWith.forEach {
            val linkedTask = tasks[it] ?: gameData.quests.getQuestById(linkedWith[0]).tasks[it]!!
            linkedTask.handleLinked()
            if (linkedTask.isComplete) {
                handleLinkedTasksOf(linkedTask)
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
            .filterValues { !it.isOptional }
            .filterKeys { it.toInt() < taskId.toInt() }
            .all { it.value.isComplete }
    }

    fun forceFinish() {
        if (currentState != QuestState.FINISHED) {
            finish(false)
        }
    }

    private fun possibleFinish(showTooltip: Boolean) {
        if (isReadyToBeFinished()) {
            if (resetState == QuestState.FINISHED) {
                finish(false)
            } else {
                finish(showTooltip)
            }
        }
    }

    private fun isReadyToBeFinished(): Boolean {
        return currentState.isEqualOrHigherThan(QuestState.ACCEPTED)
                && currentState != QuestState.FINISHED
                && areAllQuestTasksComplete()
    }

    fun finish(showTooltip: Boolean) {
        XpRewarder.receivePossibleXp(id)
        possibleSetLastReturnTaskComplete()
        currentState = QuestState.FINISHED
        if (showTooltip) showMessageTooltipQuestCompleted()
    }

    private fun possibleSetLastReturnTaskComplete() {
        tasks.toSortedMap(compareBy<String> { it.length }.thenBy { it })
            .map { it.value }
            .takeLast(1)
            .filter { it.type == QuestTaskType.RETURN }
            .filter { !it.isComplete }
            .forEach { it.setComplete() }
    }

    private fun areAllQuestTasksComplete(): Boolean {
        return tasks.values
            .filter { !it.isOptional }
            .all { it.isComplete }
    }

    private fun showMessageTooltipQuestNew() {
        if (!isHidden && resetState == QuestState.UNKNOWN) {
            brokerManager.questObservers.notifyShowMessageTooltip("New quest:" + System.lineSeparator() + title)
        }
    }

    private fun showMessageTooltipQuestUpdated() {
        if (!isHidden && currentState == QuestState.ACCEPTED
            && (!isReadyToBeFinished() || (isSubQuest && isReadyToBeFinished()))
        ) {
            brokerManager.questObservers.notifyShowMessageTooltip("Quest updated:" + System.lineSeparator() + title)
        }
    }

    private fun showMessageTooltipQuestCompleted() {
        if (!isHidden && !isSubQuest) {
            audioManager.handle(AudioCommand.SE_STOP_ALL)
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_REWARD)
            brokerManager.questObservers.notifyShowMessageTooltip("Quest completed:" + System.lineSeparator() + title)
        }
    }

    private fun showMessageTooltipQuestFailed() {
        brokerManager.questObservers.notifyShowMessageTooltip("Quest failed:" + System.lineSeparator() + title)
    }

}
