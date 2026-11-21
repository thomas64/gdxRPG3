package nl.t64.cot.components.battle

import nl.t64.cot.components.party.abilities.Target
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.stats.StatItemId
import kotlin.math.pow
import kotlin.math.sqrt


private const val AP_WEIGHT: Float = 1.6f       // > 1: more than linear, many attacks in one turn can kill at once
private const val SPEED_WEIGHT: Float = 2.75f   // > 1: more than linear, a fast character acts earlier and more often

// A fixed reference point, not an average to keep up to date: leave it as is when new enemies are added.
// The weights only count the distance to this point, so changing a weight does not change the overall scale
// of the combat power, and the xp in XpCalculator does not have to be tuned again.
// Only characters far from this point, like very fast bosses, get noticeably stronger or weaker.
private const val REFERENCE_ACTION_POINTS: Float = 5f
private const val REFERENCE_TURN_FREQUENCY: Float = 26f     // the turn counter gain of a character with a speed of 16

class CombatPowerCalculator {

    fun calculate(character: Character): Float {
        val effectiveHp: Float = character.maximumHp.coerceAtLeast(1) * character.getSurvivabilityMultiplier()
        val damage: Int = if (character.isUnarmed) {
            6       // placeholder for minimal damage
        } else {
            character.getCalculatedTotalDamage().coerceAtLeast(1)
        }
        val actionPoints: Float = character.getCalculatedActionPoints().coerceAtLeast(1).toFloat()
        val turnFrequency: Float = (BASE_TURN_COUNTER_GAIN + character.getCalculatedTotalStatOf(StatItemId.SPEED)).toFloat()
        val weightedActionPoints: Float = actionPoints.weightedAround(REFERENCE_ACTION_POINTS, AP_WEIGHT)
        val weightedTurnFrequency: Float = turnFrequency.weightedAround(REFERENCE_TURN_FREQUENCY, SPEED_WEIGHT)
        val attackPowerPerTurn: Float = weightedActionPoints * character.getBestDamagePerAp()
        val offenseOverTime: Float = damage * attackPowerPerTurn * weightedTurnFrequency
        return sqrt(effectiveHp * offenseOverTime)
    }

    private fun Character.getSurvivabilityMultiplier(): Float {
        val averageProtection: Float =
            (getCalculatedTotalProtection() + getCalculatedTotalMagicProtection()).coerceAtLeast(0) / 2f
        val blockChance: Int = getCalculatedTotalDefense().coerceIn(0, 100)
        return (1f + averageProtection / 100f) * (1f + blockChance / 100f)
    }

    private fun Float.weightedAround(reference: Float, weight: Float): Float {
        return reference * (this / reference).pow(weight)
    }

    private fun Character.getBestDamagePerAp(): Float {
        val hitChance: Float = getHitChance()
        return getAllAbilities()
            .filter { it.target == Target.ENEMY }
            .filter { it.sp == 0 || hasUnlimitedSp }
            .filter { it.isWeaponAllowed(getInventoryItem(InventoryGroup.WEAPON)) }
            .maxOfOrNull { it.damageMultiplier * (hitChance * it.hitMultiplier).coerceAtMost(1f) / it.ap }
            ?: (hitChance / 3f)     // placeholder for one basic attack of 3 AP, same purpose as the unarmed damage above.
    }

    private fun Character.getHitChance(): Float {
        if (isUnarmed) return 0.6f     // 60%. placeholder for minimal hit chance, only used for threat level calculation.
        return getCalculatedTotalHit().coerceIn(1, 100) / 100f
    }

}
