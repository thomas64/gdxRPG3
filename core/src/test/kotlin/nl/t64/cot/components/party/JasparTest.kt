package nl.t64.cot.components.party

import nl.t64.cot.GameTest
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.spells.SchoolType
import nl.t64.cot.components.party.stats.StatItemId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class JasparTest : GameTest() {

    @Test
    fun whenHeroesAreCreated_JasparShouldHaveRightStats() {
        val jaspar = HeroContainer().getCertainHero("jaspar")

        assertThat(jaspar.school).isEqualTo(SchoolType.NONE)
        assertThat(jaspar.getLevel()).isEqualTo(12)
        assertThat(jaspar.totalXp).isEqualTo(3250)
        assertThat(jaspar.xpNeededForNextLevel).isEqualTo(845)
        assertThat(jaspar.getCurrentHp()).isEqualTo(102)
        assertThat(jaspar.getMaximumHp()).isEqualTo(102)

        assertThat(jaspar.getStatById(StatItemId.INTELLIGENCE).rank).isEqualTo(6)
        assertThat(jaspar.getCalculatedTotalStatOf(StatItemId.INTELLIGENCE)).isEqualTo(6)
        assertThat(jaspar.getStatById(StatItemId.WILLPOWER).rank).isEqualTo(11)
        assertThat(jaspar.getCalculatedTotalStatOf(StatItemId.WILLPOWER)).isEqualTo(11)
        assertThat(jaspar.getStatById(StatItemId.DEXTERITY).rank).isEqualTo(14)
        assertThat(jaspar.getCalculatedTotalStatOf(StatItemId.DEXTERITY)).isEqualTo(14)
        assertThat(jaspar.getStatById(StatItemId.CONSTITUTION).rank).isEqualTo(30)
        assertThat(jaspar.getCalculatedTotalStatOf(StatItemId.CONSTITUTION)).isEqualTo(30)
        assertThat(jaspar.getStatById(StatItemId.STRENGTH).rank).isEqualTo(30)
        assertThat(jaspar.getCalculatedTotalStatOf(StatItemId.STRENGTH)).isEqualTo(30)
        assertThat(jaspar.getStatById(StatItemId.STAMINA).rank).isEqualTo(60)
        assertThat(jaspar.getCalculatedTotalStatOf(StatItemId.STAMINA)).isEqualTo(60)

        assertThat(jaspar.getSkillById(SkillItemId.ALCHEMIST).rank).isEqualTo(-1)
        assertThat(jaspar.getCalculatedTotalSkillOf(SkillItemId.ALCHEMIST)).isZero
        assertThat(jaspar.getSkillById(SkillItemId.DIPLOMAT).rank).isZero
        assertThat(jaspar.getCalculatedTotalSkillOf(SkillItemId.DIPLOMAT)).isZero
        assertThat(jaspar.getSkillById(SkillItemId.HEALER).rank).isZero
        assertThat(jaspar.getCalculatedTotalSkillOf(SkillItemId.HEALER)).isZero
        assertThat(jaspar.getSkillById(SkillItemId.MECHANIC).rank).isZero
        assertThat(jaspar.getCalculatedTotalSkillOf(SkillItemId.MECHANIC)).isZero
        assertThat(jaspar.getSkillById(SkillItemId.MERCHANT).rank).isZero
        assertThat(jaspar.getCalculatedTotalSkillOf(SkillItemId.MERCHANT)).isZero
        assertThat(jaspar.getSkillById(SkillItemId.RANGER).rank).isZero
        assertThat(jaspar.getCalculatedTotalSkillOf(SkillItemId.RANGER)).isZero
        assertThat(jaspar.getSkillById(SkillItemId.SCHOLAR).rank).isEqualTo(-1)
        assertThat(jaspar.getCalculatedTotalSkillOf(SkillItemId.SCHOLAR)).isZero
        assertThat(jaspar.getSkillById(SkillItemId.STEALTH).rank).isEqualTo(1)
        assertThat(jaspar.getCalculatedTotalSkillOf(SkillItemId.STEALTH)).isZero
        assertThat(jaspar.getSkillById(SkillItemId.THIEF).rank).isEqualTo(-1)
        assertThat(jaspar.getCalculatedTotalSkillOf(SkillItemId.THIEF)).isZero
        assertThat(jaspar.getSkillById(SkillItemId.TROUBADOUR).rank).isEqualTo(-1)
        assertThat(jaspar.getCalculatedTotalSkillOf(SkillItemId.TROUBADOUR)).isZero
        assertThat(jaspar.getSkillById(SkillItemId.WARRIOR).rank).isEqualTo(6)
        assertThat(jaspar.getCalculatedTotalSkillOf(SkillItemId.WARRIOR)).isEqualTo(6)
        assertThat(jaspar.getSkillById(SkillItemId.WIZARD).rank).isEqualTo(-1)
        assertThat(jaspar.getCalculatedTotalSkillOf(SkillItemId.WIZARD)).isZero

        assertThat(jaspar.getSkillById(SkillItemId.HAFTED).rank).isZero
        assertThat(jaspar.getCalculatedTotalSkillOf(SkillItemId.HAFTED)).isZero
        assertThat(jaspar.getSkillById(SkillItemId.MISSILE).rank).isEqualTo(-1)
        assertThat(jaspar.getCalculatedTotalSkillOf(SkillItemId.MISSILE)).isZero
        assertThat(jaspar.getSkillById(SkillItemId.POLE).rank).isEqualTo(6)
        assertThat(jaspar.getCalculatedTotalSkillOf(SkillItemId.POLE)).isEqualTo(6)
        assertThat(jaspar.getSkillById(SkillItemId.SHIELD).rank).isEqualTo(4)
        assertThat(jaspar.getCalculatedTotalSkillOf(SkillItemId.SHIELD)).isEqualTo(4)
        assertThat(jaspar.getSkillById(SkillItemId.SWORD).rank).isEqualTo(6)
        assertThat(jaspar.getCalculatedTotalSkillOf(SkillItemId.SWORD)).isEqualTo(6)
        assertThat(jaspar.getSkillById(SkillItemId.THROWN).rank).isEqualTo(-1)
        assertThat(jaspar.getCalculatedTotalSkillOf(SkillItemId.THROWN)).isZero

        assertThat(jaspar.getCalcValueOf(InventoryGroup.WEAPON, CalcAttributeId.BASE_HIT)).isEqualTo(50)
        assertThat(jaspar.getCalcValueOf(InventoryGroup.WEAPON, CalcAttributeId.DAMAGE)).isEqualTo(10)
        assertThat(jaspar.getCalcValueOf(InventoryGroup.SHIELD, CalcAttributeId.DEFENSE)).isEqualTo(15)
        assertThat(jaspar.getTotalCalcOf(CalcAttributeId.PROTECTION)).isEqualTo(18)

        assertThat(jaspar.getTotalCalcOf(CalcAttributeId.DAMAGE)).isEqualTo(10)
        assertThat(jaspar.getCalculatedTotalDamage()).isEqualTo(58)
        assertThat(jaspar.getTotalCalcOf(CalcAttributeId.ACTION_POINTS)).isEqualTo(0)
        assertThat(jaspar.getCalculatedActionPoints()).isEqualTo(15)
    }

}
