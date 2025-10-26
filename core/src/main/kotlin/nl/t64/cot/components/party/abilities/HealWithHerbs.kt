package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.Participant
import nl.t64.cot.components.party.skills.SkillItemId


class HealWithHerbs(
    abilityItem: AbilityItem,
    attacker: Participant
) : HealingAbilityItem(
    abilityItem,
    attacker
) {

    override fun createPreviewMessage(): String {
        return listOf(
            "Heal ${target.character.name} with herbs to",
            "restore ${calculateMaxHealPoints()} HP of ${target.character.gender} lost health?"
        ).alignCenter()
    }

    override fun calculateMaxHealPoints(): Int {
        return 20 * attacker.character.getCalculatedTotalSkillOf(SkillItemId.HEALER)
    }

}
