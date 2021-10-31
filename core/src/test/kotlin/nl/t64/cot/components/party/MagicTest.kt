package nl.t64.cot.components.party

import nl.t64.cot.GameTest
import nl.t64.cot.components.party.spells.SpellDatabase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class MagicTest : GameTest() {

    @Test
    fun whenSpellIsCreatedAtRank1_ShouldHaveVariables() {
        val airShield = SpellDatabase.createSpellItem("air_shield", 1)
        assertThat(airShield.id).isEqualTo("air_shield")
        assertThat(airShield.name).isEqualTo("Air Shield")
        assertThat(airShield.sort).isEqualTo(110)
        assertThat(airShield.rank).isEqualTo(1)
        assertThat(airShield.getTotalDescription()).isEqualToIgnoringWhitespace("""
            All friendly entities within the range of the spell
            add 1 per Rank to their Protection value.

            School: Elemental
            Requires: Gemstone
            Stamina cost: 6

            XP needed for next rank: 13
            Gold needed for next rank: 8""")
    }

    @Test
    fun whenSpellIsCreatedAtRank10_ShouldHaveVariables() {
        val airShield = SpellDatabase.createSpellItem("air_shield", 10)
        assertThat(airShield.id).isEqualTo("air_shield")
        assertThat(airShield.name).isEqualTo("Air Shield")
        assertThat(airShield.sort).isEqualTo(110)
        assertThat(airShield.rank).isEqualTo(10)
        assertThat(airShield.getTotalDescription()).isEqualToIgnoringWhitespace("""
            All friendly entities within the range of the spell
            add 1 per Rank to their Protection value.

            School: Elemental
            Requires: Gemstone
            Stamina cost: 6

            XP needed for next rank: Max
            Gold needed for next rank: Max""")
    }

}
