package nl.t64.cot.components.battle

import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.components.party.abilities.BuffAbilityItem
import nl.t64.cot.components.party.abilities.HealingAbilityItem
import nl.t64.cot.components.party.abilities.PerformingAbilityItem
import nl.t64.cot.screens.battle.BattleUtils


class SpecialAction(
    currentParticipant: Participant,
    currentTarget: Participant,
    selectedSpecial: BattleAbilityItem
) : AttackAction(currentParticipant, currentTarget, selectedSpecial) {

    fun isAlreadyCast(): String? {
        return when (selectedAbility) {
            is BuffAbilityItem -> if (selectedAbility.isBuffActive()) createAlreadyCastMessage() else null
            is HealingAbilityItem -> null

            is PerformingAbilityItem -> {
                val areOthersPerforming: Boolean = BattleUtils.getAllParticipants()
                    .filterNot { it == currentParticipant }
                    .any { it.isPerforming }

                if (areOthersPerforming) {
                    val previewMessage: String = createPreviewMessage()
                    val warningMessage = "[FIREBRICK]Only one performance is allowed."
                    val underscores: String = createUnderscoresWithLengthOf(previewMessage, warningMessage)
                    """ |$previewMessage
                        |$underscores
                        |
                        |$warningMessage""".trimIndent().trimMargin()
                } else null
            }

            else -> throw IllegalArgumentException("SpecialAction does not support ${selectedAbility.name}.")
        }
    }

    private fun createAlreadyCastMessage(): String {
        val previewMessage: String = createPreviewMessage()
        val warningMessage1 = "[FIREBRICK]${selectedAbility.name} is already"
        val warningMessage2 = "cast on ${target.name}!"
        val underscores: String = createUnderscoresWithLengthOf(previewMessage, warningMessage1, warningMessage2)
        return """ |$previewMessage
                   |$underscores
                   |
                   |$warningMessage1
                   |$warningMessage2""".trimIndent().trimMargin()
    }

    override fun createCommand(): String {
        return when (selectedAbility) {
            is HealingAbilityItem -> "Heal"
            is PerformingAbilityItem -> "Perform"
            else -> "Cast"
        }
    }

}
