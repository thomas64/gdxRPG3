package nl.t64.cot.components.party

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class CalcAttributeIdTest {

    @Test
    fun test() {
        assertThat("something".toCalcAttributeId()).isNull()
        assertThat("weapon damage".toCalcAttributeId()).isSameAs(CalcAttributeId.DAMAGE)
    }

}
