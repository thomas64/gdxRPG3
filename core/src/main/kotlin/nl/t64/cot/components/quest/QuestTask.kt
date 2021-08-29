package nl.t64.cot.components.quest

import nl.t64.cot.Utils.gameData


class QuestTask(
    val taskPhrase: String = "",
    val type: QuestTaskType = QuestTaskType.NONE,
    val target: Map<String, Int> = emptyMap(),
    val isOptional: Boolean = false,
    val isAnyOf: Boolean = false,
    var isHidden: Boolean = false,
    val linkedWith: String? = null

) {
    var isComplete: Boolean = false
    var isFailed: Boolean = false
    var isQuestFinished: Boolean = false

    override fun toString(): String {
        return when {
            type == QuestTaskType.NONE -> System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + taskPhrase
            isFailed -> "x  $taskPhrase"
            isQuestFinished -> "v  $taskPhrase"
            type == QuestTaskType.RETURN -> "     $taskPhrase"
            isCompleteForReturn() -> "v  $taskPhrase"
            else -> "     $taskPhrase"
        }
    }

    fun forceFinished() {
        isQuestFinished = true
        if (isOptional && !isComplete) {
            isFailed = true
        }
    }

    fun setComplete() {
        when (type) {
            QuestTaskType.ITEM_DELIVERY -> {
                target.forEach { (itemId, amount) -> gameData.inventory.autoRemoveItem(itemId, amount) }
                isComplete = true
            }
            QuestTaskType.SHOW_ITEM,
            QuestTaskType.DISCOVER,
            QuestTaskType.MESSAGE_DELIVERY -> isComplete = true
            QuestTaskType.CHECK -> setCheckTypePossibleComplete()
            else -> throw IllegalArgumentException("Only possible to complete a DISCOVER, CHECK or DELIVERY task.")
        }
    }

    private fun setCheckTypePossibleComplete() {
        if (target.isEmpty()) {
            isComplete = true
            return
        }

        if (doesInventoryContainsTarget()) {
            isComplete = true
        }
    }

    fun removeTargetFromInventory() {
        gameData.inventory.autoRemoveItem(getTargetEntry().key, getTargetEntry().value)
    }

    fun hasTargetInInventory(): Boolean {
        return gameData.inventory.contains(target)
    }

    fun isCompleteForReturn(): Boolean {
        return when (type) {
            QuestTaskType.RETURN -> true
            QuestTaskType.FIND_ITEM,
            QuestTaskType.FETCH_ITEM -> doesInventoryContainsTarget()
            QuestTaskType.DISCOVER,
            QuestTaskType.CHECK,
            QuestTaskType.KILL, // temp for now
            QuestTaskType.MESSAGE_DELIVERY,
            QuestTaskType.SHOW_ITEM, // test temp?
            QuestTaskType.ITEM_DELIVERY -> isComplete
            else -> throw IllegalArgumentException("No '$type' task for now.")
        }
    }

    private fun doesInventoryContainsTarget(): Boolean {
        return gameData.inventory.hasEnoughOfItem(getTargetEntry().key, getTargetEntry().value)
    }

    private fun getTargetEntry(): Map.Entry<String, Int> {
        return target.entries.iterator().next()
    }

}
