package nl.t64.cot.components.quest

import nl.t64.cot.Utils.gameData


class QuestTask(
    var taskPhrase: String = "",
    val updatedPhrase: String? = null,
    val type: QuestTaskType = QuestTaskType.NONE,
    val target: Map<String, Int> = emptyMap(),
    val isOptional: Boolean = false,
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
            isComplete -> "v  $taskPhrase"
            else -> "     $taskPhrase"
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
            QuestTaskType.KILL -> completeTask()
            QuestTaskType.GIVE_ITEM -> {
                removeTargetFromInventory()
                completeTask()
            }
            else -> throw IllegalArgumentException("Only some types are completable this way for now.")
        }
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
        updatedPhrase?.let { taskPhrase = it }
        isComplete = true
    }

    private fun removeTargetFromInventory() {
        gameData.inventory.autoRemoveItem(getTargetEntry().key, getTargetEntry().value)
    }

    private fun getTargetEntry(): Map.Entry<String, Int> {
        return target.iterator().next()
    }

}
