package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.Participant
import nl.t64.cot.components.party.skills.SkillItemId


class LayOnHands(
    abilityItem: AbilityItem,
    attacker: Participant
) : HealingAbilityItem(
    abilityItem,
    attacker
) {

    override fun createPreviewMessage(): String {
        return listOf(
            "Lay hands on ${target.character.name} to",
            "restore ${calculateMaxHealPoints()} HP of ${target.character.gender} lost health?"
        ).alignCenter()
    }

    override fun calculateMaxHealPoints(): Int {
        return 10 * attacker.character.getCalculatedTotalSkillOf(SkillItemId.HEALER)
    }

}
