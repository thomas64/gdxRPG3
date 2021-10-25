package nl.t64.cot.components.party

import nl.t64.cot.GameTest
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.spells.SchoolType
import nl.t64.cot.components.party.stats.StatItemId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class KiaraTest : GameTest() {

    @Test
    fun whenHeroesAreCreated_KiaraShouldHaveRightStats() {
        val kiara = HeroContainer().getCertainHero("kiara")

        assertThat(kiara.school).isEqualTo(SchoolType.ELEMENTAL)
        assertThat(kiara.getLevel()).isEqualTo(12)
        assertThat(kiara.totalXp).isEqualTo(3250)
        assertThat(kiara.xpNeededForNextLevel).isEqualTo(845)
        assertThat(kiara.getCurrentHp()).isEqualTo(72)
        assertThat(kiara.getMaximumHp()).isEqualTo(72)

        assertThat(kiara.getStatById(StatItemId.INTELLIGENCE).rank).isEqualTo(15)
        assertThat(kiara.getCalculatedTotalStatOf(StatItemId.INTELLIGENCE)).isEqualTo(15)
        assertThat(kiara.getStatById(StatItemId.WILLPOWER).rank).isEqualTo(10)
        assertThat(kiara.getCalculatedTotalStatOf(StatItemId.WILLPOWER)).isEqualTo(10)
        assertThat(kiara.getStatById(StatItemId.DEXTERITY).rank).isEqualTo(30)
        assertThat(kiara.getCalculatedTotalStatOf(StatItemId.DEXTERITY)).isEqualTo(30)
        assertThat(kiara.getStatById(StatItemId.ENDURANCE).rank).isEqualTo(20)
        assertThat(kiara.getCalculatedTotalStatOf(StatItemId.ENDURANCE)).isEqualTo(20)
        assertThat(kiara.getStatById(StatItemId.STRENGTH).rank).isEqualTo(15)
        assertThat(kiara.getCalculatedTotalStatOf(StatItemId.STRENGTH)).isEqualTo(15)
        assertThat(kiara.getStatById(StatItemId.STAMINA).rank).isEqualTo(40)
        assertThat(kiara.getCalculatedTotalStatOf(StatItemId.STAMINA)).isEqualTo(40)

        assertThat(kiara.getSkillById(SkillItemId.ALCHEMIST).rank).isZero
        assertThat(kiara.getCalculatedTotalSkillOf(SkillItemId.ALCHEMIST)).isZero
        assertThat(kiara.getSkillById(SkillItemId.DIPLOMAT).rank).isZero
        assertThat(kiara.getCalculatedTotalSkillOf(SkillItemId.DIPLOMAT)).isZero
        assertThat(kiara.getSkillById(SkillItemId.HEALER).rank).isEqualTo(1)
        assertThat(kiara.getCalculatedTotalSkillOf(SkillItemId.HEALER)).isEqualTo(1)
        assertThat(kiara.getSkillById(SkillItemId.MECHANIC).rank).isZero
        assertThat(kiara.getCalculatedTotalSkillOf(SkillItemId.MECHANIC)).isZero
        assertThat(kiara.getSkillById(SkillItemId.MERCHANT).rank).isEqualTo(4)
        assertThat(kiara.getCalculatedTotalSkillOf(SkillItemId.MERCHANT)).isEqualTo(4)
        assertThat(kiara.getSkillById(SkillItemId.RANGER).rank).isZero
        assertThat(kiara.getCalculatedTotalSkillOf(SkillItemId.RANGER)).isZero
        assertThat(kiara.getSkillById(SkillItemId.SCHOLAR).rank).isZero
        assertThat(kiara.getCalculatedTotalSkillOf(SkillItemId.SCHOLAR)).isZero
        assertThat(kiara.getSkillById(SkillItemId.STEALTH).rank).isEqualTo(5)
        assertThat(kiara.getCalculatedTotalSkillOf(SkillItemId.STEALTH)).isEqualTo(5)
        assertThat(kiara.getSkillById(SkillItemId.THIEF).rank).isEqualTo(8)
        assertThat(kiara.getCalculatedTotalSkillOf(SkillItemId.THIEF)).isEqualTo(8)
        assertThat(kiara.getSkillById(SkillItemId.TROUBADOUR).rank).isZero
        assertThat(kiara.getCalculatedTotalSkillOf(SkillItemId.TROUBADOUR)).isZero
        assertThat(kiara.getSkillById(SkillItemId.WARRIOR).rank).isZero
        assertThat(kiara.getCalculatedTotalSkillOf(SkillItemId.WARRIOR)).isZero
        assertThat(kiara.getSkillById(SkillItemId.WIZARD).rank).isEqualTo(4)
        assertThat(kiara.getCalculatedTotalSkillOf(SkillItemId.WIZARD)).isEqualTo(4)

        assertThat(kiara.getSkillById(SkillItemId.HAFTED).rank).isEqualTo(-1)
        assertThat(kiara.getCalculatedTotalSkillOf(SkillItemId.HAFTED)).isZero
        assertThat(kiara.getSkillById(SkillItemId.MISSILE).rank).isEqualTo(7)
        assertThat(kiara.getCalculatedTotalSkillOf(SkillItemId.MISSILE)).isEqualTo(7)
        assertThat(kiara.getSkillById(SkillItemId.POLE).rank).isEqualTo(2)
        assertThat(kiara.getCalculatedTotalSkillOf(SkillItemId.POLE)).isEqualTo(2)
        assertThat(kiara.getSkillById(SkillItemId.SHIELD).rank).isEqualTo(-1)
        assertThat(kiara.getCalculatedTotalSkillOf(SkillItemId.SHIELD)).isZero
        assertThat(kiara.getSkillById(SkillItemId.SWORD).rank).isEqualTo(7)
        assertThat(kiara.getCalculatedTotalSkillOf(SkillItemId.SWORD)).isEqualTo(7)
        assertThat(kiara.getSkillById(SkillItemId.THROWN).rank).isEqualTo(-1)
        assertThat(kiara.getCalculatedTotalSkillOf(SkillItemId.THROWN)).isZero

        assertThat(kiara.getCalcValueOf(InventoryGroup.WEAPON, CalcAttributeId.BASE_HIT)).isEqualTo(50)
        assertThat(kiara.getCalcValueOf(InventoryGroup.WEAPON, CalcAttributeId.DAMAGE)).isEqualTo(13)
        assertThat(kiara.getCalcValueOf(InventoryGroup.SHIELD, CalcAttributeId.DEFENSE)).isZero
        assertThat(kiara.getTotalCalcOf(CalcAttributeId.PROTECTION)).isEqualTo(2)

        assertThat(kiara.getTotalCalcOf(CalcAttributeId.DAMAGE)).isEqualTo(13)
        assertThat(kiara.getCalculatedTotalDamage()).isEqualTo(42)
        assertThat(kiara.getTotalCalcOf(CalcAttributeId.ACTION_POINTS)).isEqualTo(0)
        assertThat(kiara.getCalculatedActionPoints()).isEqualTo(14)
    }

}
