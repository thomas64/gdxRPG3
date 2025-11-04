package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.Participant


class Clumsiness(
    abilityItem: AbilityItem,
    attacker: Participant
) : BuffAbilityItem(
    abilityItem,
    attacker
) {

    override fun isBuffActive(): Boolean {
        return target.character.bonus.dexterityFromSpell < 0
    }

    override fun getBuffDescription(): String {
        return "-${calculateBonusValue()} Dexterity"
    }

    override fun getCastMessage(bonusValue: Int): String {
        return "-$bonusValue Dex"
    }

    override fun applyBuff(bonusValue: Int) {
        target.character.bonus.dexterityFromSpell = -bonusValue
    }

}
