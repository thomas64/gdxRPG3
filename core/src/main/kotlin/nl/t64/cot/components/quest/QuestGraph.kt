package nl.t64.cot.components.quest

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.worldScreen
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
    var isHidden: Boolean = false,
    val shouldHideMessagesAfterAndIncludingFinished: Boolean = false,
    val shouldHideMessagesAfterFinished: Boolean = false,
    val isResettable: Boolean = true,
    val linkedWith: List<String> = emptyList(),
    val tasks: Map<String, QuestTask> = emptyMap()
) {
    var currentState: QuestState = QuestState.UNKNOWN
    var resetState: QuestState = QuestState.UNKNOWN
    var isFailed: Boolean = false
    var wasFailed: Boolean = false
    var isHiddenInQuestLog: Boolean = false

    private val titleWithoutPrefix: String = title.removeSuffix(" [M]")

    override fun toString(): String {
        return when {
            isFailed -> "[FIREBRICK]x    $title"
            currentState == QuestState.UNCLAIMED -> "o    $title"
            currentState == QuestState.FINISHED -> "[GRAY]v    $title"
            resetState == QuestState.UNCLAIMED -> "o    $title"
            resetState == QuestState.FINISHED && !isResettable -> "[GRAY]v    $title"
            resetState == QuestState.FINISHED -> "[GRAY]r    $title"
            else -> "      $title"
        }
    }

    fun isOneOfBothStatesEqualOrHigherThan(questState: QuestState): Boolean {
        return resetState.isEqualOrHigherThan(questState) || currentState.isEqualOrHigherThan(questState)
    }

    fun getAllQuestTasksForVisual(): Array<QuestTask> {
        return (tasks + getTasksOfAcceptedSubQuests() + getSpecificHiddenTasksOfAcceptedSubQuests())
            .toSortedMap(compareBy<String> { it.length }.thenBy { it })
            .map { it.value }
            .filterNot { it.isHidden }
            .toTypedArray()
    }

    private fun getTasksOfAcceptedSubQuests(): Map<String, QuestTask> {
        return linkedWith
            .map { gameData.quests.getQuestById(it) }
            .filter { isSubQuestAcceptedOrNotFinishedInThePast(it) }
            .map { it.tasks }
            .flatMap { it.toList() }
            .toMap()
            .filterValues { !it.shouldHideAfterReset }
    }

    private fun isSubQuestAcceptedOrNotFinishedInThePast(subQuest: QuestGraph): Boolean {
        if (subQuest.currentState.isEqualOrHigherThan(QuestState.ACCEPTED)) {
            return true
        }
        return subQuest.resetState.isEqualOrHigherThan(QuestState.ACCEPTED)
            && subQuest.resetState != QuestState.FINISHED
    }

    private fun getSpecificHiddenTasksOfAcceptedSubQuests(): Map<String, QuestTask> {
        return linkedWith
            .map { gameData.quests.getQuestById(it) }
            .filter { it.currentState.isEqualOrHigherThan(QuestState.ACCEPTED) }
            .map { it.tasks }
            .flatMap { it.toList() }
            .toMap()
            .filterValues { it.shouldHideAfterReset }
    }

    fun reset() {
        if (resetState.isLowerThan(currentState)) {
            resetState = currentState
        }
        wasFailed = isFailed
        isFailed = false
        currentState = QuestState.UNKNOWN
        tasks.values.forEach { it.possibleReset() }
    }

    fun know() {
        if (currentState == QuestState.UNKNOWN) {
            currentState = QuestState.KNOWN
            isHiddenInQuestLog = false
        }
    }

    fun accept() {
        if (currentState.isEqualOrLowerThan(QuestState.KNOWN)) {
            setCompleteTasksComplete()
            setAcceptedAndPossiblyShowMessage()
        }
        possibleFinish(true)
    }

    fun fail() {
        failAndAlsoParents()
    }

    private fun setAcceptedAndPossiblyShowMessage() {
        currentState = QuestState.ACCEPTED
        isHiddenInQuestLog = false
        if (isSubQuest) {
            showMessageTooltipQuestUpdated()
        } else {
            showMessageTooltipQuestNew()
        }
    }

    fun unclaim() {
        isHiddenInQuestLog = shouldRemainHiddenInQuestLog()
        currentState = QuestState.UNCLAIMED
    }

    fun forceSetAllTasksComplete() {
        tasks.keys.forEach { setTaskComplete(it, false) }
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
                .filterValues { conversationId in it.conversationIds }
                .filterValues { !it.isComplete }
                .filterValues { it.hasTargetInInventory() }
                .forEach { setTaskComplete(it.key) }
        }
    }

    fun possibleSetDeliverItemAlternateTaskComplete(conversationId: String) {
        if (currentState.isEqualOrLowerThan(QuestState.ACCEPTED)) {
            tasks.filterValues { it.type == QuestTaskType.DELIVER_ITEM }
                .filterValues { conversationId in it.conversationIds }
                .filterValues { !it.isComplete }
                .filterValues { it.targetAlternate.isNotEmpty() }
                .filterValues { it.hasTargetAlternateInInventory() }
                .onEach { it.value.isTargetAlternateUsed = true }
                .forEach { setTaskComplete(it.key) }
        }
    }

    fun possibleSetDeliverMessageTaskComplete(conversationId: String) {
        if (currentState.isEqualOrLowerThan(QuestState.ACCEPTED)) {
            tasks.filterValues { it.type == QuestTaskType.DELIVER_MESSAGE }
                .filterValues { conversationId in it.conversationIds }
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

    fun setTaskFailed(taskId: String) {
        val questTask = tasks[taskId]!!
        questTask.isFailed = true
        if (isOneOfBothStatesEqualOrHigherThan(QuestState.ACCEPTED)) {
            failAndAlsoParents()
        }
    }

    fun hasTaskWithConversationId(conversationId: String): Boolean {
        return tasks.values.any { conversationId in it.conversationIds }
    }

    fun forceUnhideAllOptionalTasks() {
        tasks.filterValues { it.isOptional }
            .filterValues { it.isHidden }
            .forEach { it.value.isHidden = false }
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

    fun isTaskFailed(taskId: String): Boolean {
        return tasks[taskId]?.isFailed ?: false
    }

    fun isTaskComplete(taskId: String): Boolean {
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
        val shouldHideInQuestLog = shouldRemainHiddenInQuestLog()

        currentState = QuestState.FINISHED
        isHiddenInQuestLog = shouldHideInQuestLog
        if (!shouldHideInQuestLog && showTooltip) {
            showMessageTooltipQuestCompleted()
        }
    }

    private fun shouldRemainHiddenInQuestLog(): Boolean {
        return isHiddenInQuestLog
            || (resetState == QuestState.UNKNOWN && currentState == QuestState.UNKNOWN)

        // H = isHiddenInQuestLog
        // U = (resetState == UNKNOWN && currentState == UNKNOWN)
        // H=false, U=false => false
        // H=false, U=true => true
        // H=true, U=false => true
        // H=true, U=true => true
        // Only the first case results in false.
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

    private fun failAndAlsoParents() {
        showMessageTooltipQuestFailed()
        isFailed = true
        if (isSubQuest) {
            gameData.quests.getParentsOf(id).forEach { it.isFailed = true }
        }
    }

    private fun showMessageTooltipQuestNew() {
        if (shouldShowMessage()
            && !isHidden
            && resetState == QuestState.UNKNOWN
        ) {
            worldScreen.showMessageTooltip("New quest:" + System.lineSeparator() + titleWithoutPrefix)
        }
    }

    private fun showMessageTooltipQuestUpdated() {
        if (shouldShowMessage()
            && !isHidden
            && (currentState == QuestState.ACCEPTED || resetState == QuestState.ACCEPTED)
            && (!isReadyToBeFinished() || (isSubQuest && isReadyToBeFinished()))
        ) {
            worldScreen.showMessageTooltip("Quest updated:" + System.lineSeparator() + titleWithoutPrefix)
        }
    }

    private fun showMessageTooltipQuestCompleted() {
        if (shouldShowMessage()
            && !isHidden
            && !isSubQuest
        ) {
            stopAllSe()
            playSe(AudioEvent.SE_REWARD)
            worldScreen.showMessageTooltip("Quest completed:" + System.lineSeparator() + titleWithoutPrefix)
        }
    }

    private fun showMessageTooltipQuestFailed() {
        if (shouldShowMessage()
            && !isFailed
            && isOneOfBothStatesEqualOrHigherThan(QuestState.KNOWN)
        ) {
            stopAllSe()
            playSe(AudioEvent.SE_QUEST_FAIL)
            worldScreen.showMessageTooltip("Quest failed:" + System.lineSeparator() + titleWithoutPrefix)
        }
    }

    private fun shouldShowMessage(): Boolean {
        if (shouldHideMessagesAfterAndIncludingFinished
            && isOneOfBothStatesEqualOrHigherThan(QuestState.FINISHED)
        ) {
            return false
        }
        if (shouldHideMessagesAfterFinished
            && resetState == QuestState.FINISHED
        ) {
            return false
        }
        return true
    }
}
