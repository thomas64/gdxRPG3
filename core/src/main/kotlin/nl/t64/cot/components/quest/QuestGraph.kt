package nl.t64.cot.components.quest

import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.party.XpRewarder


data class QuestGraph(
    val id: String = "",
    val title: String = "",
    val entityId: String = "",
    val summary: String = "",
    val isHidden: Boolean = false,
    val linkedWith: String? = null,
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
        return tasks.entries
            .sortedBy { it.key }
            .map { it.value }
            .filter { !it.isHidden }
            .toTypedArray()
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
            currentState = QuestState.ACCEPTED
            if (resetState == QuestState.UNKNOWN) {
                showMessageTooltipQuestNew()
            }
        }
        possibleFinish()
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
        tasks.filterValues { it.type == QuestTaskType.GIVE_ITEM }
            .filterValues { it.hasTargetInInventory() }
            .forEach { setTaskComplete(it.key) }
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
            .filterValues { !it.isOptional }
            .filterKeys { it.toInt() < taskId.toInt() }
            .all { it.value.isComplete }
    }

    fun forceFinish(showTooltip: Boolean = false) {
        if (currentState != QuestState.FINISHED) {
            finish(showTooltip)
        }
    }

//    fun handleReceive() {
//        val receiveLoot = tasks.values
//            .filter { it.type == QuestTaskType.ITEM_DELIVERY }
//            .map { Loot(it.target as MutableMap<String, Int>) }
//            .first()
//    }

    private fun possibleFinish(showTooltip: Boolean = true) {
        if (currentState.isEqualOrHigherThan(QuestState.ACCEPTED)
            && currentState != QuestState.FINISHED
            && areAllQuestTasksComplete()
        ) {
            if (resetState == QuestState.FINISHED) {
                finish(false)
            } else {
                finish(showTooltip)
            }
        }
    }

    fun finish(showTooltip: Boolean = true) {
        XpRewarder.receivePossibleXp(id)
        possibleSetLastReturnTaskComplete()
        currentState = QuestState.FINISHED
        if (showTooltip) showMessageTooltipQuestCompleted()
    }

    private fun possibleSetLastReturnTaskComplete() {
        tasks.entries
            .sortedBy { it.key }
            .takeLast(1)
            .filter { it.value.type == QuestTaskType.RETURN }
            .filter { !it.value.isComplete }
            .forEach { it.value.setComplete() }
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
            audioManager.handle(AudioCommand.SE_STOP_ALL)
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_REWARD)
            brokerManager.questObservers.notifyShowMessageTooltip("Quest completed:" + System.lineSeparator() + title)
        }
    }

    private fun showMessageTooltipQuestFailed() {
        brokerManager.questObservers.notifyShowMessageTooltip("Quest failed:" + System.lineSeparator() + title)
    }

}
