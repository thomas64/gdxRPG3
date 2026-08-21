package nl.t64.cot.components.battle

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


private const val EXPECTED_CAP_PERCENT: Int = 90

internal class ArmorReductionCalculatorTest {

    private val calculator = ArmorReductionCalculator()

    @Test
    fun whenProtectionIsZeroOrLower_ShouldGiveNoReduction() {
        assertThat(calculator.toPercent(0)).isEqualTo(0)
        assertThat(calculator.toPercent(-5)).isEqualTo(0)
    }

    @Test
    fun whenProtectionIsOnCurvePoint_ShouldGiveExactReduction() {
        assertThat(calculator.toPercent(7)).isEqualTo(20)
        assertThat(calculator.toPercent(28)).isEqualTo(30)
        assertThat(calculator.toPercent(51)).isEqualTo(45)
        assertThat(calculator.toPercent(72)).isEqualTo(55)
        assertThat(calculator.toPercent(95)).isEqualTo(70)
        assertThat(calculator.toPercent(116)).isEqualTo(80)
    }

    @Test
    fun whenProtectionIsBetweenCurvePoints_ShouldInterpolateLinearly() {
        assertThat(calculator.toPercent(1)).isEqualTo(3)
        assertThat(calculator.toPercent(4)).isEqualTo(11)
        assertThat(calculator.toPercent(14)).isEqualTo(23)
        assertThat(calculator.toPercent(21)).isEqualTo(27)
        assertThat(calculator.toPercent(62)).isEqualTo(50)
    }

    @Test
    fun whenProtectionIsAboveLastCurvePoint_ShouldGrowTowardCap() {
        assertThat(calculator.toPercent(117)).isEqualTo(80)
        assertThat(calculator.toPercent(150)).isEqualTo(86)
        assertThat(calculator.toPercent(194)).isEqualTo(89)
        assertThat(calculator.toPercent(1000)).isEqualTo(EXPECTED_CAP_PERCENT)
        assertThat(calculator.toPercent(100000)).isEqualTo(EXPECTED_CAP_PERCENT)
    }

    @Test
    fun whenProtectionKeepsRising_ShouldNeverDropAndNeverPassCap() {
        var previousPercentage = 0
        (1..1000).forEach { protection: Int ->
            val percentage: Int = calculator.toPercent(protection)
            assertThat(percentage).isGreaterThanOrEqualTo(previousPercentage)
            assertThat(percentage).isLessThanOrEqualTo(EXPECTED_CAP_PERCENT)
            previousPercentage = percentage
        }
    }

}
