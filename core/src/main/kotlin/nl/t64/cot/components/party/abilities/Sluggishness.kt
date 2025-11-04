package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.Participant


class Sluggishness(
    abilityItem: AbilityItem,
    attacker: Participant
) : BuffAbilityItem(
    abilityItem,
    attacker
) {

    override fun isBuffActive(): Boolean {
        return target.character.bonus.speedFromSpell < 0
    }

    override fun getBuffDescription(): String {
        return "-${calculateBonusValue()} Speed"
    }

    override fun getCastMessage(bonusValue: Int): String {
        return "-$bonusValue Spd"
    }

    override fun applyBuff(bonusValue: Int) {
        target.character.bonus.speedFromSpell = -bonusValue
    }

}
