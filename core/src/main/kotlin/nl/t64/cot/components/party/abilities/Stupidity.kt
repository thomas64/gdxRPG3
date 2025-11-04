package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.Participant


class Stupidity(
    abilityItem: AbilityItem,
    attacker: Participant
) : BuffAbilityItem(
    abilityItem,
    attacker
) {

    override fun isBuffActive(): Boolean {
        return target.character.bonus.intelligenceFromSpell < 0
    }

    override fun getBuffDescription(): String {
        return "-${calculateBonusValue()} Intelligence"
    }

    override fun getCastMessage(bonusValue: Int): String {
        return "-$bonusValue Int"
    }

    override fun applyBuff(bonusValue: Int) {
        target.character.bonus.intelligenceFromSpell = -bonusValue
    }

}
