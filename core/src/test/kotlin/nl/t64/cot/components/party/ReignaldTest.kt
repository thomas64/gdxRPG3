package nl.t64.cot.components.party

import nl.t64.cot.GameTest
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.spells.SchoolType
import nl.t64.cot.components.party.stats.StatItemId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class ReignaldTest : GameTest() {

    @Test
    fun whenHeroesAreCreated_ReignaldShouldHaveRightStats() {
        val reignald = HeroContainer().getCertainHero("reignald")

        assertThat(reignald.school).isEqualTo(SchoolType.NONE)
        assertThat(reignald.getLevel()).isEqualTo(8)
        assertThat(reignald.totalXp).isEqualTo(1020)
        assertThat(reignald.xpNeededForNextLevel).isEqualTo(405)
        assertThat(reignald.getCurrentHp()).isEqualTo(68)
        assertThat(reignald.getMaximumHp()).isEqualTo(68)

        assertThat(reignald.getStatById(StatItemId.INTELLIGENCE).rank).isEqualTo(10)
        assertThat(reignald.getCalculatedTotalStatOf(StatItemId.INTELLIGENCE)).isEqualTo(10)
        assertThat(reignald.getStatById(StatItemId.WILLPOWER).rank).isEqualTo(8)
        assertThat(reignald.getCalculatedTotalStatOf(StatItemId.WILLPOWER)).isEqualTo(8)
        assertThat(reignald.getStatById(StatItemId.DEXTERITY).rank).isEqualTo(25)
        assertThat(reignald.getCalculatedTotalStatOf(StatItemId.DEXTERITY)).isEqualTo(25)
        assertThat(reignald.getStatById(StatItemId.AGILITY).rank).isEqualTo(10)
        assertThat(reignald.getCalculatedTotalStatOf(StatItemId.AGILITY)).isEqualTo(9)
        assertThat(reignald.getStatById(StatItemId.ENDURANCE).rank).isEqualTo(20)
        assertThat(reignald.getCalculatedTotalStatOf(StatItemId.ENDURANCE)).isEqualTo(20)
        assertThat(reignald.getStatById(StatItemId.STRENGTH).rank).isEqualTo(20)
        assertThat(reignald.getCalculatedTotalStatOf(StatItemId.STRENGTH)).isEqualTo(20)
        assertThat(reignald.getStatById(StatItemId.STAMINA).rank).isEqualTo(40)
        assertThat(reignald.getCalculatedTotalStatOf(StatItemId.STAMINA)).isEqualTo(40)

        assertThat(reignald.getSkillById(SkillItemId.ALCHEMIST).rank).isEqualTo(-1)
        assertThat(reignald.getCalculatedTotalSkillOf(SkillItemId.ALCHEMIST)).isZero
        assertThat(reignald.getSkillById(SkillItemId.DIPLOMAT).rank).isZero
        assertThat(reignald.getCalculatedTotalSkillOf(SkillItemId.DIPLOMAT)).isZero
        assertThat(reignald.getSkillById(SkillItemId.HEALER).rank).isZero
        assertThat(reignald.getCalculatedTotalSkillOf(SkillItemId.HEALER)).isZero
        assertThat(reignald.getSkillById(SkillItemId.MECHANIC).rank).isZero
        assertThat(reignald.getCalculatedTotalSkillOf(SkillItemId.MECHANIC)).isZero
        assertThat(reignald.getSkillById(SkillItemId.MERCHANT).rank).isZero
        assertThat(reignald.getCalculatedTotalSkillOf(SkillItemId.MERCHANT)).isZero
        assertThat(reignald.getSkillById(SkillItemId.RANGER).rank).isZero
        assertThat(reignald.getCalculatedTotalSkillOf(SkillItemId.RANGER)).isZero
        assertThat(reignald.getSkillById(SkillItemId.SCHOLAR).rank).isZero
        assertThat(reignald.getCalculatedTotalSkillOf(SkillItemId.SCHOLAR)).isZero
        assertThat(reignald.getSkillById(SkillItemId.STEALTH).rank).isEqualTo(1)
        assertThat(reignald.getCalculatedTotalSkillOf(SkillItemId.STEALTH)).isZero
        assertThat(reignald.getSkillById(SkillItemId.THIEF).rank).isEqualTo(-1)
        assertThat(reignald.getCalculatedTotalSkillOf(SkillItemId.THIEF)).isZero
        assertThat(reignald.getSkillById(SkillItemId.TROUBADOUR).rank).isZero
        assertThat(reignald.getCalculatedTotalSkillOf(SkillItemId.TROUBADOUR)).isZero
        assertThat(reignald.getSkillById(SkillItemId.WARRIOR).rank).isEqualTo(4)
        assertThat(reignald.getCalculatedTotalSkillOf(SkillItemId.WARRIOR)).isEqualTo(4)
        assertThat(reignald.getSkillById(SkillItemId.WIZARD).rank).isEqualTo(-1)
        assertThat(reignald.getCalculatedTotalSkillOf(SkillItemId.WIZARD)).isZero

        assertThat(reignald.getSkillById(SkillItemId.HAFTED).rank).isZero
        assertThat(reignald.getCalculatedTotalSkillOf(SkillItemId.HAFTED)).isZero
        assertThat(reignald.getSkillById(SkillItemId.MISSILE).rank).isEqualTo(-1)
        assertThat(reignald.getCalculatedTotalSkillOf(SkillItemId.MISSILE)).isZero
        assertThat(reignald.getSkillById(SkillItemId.POLE).rank).isZero
        assertThat(reignald.getCalculatedTotalSkillOf(SkillItemId.POLE)).isZero
        assertThat(reignald.getSkillById(SkillItemId.SHIELD).rank).isEqualTo(2)
        assertThat(reignald.getCalculatedTotalSkillOf(SkillItemId.SHIELD)).isEqualTo(2)
        assertThat(reignald.getSkillById(SkillItemId.SWORD).rank).isEqualTo(4)
        assertThat(reignald.getCalculatedTotalSkillOf(SkillItemId.SWORD)).isEqualTo(4)
        assertThat(reignald.getSkillById(SkillItemId.THROWN).rank).isEqualTo(2)
        assertThat(reignald.getCalculatedTotalSkillOf(SkillItemId.THROWN)).isEqualTo(2)

        assertThat(reignald.getCalcValueOf(InventoryGroup.WEAPON, CalcAttributeId.BASE_HIT)).isEqualTo(50)
        assertThat(reignald.getCalcValueOf(InventoryGroup.WEAPON, CalcAttributeId.DAMAGE)).isEqualTo(15)
        assertThat(reignald.getCalcValueOf(InventoryGroup.SHIELD, CalcAttributeId.DEFENSE)).isEqualTo(10)
        assertThat(reignald.getTotalCalcOf(CalcAttributeId.PROTECTION)).isEqualTo(8)

        assertThat(reignald.getTotalCalcOf(CalcAttributeId.DAMAGE)).isEqualTo(15)
        assertThat(reignald.getCalculatedTotalDamage()).isEqualTo(47)
        assertThat(reignald.getTotalCalcOf(CalcAttributeId.MOVEPOINTS)).isEqualTo(0)
        assertThat(reignald.getCalculatedMovepoints()).isEqualTo(13)
    }

}
