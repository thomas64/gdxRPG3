package nl.t64.cot.components.battle

import kotlin.math.exp
import kotlin.math.roundToInt


private data class ArmorCurvePoint(
    val armor: Float,
    val reductionPercent: Float
)

const val MAX_PROTECTION_PERCENT: Int = 90
private const val POST_CURVE_GROWTH: Float = 0.03f

private val TARGET_CURVE: List<ArmorCurvePoint> = listOf(ArmorCurvePoint(0f, 0f),
                                                         ArmorCurvePoint(7f, 20f),      // 7 * 1       basic light
                                                         ArmorCurvePoint(28f, 30f),     // 7 * 4       masterwork light
                                                         ArmorCurvePoint(51f, 45f),     // 7 * 7  + 2  basic medium
                                                         ArmorCurvePoint(72f, 55f),     // 7 * 10 + 2  masterwork medium
                                                         ArmorCurvePoint(95f, 70f),     // 7 * 13 + 4  basic heavy
                                                         ArmorCurvePoint(116f, 80f))    // 7 * 16 + 4  masterwork heavy

class ArmorReductionCalculator(
    private val armor: Int
) {

    fun toPercent(): Int {
        if (armor <= 0) return 0

        return calculateTargetCurveReduction().roundToInt()
    }

    private fun calculateTargetCurveReduction(): Float {
        return findReductionWithinCurve()
            ?: growPastLastPoint()
    }

    private fun findReductionWithinCurve(): Float? {
        (0 until TARGET_CURVE.lastIndex).forEach { index: Int ->
            val currentPoint: ArmorCurvePoint = TARGET_CURVE[index]
            val nextPoint: ArmorCurvePoint = TARGET_CURVE[index + 1]
            val isWithinThisSegment: Boolean = armor <= nextPoint.armor

            if (isWithinThisSegment) {
                return getReductionBetween(currentPoint, nextPoint)
            }
        }
        return null
    }

    private fun getReductionBetween(currentPoint: ArmorCurvePoint,
                                    nextPoint: ArmorCurvePoint): Float {
        val armorRangeBetweenTheTwoPoints: Float = nextPoint.armor - currentPoint.armor
        val progressOfTotalArmorBetweenTheTwoPoints: Float = (armor - currentPoint.armor) / armorRangeBetweenTheTwoPoints
        val reductionRangeBetweenTheTwoPoints: Float = nextPoint.reductionPercent - currentPoint.reductionPercent
        return currentPoint.reductionPercent + (progressOfTotalArmorBetweenTheTwoPoints * reductionRangeBetweenTheTwoPoints)
    }

    private fun growPastLastPoint(): Float {
        val lastPoint: ArmorCurvePoint = TARGET_CURVE.last()
        val extraArmorAboveList: Float = armor - lastPoint.armor
        val growthFactor: Float = 1f - exp(-POST_CURVE_GROWTH * extraArmorAboveList)
        val remainingHeadroom: Float = MAX_PROTECTION_PERCENT - lastPoint.reductionPercent
        return lastPoint.reductionPercent + (remainingHeadroom * growthFactor)
    }

}
