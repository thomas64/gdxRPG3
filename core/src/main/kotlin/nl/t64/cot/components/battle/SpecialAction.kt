package nl.t64.cot.components.battle

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.components.party.abilities.HealingAbilityItem
import nl.t64.cot.components.party.abilities.MagicShield
import nl.t64.cot.components.party.abilities.ResourceType


class SpecialAction(
    currentParticipant: Participant,
    currentTarget: Participant,
    selectedSpecial: BattleAbilityItem,
) : AttackAction(currentParticipant, currentTarget, selectedSpecial) {

    private val resource: String = selectedSpecial.abilityItem.requiredResource.title

    fun isAlreadyCast(): String? {
        return when (selectedAbility) {
            is MagicShield -> {
                if (currentTarget.character.bonus.protectionFromSpell > 0) {
                    val previewMessage: String = createPreviewMessage()
                    val warningMessage1 = "[FIREBRICK]${selectedAbility.name} is already"
                    val warningMessage2 = "cast on ${target.name}!"
                    val underscores: String = createUnderscoresWithLengthOf(previewMessage, warningMessage1, warningMessage2)
                    """ |$previewMessage
                        |$underscores
                        |
                        |$warningMessage1
                        |$warningMessage2""".trimIndent().trimMargin()
                } else null
            }
            is HealingAbilityItem -> null
            else -> throw IllegalArgumentException("SpecialAction does not support ${selectedAbility.name}.")
        }
    }

    fun isCostingTooMuchResources(): String? {
        return when {
            !selectedAbility.hasEnoughResources() -> {
                val previewMessage: String = createPreviewMessage()
                val warningMessage = "[FIREBRICK]Not enough ${resource}s!"
                val underscores: String = createUnderscoresWithLengthOf(previewMessage, warningMessage)
                """ |$previewMessage
                    |$underscores
                    |
                    |$warningMessage""".trimIndent().trimMargin()
            }
            else -> null
        }
    }

    override fun createCommand(): String {
        return when (selectedAbility) {
            is HealingAbilityItem -> "Heal"
            else -> "Cast"
        }
    }

    override fun handle(): List<AttackData> {
        currentParticipant.currentAP -= selectedAbility.ap
        instigator.currentSp -= selectedAbility.sp
        if (selectedAbility.abilityItem.requiredResource != ResourceType.NONE) {
            gameData.inventory.autoRemoveItem(resource.lowercase(), 1)
        }

        return selectedAbility.handle()
    }

}
