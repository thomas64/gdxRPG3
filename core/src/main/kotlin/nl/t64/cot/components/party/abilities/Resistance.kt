package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.Participant


class Resistance(
    abilityItem: AbilityItem,
    attacker: Participant
) : BuffAbilityItem(
    abilityItem,
    attacker
) {

    override fun isBuffActive(): Boolean {
        return target.character.bonus.willpowerFromSpell > 0
    }

    override fun getBuffDescription(): String {
        return "+${calculateBonusValue()} Willpower"
    }

    override fun getCastMessage(bonusValue: Int): String {
        return "+$bonusValue Wil"
    }

    override fun applyBuff(bonusValue: Int) {
        target.character.bonus.willpowerFromSpell = bonusValue
    }

}
