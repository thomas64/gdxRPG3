package nl.t64.cot.components.battle

import kotlin.math.exp
import kotlin.math.roundToInt


private data class ArmorCurvePoint(
    val protection: Float,
    val reductionPercent: Float
)

private const val CAP_PERCENT: Float = 90f
private const val POST_CURVE_GROWTH: Float = 0.03f

private val TARGET_CURVE: List<ArmorCurvePoint> = listOf(ArmorCurvePoint(0f, 0f),
                                                         ArmorCurvePoint(7f, 20f),      // 7 * 1       basic light
                                                         ArmorCurvePoint(28f, 30f),     // 7 * 4       masterwork light
                                                         ArmorCurvePoint(51f, 45f),     // 7 * 7  + 2  basic medium
                                                         ArmorCurvePoint(72f, 55f),     // 7 * 10 + 2  masterwork medium
                                                         ArmorCurvePoint(95f, 70f),     // 7 * 13 + 4  basic heavy
                                                         ArmorCurvePoint(116f, 80f))    // 7 * 16 + 4  masterwork heavy

class ArmorReductionCalculator {

    fun toPercent(rawProtection: Int): Int {
        if (rawProtection <= 0) return 0

        return calculateTargetCurveReduction(rawProtection).roundToInt()
    }

    private fun calculateTargetCurveReduction(rawProtection: Int): Float {
        return findReductionWithinCurve(rawProtection)
            ?: growPastLastPoint(rawProtection)
    }

    private fun findReductionWithinCurve(rawProtection: Int): Float? {
        (0 until TARGET_CURVE.lastIndex).forEach { index: Int ->
            val currentPoint: ArmorCurvePoint = TARGET_CURVE[index]
            val nextPoint: ArmorCurvePoint = TARGET_CURVE[index + 1]
            val isWithinThisSegment: Boolean = rawProtection <= nextPoint.protection

            if (isWithinThisSegment) {
                return getReductionBetween(currentPoint, nextPoint, rawProtection)
            }
        }
        return null
    }

    private fun getReductionBetween(currentPoint: ArmorCurvePoint,
                                    nextPoint: ArmorCurvePoint,
                                    rawProtection: Int): Float {
        val protectionRangeBetweenTheTwoPoints: Float = nextPoint.protection - currentPoint.protection
        val progressOfTotalProtectionBetweenTheTwoPoints: Float = (rawProtection - currentPoint.protection) / protectionRangeBetweenTheTwoPoints
        val reductionRangeBetweenTheTwoPoints: Float = nextPoint.reductionPercent - currentPoint.reductionPercent
        return currentPoint.reductionPercent + (progressOfTotalProtectionBetweenTheTwoPoints * reductionRangeBetweenTheTwoPoints)
    }

    private fun growPastLastPoint(rawProtection: Int): Float {
        val lastPoint: ArmorCurvePoint = TARGET_CURVE.last()
        val extraProtectionAboveList: Float = rawProtection - lastPoint.protection
        val growthFactor: Float = 1f - exp(-POST_CURVE_GROWTH * extraProtectionAboveList)
        val remainingHeadroom: Float = CAP_PERCENT - lastPoint.reductionPercent
        return lastPoint.reductionPercent + (remainingHeadroom * growthFactor)
    }

}
