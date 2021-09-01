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
            QuestTaskType.SAY_THE_RIGHT_THING,
            QuestTaskType.KILL -> {
                updatedPhrase?.let { taskPhrase = it }
                isComplete = true
            }
            else -> throw IllegalArgumentException("Only some types are completable this way for now.")
        }
    }

    fun hasTargetInInventory(): Boolean {
        return gameData.inventory.contains(target)
    }

    private fun removeTargetFromInventory() {
        gameData.inventory.autoRemoveItem(getTargetEntry().key, getTargetEntry().value)
    }

    private fun getTargetEntry(): Map.Entry<String, Int> {
        return target.entries.iterator().next()
    }

}
