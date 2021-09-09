package nl.t64.cot.components.party

import nl.t64.cot.GameTest
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.spells.SchoolType
import nl.t64.cot.components.party.stats.StatItemId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class LuthaisTest : GameTest() {

    @Test
    fun whenHeroesAreCreated_LuthaisShouldHaveRightStats() {
        val luthais = HeroContainer().getCertainHero("luthais")

        assertThat(luthais.school).isEqualTo(SchoolType.ELEMENTAL)
        assertThat(luthais.getLevel()).isEqualTo(20)
        assertThat(luthais.totalXp).isEqualTo(14350)
        assertThat(luthais.xpNeededForNextLevel).isEqualTo(2205)
        assertThat(luthais.getCurrentHp()).isEqualTo(88)
        assertThat(luthais.getMaximumHp()).isEqualTo(88)

        assertThat(luthais.getStatById(StatItemId.INTELLIGENCE).rank).isEqualTo(30)
        assertThat(luthais.getCalculatedTotalStatOf(StatItemId.INTELLIGENCE)).isEqualTo(30)
        assertThat(luthais.getStatById(StatItemId.WILLPOWER).rank).isEqualTo(30)
        assertThat(luthais.getCalculatedTotalStatOf(StatItemId.WILLPOWER)).isEqualTo(30)
        assertThat(luthais.getStatById(StatItemId.DEXTERITY).rank).isEqualTo(20)
        assertThat(luthais.getCalculatedTotalStatOf(StatItemId.DEXTERITY)).isEqualTo(20)
        assertThat(luthais.getStatById(StatItemId.AGILITY).rank).isEqualTo(12)
        assertThat(luthais.getCalculatedTotalStatOf(StatItemId.AGILITY)).isEqualTo(12)
        assertThat(luthais.getStatById(StatItemId.ENDURANCE).rank).isEqualTo(18)
        assertThat(luthais.getCalculatedTotalStatOf(StatItemId.ENDURANCE)).isEqualTo(18)
        assertThat(luthais.getStatById(StatItemId.STRENGTH).rank).isEqualTo(8)
        assertThat(luthais.getCalculatedTotalStatOf(StatItemId.STRENGTH)).isEqualTo(8)
        assertThat(luthais.getStatById(StatItemId.STAMINA).rank).isEqualTo(50)
        assertThat(luthais.getCalculatedTotalStatOf(StatItemId.STAMINA)).isEqualTo(50)

        assertThat(luthais.getSkillById(SkillItemId.ALCHEMIST).rank).isEqualTo(7)
        assertThat(luthais.getCalculatedTotalSkillOf(SkillItemId.ALCHEMIST)).isEqualTo(7)
        assertThat(luthais.getSkillById(SkillItemId.DIPLOMAT).rank).isZero
        assertThat(luthais.getCalculatedTotalSkillOf(SkillItemId.DIPLOMAT)).isZero
        assertThat(luthais.getSkillById(SkillItemId.HEALER).rank).isEqualTo(8)
        assertThat(luthais.getCalculatedTotalSkillOf(SkillItemId.HEALER)).isEqualTo(8)
        assertThat(luthais.getSkillById(SkillItemId.LOREMASTER).rank).isEqualTo(9)
        assertThat(luthais.getCalculatedTotalSkillOf(SkillItemId.LOREMASTER)).isEqualTo(9)
        assertThat(luthais.getSkillById(SkillItemId.MECHANIC).rank).isEqualTo(6)
        assertThat(luthais.getCalculatedTotalSkillOf(SkillItemId.MECHANIC)).isEqualTo(6)
        assertThat(luthais.getSkillById(SkillItemId.MERCHANT).rank).isZero
        assertThat(luthais.getCalculatedTotalSkillOf(SkillItemId.MERCHANT)).isZero
        assertThat(luthais.getSkillById(SkillItemId.RANGER).rank).isZero
        assertThat(luthais.getCalculatedTotalSkillOf(SkillItemId.RANGER)).isZero
        assertThat(luthais.getSkillById(SkillItemId.STEALTH).rank).isEqualTo(5)
        assertThat(luthais.getCalculatedTotalSkillOf(SkillItemId.STEALTH)).isEqualTo(5)
        assertThat(luthais.getSkillById(SkillItemId.THIEF).rank).isZero
        assertThat(luthais.getCalculatedTotalSkillOf(SkillItemId.THIEF)).isZero
        assertThat(luthais.getSkillById(SkillItemId.TROUBADOUR).rank).isZero
        assertThat(luthais.getCalculatedTotalSkillOf(SkillItemId.TROUBADOUR)).isZero
        assertThat(luthais.getSkillById(SkillItemId.WARRIOR).rank).isZero
        assertThat(luthais.getCalculatedTotalSkillOf(SkillItemId.WARRIOR)).isZero
        assertThat(luthais.getSkillById(SkillItemId.WIZARD).rank).isEqualTo(10)
        assertThat(luthais.getCalculatedTotalSkillOf(SkillItemId.WIZARD)).isEqualTo(10)

        assertThat(luthais.getSkillById(SkillItemId.HAFTED).rank).isZero
        assertThat(luthais.getCalculatedTotalSkillOf(SkillItemId.HAFTED)).isZero
        assertThat(luthais.getSkillById(SkillItemId.MISSILE).rank).isEqualTo(-1)
        assertThat(luthais.getCalculatedTotalSkillOf(SkillItemId.MISSILE)).isZero
        assertThat(luthais.getSkillById(SkillItemId.POLE).rank).isEqualTo(8)
        assertThat(luthais.getCalculatedTotalSkillOf(SkillItemId.POLE)).isEqualTo(8)
        assertThat(luthais.getSkillById(SkillItemId.SHIELD).rank).isEqualTo(-1)
        assertThat(luthais.getCalculatedTotalSkillOf(SkillItemId.SHIELD)).isZero
        assertThat(luthais.getSkillById(SkillItemId.SWORD).rank).isZero
        assertThat(luthais.getCalculatedTotalSkillOf(SkillItemId.SWORD)).isZero
        assertThat(luthais.getSkillById(SkillItemId.THROWN).rank).isEqualTo(8)
        assertThat(luthais.getCalculatedTotalSkillOf(SkillItemId.THROWN)).isEqualTo(8)

        assertThat(luthais.getCalcValueOf(InventoryGroup.WEAPON, CalcAttributeId.BASE_HIT)).isEqualTo(80)
        assertThat(luthais.getCalcValueOf(InventoryGroup.WEAPON, CalcAttributeId.DAMAGE)).isEqualTo(10)
        assertThat(luthais.getCalcValueOf(InventoryGroup.SHIELD, CalcAttributeId.DEFENSE)).isZero
        assertThat(luthais.getTotalCalcOf(CalcAttributeId.PROTECTION)).isEqualTo(3)

        assertThat(luthais.getTotalCalcOf(CalcAttributeId.DAMAGE)).isEqualTo(10)
        assertThat(luthais.getCalculatedTotalDamage()).isEqualTo(34)
        assertThat(luthais.getTotalCalcOf(CalcAttributeId.MOVEPOINTS)).isEqualTo(0)
        assertThat(luthais.getCalculatedMovepoints()).isEqualTo(15)
    }

}
