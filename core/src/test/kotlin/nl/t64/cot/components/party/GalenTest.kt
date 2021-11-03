package nl.t64.cot.components.party

import nl.t64.cot.GameTest
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.spells.SchoolType
import nl.t64.cot.components.party.stats.StatItemId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class GalenTest : GameTest() {

    @Test
    fun whenHeroesAreCreated_GalenShouldHaveRightStats() {
        val galen = HeroContainer().getCertainHero("galen")

        assertThat(galen.school).isEqualTo(SchoolType.NONE)
        assertThat(galen.getLevel()).isEqualTo(4)
        assertThat(galen.totalXp).isEqualTo(150)
        assertThat(galen.xpNeededForNextLevel).isEqualTo(125)
        assertThat(galen.getCurrentHp()).isEqualTo(64)
        assertThat(galen.getMaximumHp()).isEqualTo(64)

        assertThat(galen.getStatById(StatItemId.INTELLIGENCE).rank).isEqualTo(15)
        assertThat(galen.getCalculatedTotalStatOf(StatItemId.INTELLIGENCE)).isEqualTo(15)
        assertThat(galen.getStatById(StatItemId.WILLPOWER).rank).isEqualTo(15)
        assertThat(galen.getCalculatedTotalStatOf(StatItemId.WILLPOWER)).isEqualTo(15)
        assertThat(galen.getStatById(StatItemId.DEXTERITY).rank).isEqualTo(18)
        assertThat(galen.getCalculatedTotalStatOf(StatItemId.DEXTERITY)).isEqualTo(18)
        assertThat(galen.getStatById(StatItemId.CONSTITUTION).rank).isEqualTo(20)
        assertThat(galen.getCalculatedTotalStatOf(StatItemId.CONSTITUTION)).isEqualTo(20)
        assertThat(galen.getStatById(StatItemId.STRENGTH).rank).isEqualTo(25)
        assertThat(galen.getCalculatedTotalStatOf(StatItemId.STRENGTH)).isEqualTo(25)
        assertThat(galen.getStatById(StatItemId.STAMINA).rank).isEqualTo(40)
        assertThat(galen.getCalculatedTotalStatOf(StatItemId.STAMINA)).isEqualTo(40)

        assertThat(galen.getSkillById(SkillItemId.ALCHEMIST).rank).isEqualTo(-1)
        assertThat(galen.getCalculatedTotalSkillOf(SkillItemId.ALCHEMIST)).isZero
        assertThat(galen.getSkillById(SkillItemId.DIPLOMAT).rank).isZero
        assertThat(galen.getCalculatedTotalSkillOf(SkillItemId.DIPLOMAT)).isZero
        assertThat(galen.getSkillById(SkillItemId.HEALER).rank).isZero
        assertThat(galen.getCalculatedTotalSkillOf(SkillItemId.HEALER)).isZero
        assertThat(galen.getSkillById(SkillItemId.MECHANIC).rank).isZero
        assertThat(galen.getCalculatedTotalSkillOf(SkillItemId.MECHANIC)).isZero
        assertThat(galen.getSkillById(SkillItemId.MERCHANT).rank).isZero
        assertThat(galen.getCalculatedTotalSkillOf(SkillItemId.MERCHANT)).isZero
        assertThat(galen.getSkillById(SkillItemId.RANGER).rank).isEqualTo(4)
        assertThat(galen.getCalculatedTotalSkillOf(SkillItemId.RANGER)).isEqualTo(4)
        assertThat(galen.getSkillById(SkillItemId.SCHOLAR).rank).isZero
        assertThat(galen.getCalculatedTotalSkillOf(SkillItemId.SCHOLAR)).isZero
        assertThat(galen.getSkillById(SkillItemId.STEALTH).rank).isEqualTo(3)
        assertThat(galen.getCalculatedTotalSkillOf(SkillItemId.STEALTH)).isZero
        assertThat(galen.getSkillById(SkillItemId.THIEF).rank).isZero
        assertThat(galen.getCalculatedTotalSkillOf(SkillItemId.THIEF)).isZero
        assertThat(galen.getSkillById(SkillItemId.TROUBADOUR).rank).isZero
        assertThat(galen.getCalculatedTotalSkillOf(SkillItemId.TROUBADOUR)).isZero
        assertThat(galen.getSkillById(SkillItemId.WARRIOR).rank).isEqualTo(5)
        assertThat(galen.getCalculatedTotalSkillOf(SkillItemId.WARRIOR)).isEqualTo(5)
        assertThat(galen.getSkillById(SkillItemId.WIZARD).rank).isEqualTo(-1)
        assertThat(galen.getCalculatedTotalSkillOf(SkillItemId.WIZARD)).isZero

        assertThat(galen.getSkillById(SkillItemId.HAFTED).rank).isEqualTo(5)
        assertThat(galen.getCalculatedTotalSkillOf(SkillItemId.HAFTED)).isEqualTo(5)
        assertThat(galen.getSkillById(SkillItemId.MISSILE).rank).isEqualTo(3)
        assertThat(galen.getCalculatedTotalSkillOf(SkillItemId.MISSILE)).isEqualTo(3)
        assertThat(galen.getSkillById(SkillItemId.POLE).rank).isZero
        assertThat(galen.getCalculatedTotalSkillOf(SkillItemId.POLE)).isZero
        assertThat(galen.getSkillById(SkillItemId.SHIELD).rank).isEqualTo(3)
        assertThat(galen.getCalculatedTotalSkillOf(SkillItemId.SHIELD)).isEqualTo(3)
        assertThat(galen.getSkillById(SkillItemId.SWORD).rank).isEqualTo(-1)
        assertThat(galen.getCalculatedTotalSkillOf(SkillItemId.SWORD)).isZero
        assertThat(galen.getSkillById(SkillItemId.THROWN).rank).isEqualTo(-1)
        assertThat(galen.getCalculatedTotalSkillOf(SkillItemId.THROWN)).isZero

        assertThat(galen.getCalcValueOf(InventoryGroup.WEAPON, CalcAttributeId.BASE_HIT)).isEqualTo(30)
        assertThat(galen.getCalcValueOf(InventoryGroup.WEAPON, CalcAttributeId.DAMAGE)).isEqualTo(20)
        assertThat(galen.getCalcValueOf(InventoryGroup.SHIELD, CalcAttributeId.DEFENSE)).isEqualTo(10)
        assertThat(galen.getTotalCalcOf(CalcAttributeId.PROTECTION)).isEqualTo(10)

        assertThat(galen.getTotalCalcOf(CalcAttributeId.DAMAGE)).isEqualTo(20)
        assertThat(galen.getCalculatedTotalDamage()).isEqualTo(60)
        assertThat(galen.getTotalCalcOf(CalcAttributeId.ACTION_POINTS)).isEqualTo(0)
        assertThat(galen.getCalculatedActionPoints()).isEqualTo(13)
    }

}
