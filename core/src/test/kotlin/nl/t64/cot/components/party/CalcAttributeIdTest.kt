package nl.t64.cot.components.party

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test


internal class CalcAttributeIdTest {

    @Test
    fun test() {
        assertThatExceptionOfType(NoSuchElementException::class.java).isThrownBy {
            "something".toCalcAttributeId()
        }
        assertThat("weapon damage".toCalcAttributeId()).isSameAs(CalcAttributeId.DAMAGE)
    }

}
