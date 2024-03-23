package nl.t64.cot.components.quest

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.audio.stopAllSe
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
            isFailed -> "x    $title"
            currentState == QuestState.FINISHED -> "v    $title"
            currentState == QuestState.UNCLAIMED -> "o    $title"
            resetState == QuestState.FINISHED -> "r    $title"
            else -> "      $title"
        }
    }

    fun isOneOfBothStatesEqualOrHigherThan(questState: QuestState): Boolean =
        resetState.isEqualOrHigherThan(questState) || currentState.isEqualOrHigherThan(questState)

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
            .filter { it.currentState.isEqualOrHigherThan(QuestState.ACCEPTED) }
            .map { it.tasks }
            .flatMap { it.toList() }
            .toMap()
    }

    fun reset() {
        if (resetState.isLowerThan(currentState)) {
            resetState = currentState
        }
        currentState = QuestState.UNKNOWN
        tasks.values.forEach { it.possibleReset() }
    }

    fun know() {
        if (currentState == QuestState.UNKNOWN) {
            currentState = QuestState.KNOWN
        }
    }

    fun accept() {
        if (currentState.isEqualOrLowerThan(QuestState.KNOWN)) {
            setCompleteTasksComplete()
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

    private fun setCompleteTasksComplete() {
        tasks.filterValues { it.type == QuestTaskType.COMPLETE }
            .forEach { setTaskComplete(it.key) }
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

    fun possibleSetProvideItemTaskComplete() {
        accept()
        tasks.toSortedMap(compareBy<String> { it.length }.thenBy { it })
            .filterValues { it.type == QuestTaskType.PROVIDE_ITEM }
            .filterValues { !it.isComplete }
            .filterValues { it.hasTargetInInventory() }
            .entries
            .first()
            .let { setTaskComplete(it.key) }
    }

    fun possibleSetTradeItemsTaskComplete(): Loot {
        accept()
        return tasks
            .filterValues { it.type == QuestTaskType.TRADE_ITEMS }
            .filterValues { it.hasTargetInInventory() }
            .onEach { setTaskComplete(it.key) }
            .map { Loot(it.value.receive.toMutableMap()) }
            .single()
    }

    fun setSayTheRightThingTaskCompleteAndReceivePossibleTarget(): Loot {
        accept()
        return tasks
            .filterValues { it.type == QuestTaskType.SAY_THE_RIGHT_THING }
            .filterValues { !it.isComplete }
            .onEach { setTaskComplete(it.key) }
            .map { it.value.target }
            .flatMap { it.toList() }
            .toMap()
            .toMutableMap()
            .let { Loot(it) }

    }

    fun receiveItemsForQuest(): Loot {
        return tasks.values
            .filter { it.type in listOf(QuestTaskType.DELIVER_ITEM, QuestTaskType.CHECK_WITH_ITEM) }
            .map { it.target }
            .flatMap { it.toList() }
            .toMap()
            .toMutableMap()
            .let { Loot(it) }
    }

    fun possibleSetDeliverItemTaskComplete(conversationId: String) {
        if (currentState.isEqualOrLowerThan(QuestState.ACCEPTED)) {
            tasks.filterValues { it.type == QuestTaskType.DELIVER_ITEM }
                .filterValues { it.conversationId == conversationId }
                .filterValues { !it.isComplete }
                .filterValues { it.hasTargetInInventory() }
                .forEach { setTaskComplete(it.key) }
        }
    }

    fun possibleSetDeliverItemAlternateTaskComplete(conversationId: String) {
        if (currentState.isEqualOrLowerThan(QuestState.ACCEPTED)) {
            tasks.filterValues { it.type == QuestTaskType.DELIVER_ITEM }
                .filterValues { it.conversationId == conversationId }
                .filterValues { !it.isComplete }
                .filterValues { it.targetAlternate.isNotEmpty() }
                .onEach { it.value.updateTargetToAlternate() }
                .filterValues { it.hasTargetInInventory() }
                .forEach { setTaskComplete(it.key) }
        }
    }

    fun possibleSetDeliverMessageTaskComplete(conversationId: String) {
        if (currentState.isEqualOrLowerThan(QuestState.ACCEPTED)) {
            tasks.filterValues { it.type == QuestTaskType.DELIVER_MESSAGE }
                .filterValues { it.conversationId == conversationId }
                .forEach { setTaskComplete(it.key) }
        }
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
            if (showTooltip && !questTask.isReset) showMessageTooltipQuestUpdated()
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
        return isOneOfBothStatesEqualOrHigherThan(QuestState.ACCEPTED)
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
            .forEach {
                it.setComplete()
                unhideTaskWithLinkedTask(it)
            }
    }

    private fun areAllQuestTasksComplete(): Boolean {
        return tasks.values
            .filter { !it.isOptional }
            .all { it.isComplete }
    }

    private fun showMessageTooltipQuestNew() {
        if (!isHidden && resetState == QuestState.UNKNOWN) {
            screenManager.getWorldScreen().showMessageTooltip("New quest:" + System.lineSeparator() + title)
        }
    }

    private fun showMessageTooltipQuestUpdated() {
        if (!isHidden
            && (currentState == QuestState.ACCEPTED || resetState == QuestState.ACCEPTED)
            && (!isReadyToBeFinished() || (isSubQuest && isReadyToBeFinished()))
        ) {
            screenManager.getWorldScreen().showMessageTooltip("Quest updated:" + System.lineSeparator() + title)
        }
    }

    private fun showMessageTooltipQuestCompleted() {
        if (!isHidden && !isSubQuest) {
            stopAllSe()
            playSe(AudioEvent.SE_REWARD)
            screenManager.getWorldScreen().showMessageTooltip("Quest completed:" + System.lineSeparator() + title)
        }
    }

    private fun showMessageTooltipQuestFailed() {
        screenManager.getWorldScreen().showMessageTooltip("Quest failed:" + System.lineSeparator() + title)
    }

}
