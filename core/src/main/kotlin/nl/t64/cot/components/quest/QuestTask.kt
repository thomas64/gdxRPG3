package nl.t64.cot.components.quest

import nl.t64.cot.Utils.gameData


class QuestTask(
    var taskPhrase: String = "",
    val updatedPhrase: String? = null,
    val type: QuestTaskType = QuestTaskType.NONE,
    val target: MutableMap<String, Int> = mutableMapOf(),
    val targetAlternate: Map<String, Int> = emptyMap(),
    val receive: Map<String, Int> = emptyMap(),
    val conversationId: String = "",
    val isOptional: Boolean = false,
    var isHidden: Boolean = false,
    var isRepeatable: Boolean = false,
    val linkedWith: List<String> = emptyList()
) {
    var isReset: Boolean = false
    var isComplete: Boolean = false
    var isFailed: Boolean = false
    var isQuestFinished: Boolean = false

    override fun toString(): String {
        return when {
            type == QuestTaskType.NONE -> System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + taskPhrase
            isFailed -> "x    $taskPhrase"
            isComplete -> "v    $taskPhrase"
            isReset -> "r    $taskPhrase"
            else -> "      $taskPhrase"
        }
    }

    fun possibleReset() {
        if (isComplete) {
            isReset = true
            isComplete = false
        }
    }

    fun handleLinked() {
        isHidden = false
        if (type == QuestTaskType.FREE) {
            setComplete()
        }
    }

    fun setComplete() {
        when (type) {
            QuestTaskType.FREE,
            QuestTaskType.DISCOVER,
            QuestTaskType.CHECK,
            QuestTaskType.FIND_ITEM,
            QuestTaskType.SHOW_ITEM,
            QuestTaskType.WEAR_ITEM,
            QuestTaskType.SAY_THE_RIGHT_THING,
            QuestTaskType.DELIVER_MESSAGE,
            QuestTaskType.KILL,
            QuestTaskType.RETURN -> completeTask()
            QuestTaskType.DELIVER_ITEM,
            QuestTaskType.TRADE_ITEMS,
            QuestTaskType.GIVE_ITEM -> {
                removeTargetFromInventory()
                completeTask()
            }
            else -> throw IllegalArgumentException("Only some types are completable this way for now.")
        }
    }

    fun updateTargetToAlternate() {
        target.clear()
        target.putAll(targetAlternate)
    }

    fun hasTargetInInventoryOrEquipment(): Boolean {
        return gameData.inventory.contains(target)
                || gameData.party.hasItemInEquipment(getTargetEntry().key, getTargetEntry().value)
    }

    fun hasTargetInInventory(): Boolean {
        return gameData.inventory.contains(target)
    }

    fun hasTargetInPlayerEquipment(): Boolean {
        return gameData.party.getPlayer().hasInventoryItem(getTargetEntry().key)
    }

    private fun completeTask() {
        if (!isRepeatable) {
            updatedPhrase?.let { taskPhrase = it }
            isComplete = true
        }
    }

    private fun removeTargetFromInventory() {
        gameData.inventory.autoRemoveItem(target)
    }

    private fun getTargetEntry(): Map.Entry<String, Int> {
        return target.iterator().next()
    }

}
