package nl.t64.cot.components.battle

import kotlin.math.roundToInt


/**
 * Hoeveel gevechtskracht ([CombatPowerCalculator]) één xp waard is.
 * Dit is de globale xp-kraan: verlaag de waarde om overal meer xp uit te delen, verhoog hem voor minder.
 */
private const val COMBAT_POWER_PER_XP: Float = 22f

class XpCalculator {

    fun calculate(combatPower: Float): Int {
        return (combatPower / COMBAT_POWER_PER_XP).roundToInt().coerceAtLeast(1)
    }

}
