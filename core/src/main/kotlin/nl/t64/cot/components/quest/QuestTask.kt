package nl.t64.cot.components.quest

import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.audio.stopAllSe


class QuestTask(
    var taskPhrase: String = "",
    private val updatedPhrase: String? = null,
    val type: QuestTaskType = QuestTaskType.NONE,
    val target: MutableMap<String, Int> = mutableMapOf(),
    val targetAlternate: Map<String, Int> = emptyMap(),
    val receive: Map<String, Int> = emptyMap(),
    val conversationIds: List<String> = emptyList(),
    val isOptional: Boolean = false,
    var isHidden: Boolean = false,
    private val isResettable: Boolean = true,
    private var isRepeatable: Boolean = false,
    private var hasRewardSound: Boolean = false,
    val linkedWith: List<String> = emptyList()
) {
    var isReset: Boolean = false
    var isComplete: Boolean = false
    var isFailed: Boolean = false
    var isQuestFinished: Boolean = false
    var isTargetAlternateUsed: Boolean = false

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
        if (isComplete && isResettable()) {
            isReset = true
            isComplete = false
            isTargetAlternateUsed = false
        }
    }

    private fun isResettable(): Boolean {
        if (!isResettable) return false

        return when (type) {
            QuestTaskType.COMPLETE,
            QuestTaskType.DISCOVER,
            QuestTaskType.CHECK,
            QuestTaskType.CHECK_WITH_ITEM -> false
            QuestTaskType.FIND_ITEM -> !hasTargetInInventoryOrEquipment()
            else -> true
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
            QuestTaskType.COMPLETE,
            QuestTaskType.FREE,
            QuestTaskType.DISCOVER,
            QuestTaskType.CHECK,
            QuestTaskType.CHECK_WITH_ITEM,
            QuestTaskType.FIND_ITEM,
            QuestTaskType.SHOW_ITEM,
            QuestTaskType.WEAR_ITEM,
            QuestTaskType.SAY_THE_RIGHT_THING,
            QuestTaskType.DELIVER_MESSAGE,
            QuestTaskType.KILL,
            QuestTaskType.RETURN -> completeTask()
            QuestTaskType.DELIVER_ITEM,
            QuestTaskType.TRADE_ITEMS,
            QuestTaskType.PROVIDE_ITEM -> {
                removeTargetOrTargetAlternateFromInventory()
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

    fun hasTargetAlternateInInventory(): Boolean {
        return gameData.inventory.contains(targetAlternate)
    }

    fun hasTargetInPlayerEquipment(): Boolean {
        return gameData.party.getPlayer().hasInventoryItem(getTargetEntry().key)
    }

    private fun completeTask() {
        if (!isRepeatable) {
            updatedPhrase?.let { taskPhrase = it }
            isComplete = true
            if (hasRewardSound) {
                stopAllSe()
                playSe(AudioEvent.SE_REWARD)
            }
        }
    }

    private fun removeTargetOrTargetAlternateFromInventory() {
        if (isTargetAlternateUsed) {
            gameData.inventory.autoRemoveItems(targetAlternate)
        } else {
            gameData.inventory.autoRemoveItems(target)
        }
    }

    private fun getTargetEntry(): Map.Entry<String, Int> {
        return target.iterator().next()
    }

}
