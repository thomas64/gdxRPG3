package nl.t64.cot.components.battle

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


private const val EXPECTED_CAP_PERCENT: Int = 90

internal class ArmorReductionCalculatorTest {

    @Test
    fun `when armor is zero or lower, should give no reduction`() {
        assertThat(ArmorReductionCalculator(0).toPercent()).isEqualTo(0)
        assertThat(ArmorReductionCalculator(-5).toPercent()).isEqualTo(0)
    }

    @Test
    fun `when armor is on curve point, should give exact reduction`() {
        assertThat(ArmorReductionCalculator(7).toPercent()).isEqualTo(20)
        assertThat(ArmorReductionCalculator(28).toPercent()).isEqualTo(30)
        assertThat(ArmorReductionCalculator(51).toPercent()).isEqualTo(45)
        assertThat(ArmorReductionCalculator(72).toPercent()).isEqualTo(55)
        assertThat(ArmorReductionCalculator(95).toPercent()).isEqualTo(70)
        assertThat(ArmorReductionCalculator(116).toPercent()).isEqualTo(80)
    }

    @Test
    fun `when armor is between curve points, should interpolate linearly`() {
        assertThat(ArmorReductionCalculator(1).toPercent()).isEqualTo(3)
        assertThat(ArmorReductionCalculator(4).toPercent()).isEqualTo(11)
        assertThat(ArmorReductionCalculator(14).toPercent()).isEqualTo(23)
        assertThat(ArmorReductionCalculator(21).toPercent()).isEqualTo(27)
        assertThat(ArmorReductionCalculator(62).toPercent()).isEqualTo(50)
    }

    @Test
    fun `when armor is above last curve point, should grow toward cap`() {
        assertThat(ArmorReductionCalculator(117).toPercent()).isEqualTo(80)
        assertThat(ArmorReductionCalculator(150).toPercent()).isEqualTo(86)
        assertThat(ArmorReductionCalculator(194).toPercent()).isEqualTo(89)
        assertThat(ArmorReductionCalculator(1000).toPercent()).isEqualTo(EXPECTED_CAP_PERCENT)
        assertThat(ArmorReductionCalculator(100000).toPercent()).isEqualTo(EXPECTED_CAP_PERCENT)
    }

    @Test
    fun `when armor keeps rising, should never drop and never pass cap`() {
        var previousPercentage = 0
        (1..1000).forEach { armor: Int ->
            val percentage: Int = ArmorReductionCalculator(armor).toPercent()
            assertThat(percentage).isGreaterThanOrEqualTo(previousPercentage)
            assertThat(percentage).isLessThanOrEqualTo(EXPECTED_CAP_PERCENT)
            previousPercentage = percentage
        }
    }

}
