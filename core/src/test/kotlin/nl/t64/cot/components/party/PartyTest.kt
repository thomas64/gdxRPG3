package nl.t64.cot.components.party

import nl.t64.cot.GameTest
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.constants.Constant
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test


internal class PartyTest : GameTest() {

    @Test
    fun `When check if heroId is player, should return true or false`() {
        addHeroToParty("luana")
        assertThat(party.isPlayer("mozes")).isTrue
        assertThat(party.isPlayer("luana")).isFalse
    }

    @Test
    fun whenPlayerIsRemovedFromParty_ShouldThrowException() {
        assertThat(party.contains(Constant.PLAYER_ID)).isTrue
        assertThat(party.size).isEqualTo(1)

        assertThatIllegalArgumentException()
            .isThrownBy { removeHeroFromParty(Constant.PLAYER_ID) }
            .withMessage("Cannot remove player from party.")
    }

    @Test
    fun whenHeroIsAddedToParty_ShouldBeLastInParty() {
        val mozes = party.getCertainHero("mozes")
        assertThat(party.isHeroLast(mozes)).isTrue

        addHeroToParty("luana")
        val luana = party.getCertainHero("luana")
        assertThat(party.isHeroLast(mozes)).isFalse
        assertThat(party.isHeroLast(luana)).isTrue
    }

    @Test
    fun `When check for average level, should return it only for alive party members`() {
        assertThat(party.getAverageLevel()).isEqualTo(1.0)
        addHeroToParty("luana")
        assertThat(party.getAverageLevel()).isEqualTo(1.5)
        addHeroToParty("reignald")
        assertThat(party.getAverageLevel()).isEqualTo(3.6666666666666665)
        party.getCertainHero("luana").takeDamage(200)
        assertThat(party.getAverageLevel()).isEqualTo(4.5)
    }

    @Test
    fun whenPartyIsFull_ShouldThrowException() {
        addHeroToParty("luana")
        addHeroToParty("reignald")
        addHeroToParty("ryiah")
        addHeroToParty("valter")
        addHeroToParty("galen")

        assertThat(heroes.size).isEqualTo(8)
        assertThat(party.size).isEqualTo(6)

        assertThatIllegalStateException()
            .isThrownBy { addHeroToParty("jaspar") }
            .withMessage("Party is full.")
    }

    @Test
    fun `When party gained XP, should be received by all party members alive`() {
        addHeroToParty("luana")
        addHeroToParty("reignald")
        val mozes = party.getCertainHero("mozes")
        val luana = party.getCertainHero("luana")
        val reignald = party.getCertainHero("reignald")
        reignald.takeDamage(200)

        assertThat(mozes.totalXp).isEqualTo(5)
        assertThat(luana.totalXp).isEqualTo(25)
        assertThat(reignald.totalXp).isEqualTo(1020)
        party.gainXp(1, StringBuilder())
        assertThat(mozes.totalXp).isEqualTo(6)
        assertThat(luana.totalXp).isEqualTo(26)
        assertThat(reignald.totalXp).isEqualTo(1020)
    }

    @Test
    fun `When party recovers HP, should be true for all party members alive`() {
        addHeroToParty("luana")
        addHeroToParty("reignald")

        val mozes = party.getCertainHero("mozes")
        val luana = party.getCertainHero("luana")
        val reignald = party.getCertainHero("reignald")
        mozes.takeDamage(10)
        luana.takeDamage(10)
        reignald.takeDamage(200)

        assertThat(mozes.getCurrentHp()).isEqualTo(36)
        assertThat(luana.getCurrentHp()).isEqualTo(22)
        assertThat(reignald.getCurrentHp()).isZero
        party.recoverFullHp()
        assertThat(mozes.getCurrentHp()).isEqualTo(46)
        assertThat(luana.getCurrentHp()).isEqualTo(32)
        assertThat(reignald.getCurrentHp()).isZero
    }

    @Test
    fun whenGetPreviousOrNextHeroFromParty_ShouldReturnThePreviousOrNextHero() {
        addHeroToParty("luana")
        addHeroToParty("reignald")
        addHeroToParty("ryiah")
        addHeroToParty("valter")
        addHeroToParty("galen")
        val mozes = party.getCertainHero("mozes")
        val galen = party.getCertainHero("galen")
        val luana = party.getCertainHero("luana")
        val ryiah = party.getCertainHero("ryiah")
        val reignald = party.getCertainHero("reignald")
        val valter = party.getCertainHero("valter")

        assertThat(party.getPreviousHero(mozes)).isEqualTo(galen)
        assertThat(party.getNextHero(mozes)).isEqualTo(luana)
        assertThat(party.getPreviousHero(ryiah)).isEqualTo(reignald)
        assertThat(party.getNextHero(ryiah)).isEqualTo(valter)
        assertThat(party.getPreviousHero(galen)).isEqualTo(valter)
        assertThat(party.getNextHero(galen)).isEqualTo(mozes)
    }

    @Test
    fun whenHeroesAreAdded_ShouldIncreaseTheSumOfSkill() {
        assertThat(party.getSumOfSkill(SkillItemId.MERCHANT)).isZero
        addHeroToParty("kiara")
        assertThat(party.getSumOfSkill(SkillItemId.MERCHANT)).isEqualTo(4)
        addHeroToParty("duilio")
        assertThat(party.getSumOfSkill(SkillItemId.MERCHANT)).isEqualTo(9)
        party.getCertainHero("kiara").takeDamage(200)
        assertThat(party.getSumOfSkill(SkillItemId.MERCHANT)).isEqualTo(5)
    }

    @Test
    fun whenAskedForHighestSkill_ShouldReturnThatBestHero() {
        addHeroToParty("luana")
        addHeroToParty("reignald")
        addHeroToParty("ryiah")
        addHeroToParty("valter")
        val luana = party.getCertainHero("luana")
        val valter = party.getCertainHero("valter")
        val mozes = party.getCertainHero("mozes")
        val reignald = party.getCertainHero("reignald")

        assertThat(party.getHeroWithHighestSkill(SkillItemId.THIEF)).isEqualTo(luana)
        assertThat(party.getHeroWithHighestSkill(SkillItemId.MECHANIC)).isEqualTo(valter)
        // no ranger present
        assertThat(party.getHeroWithHighestSkill(SkillItemId.RANGER)).isEqualTo(mozes)
        // two with same highest
        assertThat(party.getHeroWithHighestSkill(SkillItemId.THROWN)).isEqualTo(luana)
        luana.takeDamage(200)
        assertThat(party.getHeroWithHighestSkill(SkillItemId.THROWN)).isEqualTo(reignald)
    }

    @Test
    fun `When party has enough of skill, should return true or false`() {
        assertThat(party.hasEnoughOfSkill(SkillItemId.STEALTH, 1)).isTrue
        assertThat(party.hasEnoughOfSkill(SkillItemId.STEALTH, 3)).isFalse
        addHeroToParty("luana")
        assertThat(party.hasEnoughOfSkill(SkillItemId.STEALTH, 1)).isTrue
        assertThat(party.hasEnoughOfSkill(SkillItemId.STEALTH, 3)).isTrue
        party.getCertainHero("luana").takeDamage(200)
        assertThat(party.hasEnoughOfSkill(SkillItemId.STEALTH, 1)).isTrue
        assertThat(party.hasEnoughOfSkill(SkillItemId.STEALTH, 3)).isFalse
    }

    @Test
    fun `When party has equipment item, should return true or false`() {
        assertThat(party.hasItemInEquipment("basic_dart", 1)).isFalse
        addHeroToParty("luana")
        assertThat(party.hasItemInEquipment("basic_dart", 1)).isTrue
        val luana = party.getCertainHero("luana")
        luana.takeDamage(200)
        assertThat(party.hasItemInEquipment("basic_dart", 1)).isTrue
        luana.clearInventoryItemFor(InventoryGroup.WEAPON)
        assertThat(party.hasItemInEquipment("basic_dart", 1)).isFalse
    }

    @Test
    fun `When get index of party, should return true or false`() {
        assertThat(party.contains(0)).isTrue
        assertThat(party.contains(1)).isFalse
        assertThat(party.contains(2)).isFalse
        assertThat(party.contains(3)).isFalse
        addHeroToParty("luana")
        assertThat(party.contains(0)).isTrue
        assertThat(party.contains(1)).isTrue
        assertThat(party.contains(2)).isFalse
        assertThat(party.contains(3)).isFalse
        addHeroToParty("reignald")
        assertThat(party.contains(0)).isTrue
        assertThat(party.contains(1)).isTrue
        assertThat(party.contains(2)).isTrue
        assertThat(party.contains(3)).isFalse
        removeHeroFromParty("luana")
        assertThat(party.contains(0)).isTrue
        assertThat(party.contains(1)).isTrue
        assertThat(party.contains(2)).isFalse
        assertThat(party.contains(3)).isFalse
    }

    @Test
    fun `When get player, should return Mozes object`() {
        val mozes = party.getCertainHero("mozes")
        assertThat(party.getPlayer()).isEqualTo(mozes)
    }

    @Test
    fun `When get hero by index, should return or throw exception`() {
        val mozes = party.getCertainHero("mozes")
        assertThat(party.getHero(0)).isEqualTo(mozes)
        assertThatExceptionOfType(IndexOutOfBoundsException::class.java).isThrownBy {
            party.getHero(1)
        }
    }

    @Test
    fun `When get hero by index, should return index`() {
        val mozes = party.getCertainHero("mozes")
        assertThat(party.getIndex(mozes)).isEqualTo(0)
        val luana = heroes.getCertainHero("luana")
        assertThat(party.getIndex(luana)).isEqualTo(-1)
        addHeroToParty("luana")
        assertThat(party.getIndex(luana)).isEqualTo(1)
        removeHeroFromParty("luana")
        assertThat(party.getIndex(luana)).isEqualTo(-1)
    }

    @Test
    fun `When get heroes alive, should return alive heroes`() {
        assertThat(party.getAllHeroesAlive()).hasSize(1)
        addHeroToParty("luana")
        assertThat(party.getAllHeroesAlive()).hasSize(2)
        party.getCertainHero("mozes").takeDamage(200)
        assertThat(party.getAllHeroesAlive()).hasSize(1)
        party.getCertainHero("luana").takeDamage(200)
        assertThat(party.getAllHeroesAlive()).hasSize(0)
    }

    @Test
    fun `When get heroes, should return heroes alive or dead`() {
        assertThat(party.getAllHeroes()).hasSize(1)
        addHeroToParty("luana")
        assertThat(party.getAllHeroes()).hasSize(2)
        party.getCertainHero("mozes").takeDamage(200)
        assertThat(party.getAllHeroes()).hasSize(2)
        party.getCertainHero("luana").takeDamage(200)
        assertThat(party.getAllHeroes()).hasSize(2)
    }

    @Test
    fun `When hero is not certain, should throw exception`() {
        assertThatNullPointerException().isThrownBy {
            party.getCertainHero("luana")
        }
    }

}
