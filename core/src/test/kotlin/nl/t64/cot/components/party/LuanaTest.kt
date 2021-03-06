package nl.t64.cot.components.party

import nl.t64.cot.GameTest
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.spells.SchoolType
import nl.t64.cot.components.party.stats.StatItemId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class LuanaTest : GameTest() {

    @Test
    fun whenHeroesAreCreated_LuanaShouldHaveRightStats() {
        val luana = HeroContainer().getCertainHero("luana")

        assertThat(luana.school).isEqualTo(SchoolType.ELEMENTAL)
        assertThat(luana.getLevel()).isEqualTo(2)

        assertThat(luana.getStatById(StatItemId.INTELLIGENCE).rank).isEqualTo(10)
        assertThat(luana.getCalculatedTotalStatOf(StatItemId.INTELLIGENCE)).isEqualTo(10)
        assertThat(luana.getStatById(StatItemId.WILLPOWER).rank).isEqualTo(10)
        assertThat(luana.getCalculatedTotalStatOf(StatItemId.WILLPOWER)).isEqualTo(10)
        assertThat(luana.getStatById(StatItemId.STRENGTH).rank).isEqualTo(8)
        assertThat(luana.getCalculatedTotalStatOf(StatItemId.STRENGTH)).isEqualTo(8)
        assertThat(luana.getStatById(StatItemId.DEXTERITY).rank).isEqualTo(16)
        assertThat(luana.getCalculatedTotalStatOf(StatItemId.DEXTERITY)).isEqualTo(16)
        assertThat(luana.getStatById(StatItemId.CONSTITUTION).rank).isEqualTo(10)
        assertThat(luana.getCalculatedTotalStatOf(StatItemId.CONSTITUTION)).isEqualTo(10)
        assertThat(luana.getStatById(StatItemId.STAMINA).rank).isEqualTo(20)
        assertThat(luana.getCalculatedTotalStatOf(StatItemId.STAMINA)).isEqualTo(20)
        assertThat(luana.getStatById(StatItemId.SPEED).rank).isEqualTo(15)
        assertThat(luana.getCalculatedTotalStatOf(StatItemId.SPEED)).isEqualTo(15)

        assertThat(luana.getSkillById(SkillItemId.ALCHEMIST).rank).isZero
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.ALCHEMIST)).isZero
        assertThat(luana.getSkillById(SkillItemId.BARBARIAN).rank).isZero
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.BARBARIAN)).isZero
        assertThat(luana.getSkillById(SkillItemId.DIPLOMAT).rank).isZero
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.DIPLOMAT)).isZero
        assertThat(luana.getSkillById(SkillItemId.DRUID).rank).isZero
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.DRUID)).isZero
        assertThat(luana.getSkillById(SkillItemId.JESTER).rank).isZero
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.JESTER)).isZero
        assertThat(luana.getSkillById(SkillItemId.LOREMASTER).rank).isZero
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.LOREMASTER)).isZero
        assertThat(luana.getSkillById(SkillItemId.MECHANIC).rank).isEqualTo(1)
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.MECHANIC)).isEqualTo(1)
        assertThat(luana.getSkillById(SkillItemId.MERCHANT).rank).isZero
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.MERCHANT)).isZero
        assertThat(luana.getSkillById(SkillItemId.RANGER).rank).isZero
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.RANGER)).isZero
        assertThat(luana.getSkillById(SkillItemId.SCHOLAR).rank).isZero
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.SCHOLAR)).isZero

        assertThat(luana.getSkillById(SkillItemId.GAMBLER).rank).isZero
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.GAMBLER)).isZero
        assertThat(luana.getSkillById(SkillItemId.HEALER).rank).isZero
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.HEALER)).isZero
        assertThat(luana.getSkillById(SkillItemId.STEALTH).rank).isEqualTo(3)
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.STEALTH)).isEqualTo(3)
        assertThat(luana.getSkillById(SkillItemId.THIEF).rank).isEqualTo(3)
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.THIEF)).isEqualTo(3)
        assertThat(luana.getSkillById(SkillItemId.TROUBADOUR).rank).isZero
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.TROUBADOUR)).isZero
        assertThat(luana.getSkillById(SkillItemId.WARRIOR).rank).isZero
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.WARRIOR)).isZero
        assertThat(luana.getSkillById(SkillItemId.WIZARD).rank).isZero
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.WIZARD)).isZero

        assertThat(luana.getSkillById(SkillItemId.HAFTED).rank).isEqualTo(-1)
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.HAFTED)).isZero
        assertThat(luana.getSkillById(SkillItemId.MISSILE).rank).isEqualTo(-1)
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.MISSILE)).isZero
        assertThat(luana.getSkillById(SkillItemId.POLE).rank).isZero
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.POLE)).isZero
        assertThat(luana.getSkillById(SkillItemId.SHIELD).rank).isEqualTo(-1)
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.SHIELD)).isZero
        assertThat(luana.getSkillById(SkillItemId.SWORD).rank).isEqualTo(1)
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.SWORD)).isEqualTo(1)
        assertThat(luana.getSkillById(SkillItemId.THROWN).rank).isEqualTo(2)
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.THROWN)).isEqualTo(2)

        assertThat(luana.getSkillById(SkillItemId.BITE).rank).isZero
        assertThat(luana.getCalculatedTotalSkillOf(SkillItemId.BITE)).isZero
    }

}
