package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.Participant


class Might(
    abilityItem: AbilityItem,
    attacker: Participant
) : BuffAbilityItem(
    abilityItem,
    attacker
) {

    override fun isBuffActive(): Boolean {
        return target.character.bonus.strengthFromSpell > 0
    }

    override fun getBuffDescription(): String {
        return "+${calculateBonusValue()} Strength"
    }

    override fun getCastMessage(bonusValue: Int): String {
        return "+$bonusValue Str"
    }

    override fun applyBuff(bonusValue: Int) {
        target.character.bonus.strengthFromSpell = bonusValue
    }

}
