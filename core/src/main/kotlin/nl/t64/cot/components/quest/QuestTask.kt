package nl.t64.cot.components.quest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.audio.stopAllSe
import nl.t64.cot.components.party.inventory.InventoryDatabase


data class QuestTaskProgress(
    val isHidden: Boolean = false,
    val isReset: Boolean = false,
    val isComplete: Boolean = false,
    val isFailed: Boolean = false,
    val isTargetAlternateUsed: Boolean = false,
    val isPhraseUpdated: Boolean = false
)

@JsonIgnoreProperties(ignoreUnknown = true)
class QuestTask(
    var taskPhrase: String = "",
    private val updatedPhrase: String? = null,
    val type: QuestTaskType = QuestTaskType.NONE,
    val target: Map<String, Int> = emptyMap(),
    val targetAlternate: Map<String, Int> = emptyMap(),
    val receive: Map<String, Int> = emptyMap(),
    val conversationIds: List<String> = emptyList(),
    val shouldHideAfterReset: Boolean = false,
    val forceSetHiddenAfterReset: Boolean = false,
    val isOptional: Boolean = false,
    var isHidden: Boolean = false,
    private val isResettable: Boolean = true,
    private val isRepeatable: Boolean = false,
    private val hasRewardSound: Boolean = false,
    val linkedWith: List<String> = emptyList()
) {
    var isReset: Boolean = false
    var isComplete: Boolean = false
    var isFailed: Boolean = false
    var isTargetAlternateUsed: Boolean = false
    private val initialIsHidden: Boolean = isHidden


    fun isChanged(): Boolean {
        return toProgress() != QuestTaskProgress(isHidden = initialIsHidden)
    }

    fun toProgress(): QuestTaskProgress {
        return QuestTaskProgress(isHidden = isHidden,
                                 isReset = isReset,
                                 isComplete = isComplete,
                                 isFailed = isFailed,
                                 isTargetAlternateUsed = isTargetAlternateUsed,
                                 isPhraseUpdated = taskPhrase == updatedPhrase)
    }

    fun applyProgress(progress: QuestTaskProgress) {
        isHidden = progress.isHidden
        isReset = progress.isReset
        isComplete = progress.isComplete
        isFailed = progress.isFailed
        isTargetAlternateUsed = progress.isTargetAlternateUsed
        if (progress.isPhraseUpdated) {
            updatedPhrase?.let { taskPhrase = it }
        }
    }

    override fun toString(): String {
        return when {
            type == QuestTaskType.NONE -> System.lineSeparator() + System.lineSeparator() + System.lineSeparator() + taskPhrase
            isFailed -> "[FIREBRICK]x    $taskPhrase[BLACK]"
            isComplete -> "v    $taskPhrase"
            isReset -> "r    $taskPhrase"
            else -> "      $taskPhrase${getFindItemProgressForQuestLog()}"
        }
    }

    fun possibleReset() {
        if (forceSetHiddenAfterReset) {
            isHidden = true
        }

        isFailed = false
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
            QuestTaskType.DELIVER_ITEM_TO_SHOW,
            QuestTaskType.DELIVER_MESSAGE,
            QuestTaskType.KILL,
            QuestTaskType.MEET_PERSON,
            QuestTaskType.TALK_TO_PERSON,
            QuestTaskType.RECEIVE_ABILITY,
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

    fun getFindItemProgressForTooltip(): String? {
        return getCountableFindItemTargets()
            .map { (itemId, requiredAmount) -> "${getPluralName(itemId)}: ${getProgress(itemId, requiredAmount)}" }
            .takeIf { it.isNotEmpty() }
            ?.joinToString()
    }

    private fun getFindItemProgressForQuestLog(): String {
        return getCountableFindItemTargets()
            .map { (itemId, requiredAmount) -> getProgress(itemId, requiredAmount) }
            .takeIf { it.isNotEmpty() }
            ?.joinToString(prefix = " (", postfix = ")")
            .orEmpty()
    }

    private fun getCountableFindItemTargets(): Map<String, Int> {
        if (type != QuestTaskType.FIND_ITEM) return emptyMap()
        val minimumAmountToCount = 2
        return target.filterValues { it >= minimumAmountToCount }
    }

    private fun getProgress(itemId: String, requiredAmount: Int): String {
        val amountFound: Int = gameData.inventory.getTotalOfItemIncludingPartyEquipment(itemId)
        return "${amountFound.coerceAtMost(requiredAmount)}/$requiredAmount"
    }

    private fun getPluralName(itemId: String): String {
        val name: String = InventoryDatabase.createInventoryItem(itemId).name
        return when {
            name.endsWith("y") -> name.dropLast(1) + "ies"
            name.endsWith("s") -> name
            else -> name + "s"
        }
    }

}
