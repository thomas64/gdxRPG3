package nl.t64.cot.components.party

import nl.t64.cot.GameTest
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.spells.SchoolType
import nl.t64.cot.components.party.stats.StatItemId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class MozesTest : GameTest() {

    @Test
    fun whenHeroesAreCreated_MozesShouldHaveRightStats() {
        val mozes = HeroContainer().getCertainHero("mozes")

        assertThat(mozes.school).isEqualTo(SchoolType.UNKNOWN)
        assertThat(mozes.getLevel()).isEqualTo(1)

        assertThat(mozes.getStatById(StatItemId.INTELLIGENCE).rank).isEqualTo(10)
        assertThat(mozes.getCalculatedTotalStatOf(StatItemId.INTELLIGENCE)).isEqualTo(10)
        assertThat(mozes.getStatById(StatItemId.WILLPOWER).rank).isEqualTo(10)
        assertThat(mozes.getCalculatedTotalStatOf(StatItemId.WILLPOWER)).isEqualTo(10)
        assertThat(mozes.getStatById(StatItemId.STRENGTH).rank).isEqualTo(10)
        assertThat(mozes.getCalculatedTotalStatOf(StatItemId.STRENGTH)).isEqualTo(10)
        assertThat(mozes.getStatById(StatItemId.DEXTERITY).rank).isEqualTo(10)
        assertThat(mozes.getCalculatedTotalStatOf(StatItemId.DEXTERITY)).isEqualTo(10)
        assertThat(mozes.getStatById(StatItemId.CONSTITUTION).rank).isEqualTo(15)
        assertThat(mozes.getCalculatedTotalStatOf(StatItemId.CONSTITUTION)).isEqualTo(15)
        assertThat(mozes.getStatById(StatItemId.STAMINA).rank).isEqualTo(30)
        assertThat(mozes.getCalculatedTotalStatOf(StatItemId.STAMINA)).isEqualTo(30)
        assertThat(mozes.getStatById(StatItemId.SPEED).rank).isEqualTo(10)
        assertThat(mozes.getCalculatedTotalStatOf(StatItemId.SPEED)).isEqualTo(10)

        assertThat(mozes.getSkillById(SkillItemId.ALCHEMIST).rank).isZero
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.ALCHEMIST)).isZero
        assertThat(mozes.getSkillById(SkillItemId.BARBARIAN).rank).isZero
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.BARBARIAN)).isZero
        assertThat(mozes.getSkillById(SkillItemId.DIPLOMAT).rank).isZero
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.DIPLOMAT)).isZero
        assertThat(mozes.getSkillById(SkillItemId.DRUID).rank).isZero
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.DRUID)).isZero
        assertThat(mozes.getSkillById(SkillItemId.JESTER).rank).isZero
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.JESTER)).isZero
        assertThat(mozes.getSkillById(SkillItemId.LOREMASTER).rank).isZero
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.LOREMASTER)).isZero
        assertThat(mozes.getSkillById(SkillItemId.MECHANIC).rank).isZero
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.MECHANIC)).isZero
        assertThat(mozes.getSkillById(SkillItemId.MERCHANT).rank).isZero
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.MERCHANT)).isZero
        assertThat(mozes.getSkillById(SkillItemId.RANGER).rank).isZero
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.RANGER)).isZero
        assertThat(mozes.getSkillById(SkillItemId.SCHOLAR).rank).isZero
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.SCHOLAR)).isZero

        assertThat(mozes.getSkillById(SkillItemId.GAMBLER).rank).isZero
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.GAMBLER)).isZero
        assertThat(mozes.getSkillById(SkillItemId.HEALER).rank).isZero
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.HEALER)).isZero
        assertThat(mozes.getSkillById(SkillItemId.STEALTH).rank).isEqualTo(1)
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.STEALTH)).isEqualTo(1)
        assertThat(mozes.getSkillById(SkillItemId.THIEF).rank).isZero
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.THIEF)).isZero
        assertThat(mozes.getSkillById(SkillItemId.TROUBADOUR).rank).isEqualTo(1)
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.TROUBADOUR)).isEqualTo(1)
        assertThat(mozes.getSkillById(SkillItemId.WARRIOR).rank).isEqualTo(1)
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.WARRIOR)).isEqualTo(1)
        assertThat(mozes.getSkillById(SkillItemId.WIZARD).rank).isEqualTo(1)
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.WIZARD)).isEqualTo(1)

        assertThat(mozes.getSkillById(SkillItemId.HAFTED).rank).isEqualTo(1)
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.HAFTED)).isEqualTo(1)
        assertThat(mozes.getSkillById(SkillItemId.MISSILE).rank).isEqualTo(2)
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.MISSILE)).isEqualTo(2)
        assertThat(mozes.getSkillById(SkillItemId.POLE).rank).isZero
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.POLE)).isZero
        assertThat(mozes.getSkillById(SkillItemId.SHIELD).rank).isEqualTo(2)
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.SHIELD)).isEqualTo(2)
        assertThat(mozes.getSkillById(SkillItemId.SWORD).rank).isEqualTo(2)
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.SWORD)).isEqualTo(2)
        assertThat(mozes.getSkillById(SkillItemId.THROWN).rank).isZero
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.THROWN)).isZero

        assertThat(mozes.getSkillById(SkillItemId.BITE).rank).isZero
        assertThat(mozes.getCalculatedTotalSkillOf(SkillItemId.BITE)).isZero
    }

}
