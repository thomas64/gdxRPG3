package nl.t64.cot.components.battle

import kotlin.math.exp
import kotlin.math.roundToInt


enum class ArmorReductionChoice {
    LEGACY_K,
    TARGET_CURVE
}

data class ArmorCurvePoint(
    val protection: Float,
    val reductionPercent: Float
)

private const val LEGACY_K_FACTOR: Float = 20f
private const val MAX_REDUCTION_PERCENT: Float = 95f
private const val SOFT_CAP_PERCENT: Float = 92f
private const val POST_CURVE_GROWTH: Float = 0.03f

private val TARGET_CURVE: List<ArmorCurvePoint> = listOf(ArmorCurvePoint(0f, 0f),
                                                         ArmorCurvePoint(7f, 20f),
                                                         ArmorCurvePoint(28f, 30f),
                                                         ArmorCurvePoint(49f, 45f),
                                                         ArmorCurvePoint(70f, 55f),
                                                         ArmorCurvePoint(91f, 70f),
                                                         ArmorCurvePoint(112f, 80f))

class ArmorReductionCalculator(
    private val armorReductionChoice: ArmorReductionChoice = ArmorReductionChoice.TARGET_CURVE
) {

    fun toPercent(rawProtection: Int): Int {
        if (rawProtection <= 0) return 0

        val totalProtection: Float = rawProtection.toFloat()
        val unclampedReductionPercent: Float = calculateReduction(totalProtection)
        return unclampedReductionPercent.coerceIn(0f, MAX_REDUCTION_PERCENT).roundToInt()
    }

    private fun calculateReduction(totalProtection: Float): Float {
        return when (armorReductionChoice) {
            ArmorReductionChoice.LEGACY_K -> calculateLegacyReduction(totalProtection)
            ArmorReductionChoice.TARGET_CURVE -> calculateTargetCurveReduction(totalProtection)
        }
    }

    private fun calculateLegacyReduction(totalProtection: Float): Float {
        return (totalProtection / (LEGACY_K_FACTOR + totalProtection)) * 100f
    }

    private fun calculateTargetCurveReduction(totalProtection: Float): Float {
        return findReductionWithinCurve(totalProtection)
            ?: growPastLastPoint(totalProtection)
    }

    private fun findReductionWithinCurve(totalProtection: Float): Float? {
        (0 until TARGET_CURVE.lastIndex).forEach { index: Int ->
            val currentPoint: ArmorCurvePoint = TARGET_CURVE[index]
            val nextPoint: ArmorCurvePoint = TARGET_CURVE[index + 1]
            val isWithinThisSegment: Boolean = totalProtection <= nextPoint.protection

            if (isWithinThisSegment) {
                return getReductionBetween(currentPoint, nextPoint, totalProtection)
            }
        }
        return null
    }

    private fun getReductionBetween(currentPoint: ArmorCurvePoint,
                                    nextPoint: ArmorCurvePoint,
                                    totalProtection: Float): Float {
        val protectionRangeBetweenTheTwoPoints: Float = nextPoint.protection - currentPoint.protection
        val progressOfTotalProtectionBetweenTheTwoPoints: Float = (totalProtection - currentPoint.protection) / protectionRangeBetweenTheTwoPoints
        val reductionRangeBetweenTheTwoPoints = nextPoint.reductionPercent - currentPoint.reductionPercent
        return currentPoint.reductionPercent + (progressOfTotalProtectionBetweenTheTwoPoints * reductionRangeBetweenTheTwoPoints)
    }

    private fun growPastLastPoint(totalProtection: Float): Float {
        val lastPoint: ArmorCurvePoint = TARGET_CURVE.last()
        val extraProtectionAboveList: Float = totalProtection - lastPoint.protection
        val growthFactor: Float = 1f - exp(-POST_CURVE_GROWTH * extraProtectionAboveList)
        val remainingHeadroom: Float = SOFT_CAP_PERCENT - lastPoint.reductionPercent
        return lastPoint.reductionPercent + (remainingHeadroom * growthFactor)
    }

}
