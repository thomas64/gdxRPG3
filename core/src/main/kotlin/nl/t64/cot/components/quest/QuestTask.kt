package nl.t64.cot.components.quest

import nl.t64.cot.Utils.gameData


class QuestTask(
    val taskPhrase: String = "",
    val type: QuestType = QuestType.NONE,
    val target: Map<String, Int> = emptyMap(),
    val isOptional: Boolean = false,
) {
    var isComplete: Boolean = false
    var isFailed: Boolean = false
    var isQuestFinished: Boolean = false

    override fun toString(): String {
        return when {
            type == QuestType.NONE -> System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + taskPhrase
            isFailed -> "x  $taskPhrase"
            isQuestFinished -> "v  $taskPhrase"
            type == QuestType.RETURN -> "     $taskPhrase"
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
            QuestType.ITEM_DELIVERY -> {
                target.forEach { (itemId, amount) -> gameData.inventory.autoRemoveItem(itemId, amount) }
                isComplete = true
            }
            QuestType.DISCOVER,
            QuestType.MESSAGE_DELIVERY -> isComplete = true
            QuestType.CHECK -> setCheckTypePossibleComplete()
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
            QuestType.RETURN -> true
            QuestType.FETCH_ITEM -> doesInventoryContainsTarget()
            QuestType.DISCOVER,
            QuestType.CHECK,
            QuestType.MESSAGE_DELIVERY,
            QuestType.ITEM_DELIVERY -> isComplete
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
