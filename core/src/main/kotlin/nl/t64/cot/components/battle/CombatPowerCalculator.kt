package nl.t64.cot.components.battle

import nl.t64.cot.components.party.abilities.Target
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.stats.StatItemId
import kotlin.math.pow
import kotlin.math.sqrt


private const val SPEED_AND_AP_WEIGHT = 1.2f   // > 1: speed and ap count a bit more than linear

class CombatPowerCalculator {

    fun calculate(character: Character): Float {
        val effectiveHp: Float = character.maximumHp.coerceAtLeast(1) * character.getSurvivabilityMultiplier()
        val damage: Int = if (character.isUnarmed) {
            6       // placeholder for minimal damage
        } else {
            character.getCalculatedTotalDamage().coerceAtLeast(1)
        }
        val actionPoints: Float = character.getCalculatedActionPoints().coerceAtLeast(1).toFloat().pow(SPEED_AND_AP_WEIGHT)
        val turnFrequency: Float = (10 + character.getCalculatedTotalStatOf(StatItemId.SPEED)).toFloat().pow(SPEED_AND_AP_WEIGHT)
        val attackPowerPerTurn: Float = actionPoints * character.getBestDamagePerAp()
        val offenseOverTime: Float = damage * attackPowerPerTurn * turnFrequency
        return sqrt(effectiveHp * offenseOverTime)
    }

    private fun Character.getSurvivabilityMultiplier(): Float {
        val averageProtection: Float =
            (getCalculatedTotalProtection() + getCalculatedTotalMagicProtection()).coerceAtLeast(0) / 2f
        val blockChance: Int = getCalculatedTotalDefense().coerceIn(0, 100)
        return (1f + averageProtection / 100f) * (1f + blockChance / 100f)
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
