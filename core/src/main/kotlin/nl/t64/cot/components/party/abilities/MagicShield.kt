package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.Participant


class MagicShield(
    abilityItem: AbilityItem,
    attacker: Participant
) : BuffAbilityItem(
    abilityItem,
    attacker
) {

    override fun isBuffActive(): Boolean {
        return target.character.bonus.protectionFromSpell > 0
    }

    override fun getBuffDescription(): String {
        return "+${calculateBonusValue()} Protection"
    }

    override fun getCastMessage(bonusValue: Int): String {
        return "+$bonusValue Prt"
    }

    override fun applyBuff(bonusValue: Int) {
        target.character.bonus.protectionFromSpell = bonusValue
    }

    override fun calculateBonusValue(): Int {
        return super.calculateBonusValue() * 2
    }

}
