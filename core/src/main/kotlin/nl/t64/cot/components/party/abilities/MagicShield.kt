package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.Participant
import kotlin.math.ln


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
        val base: Float = super.calculateBonusValue().toFloat()
        val normalizedLog10: Float = ln(base) / ln(10f) // 0.0 bij 1, 1.0 bij 10
        return (4f + normalizedLog10 * 16f).toInt() // map naar [4,20]
    }

}
