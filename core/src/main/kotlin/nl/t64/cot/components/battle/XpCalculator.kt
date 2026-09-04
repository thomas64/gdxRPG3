package nl.t64.cot.components.battle

import kotlin.math.roundToInt


/**
 * How much xp an enemy is worth per 1000 combat power ([CombatPowerCalculator]).
 * This is the global xp tap: raise the value to hand out more xp everywhere, lower it for less.
 */
private const val XP_PER_1000_COMBAT_POWER: Float = 40f

class XpCalculator {

    fun calculate(combatPower: Float): Int {
        return (combatPower * XP_PER_1000_COMBAT_POWER / 1000f).roundToInt().coerceAtLeast(1)
    }

}
