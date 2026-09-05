package nl.t64.cot.components.battle

import nl.t64.cot.components.party.abilities.AbilityItemId
import nl.t64.cot.components.party.abilities.calculatePerformBonus
import nl.t64.cot.components.party.abilities.calculatePerformPenalty
import nl.t64.cot.components.party.skills.SkillItemId


class TroubadourEffectHandler(
    private val participants: List<Participant>
) {

    fun possibleApply() {
        val performer: Participant = participants.firstOrNull { it.isPerforming } ?: return
        val performance: AbilityItemId = performer.performingType!!
        val skillRank: Int = performer.character.getCalculatedTotalSkillOf(SkillItemId.TROUBADOUR)

        when (performance) {
            AbilityItemId.PERFORM_BEAUTY -> participants
                .filter { it.isHero }
                .filterNot { it == performer }
                .forEach {
                    it.character.bonus.hitBonusFromTroubadour =
                        calculatePerformBonus(it.character.getCalculatedTotalHit(), skillRank)
                }

            AbilityItemId.PERFORM_CHAOS -> participants
                .filterNot { it.isHero }
                .forEach {
                    it.character.bonus.hitPenaltyFromTroubadour =
                        calculatePerformPenalty(it.character.getCalculatedTotalHit(), skillRank)
                }

            else -> throw IllegalStateException("Unknown performing AbilityItemId: $performance")
        }
    }

    fun removeFromAllParticipants() {
        participants.forEach {
            it.character.bonus.hitBonusFromTroubadour = 0
            it.character.bonus.hitPenaltyFromTroubadour = 0
        }
    }

}
