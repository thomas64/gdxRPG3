package nl.t64.cot.components.party

import nl.t64.cot.GameTest
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.constants.Constant
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


internal class HeroesTest : GameTest() {

    private lateinit var heroes: HeroContainer
    private lateinit var party: PartyContainer

    @BeforeEach
    private fun setup() {
        heroes = HeroContainer()
        party = PartyContainer()
        addHeroToParty(Constant.PLAYER_ID)
    }

    private fun addHeroToParty(heroId: String) {
        val hero = heroes.getCertainHero(heroId)
        heroes.removeHero(heroId)
        party.addHero(hero)
    }

    private fun removeHeroFromParty(heroId: String) {
        val hero = party.getCertainHero(heroId)
        party.removeHero(heroId)
        heroes.addHero(hero)
    }

    @Test
    fun whenDataIsCreated_ShouldContainPlayer() {
        val mozes = party.getCertainHero("mozes")
        assertThat(heroes.contains(Constant.PLAYER_ID)).isFalse
        assertThat(heroes.size).isEqualTo(13)
        assertThat(party.contains(Constant.PLAYER_ID)).isTrue
        assertThat(party.size).isEqualTo(1)
        assertThat(party.containsExactlyEqualTo(mozes)).isTrue
    }

    @Test
    fun `When other savegame is started, should recognize old objects are wrong`() {
        val luanaString = "luana"
        val luanaObject = heroes.getCertainHero(luanaString)

        assertThat(party.contains(luanaString)).isFalse
        assertThat(party.containsExactlyEqualTo(luanaObject)).isFalse
        addHeroToParty(luanaString)
        assertThat(party.contains(luanaString)).isTrue
        assertThat(party.containsExactlyEqualTo(luanaObject)).isTrue

        setup()

        assertThat(party.contains(luanaString)).isFalse
        assertThat(party.containsExactlyEqualTo(luanaObject)).isFalse
        addHeroToParty(luanaString)
        assertThat(party.contains(luanaString)).isTrue
        assertThat(party.containsExactlyEqualTo(luanaObject)).isFalse
    }

    @Test
    fun `When do check whether heroId is player, should return true or false`() {

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
    fun whenHeroIsAddedToParty_ShouldSetRecruitedTrue() {
        val luana = "luana"
        assertThat(heroes.getCertainHero(luana).hasBeenRecruited).isFalse
        addHeroToParty(luana)
        assertThat(party.getCertainHero(luana).hasBeenRecruited).isTrue
        removeHeroFromParty(luana)
        assertThat(heroes.getCertainHero(luana).hasBeenRecruited).isTrue
    }

    @Test
    fun whenHeroIsAddedToParty_ShouldBeRemovedFromHeroContainer() {
        val luana = "luana"

        assertThat(heroes.size).isEqualTo(13)
        assertThat(heroes.contains(luana)).isTrue
        assertThat(party.size).isEqualTo(1)
        assertThat(party.contains(luana)).isFalse

        addHeroToParty(luana)

        assertThat(heroes.size).isEqualTo(12)
        assertThat(heroes.contains(luana)).isFalse
        assertThat(party.size).isEqualTo(2)
        assertThat(party.contains(luana)).isTrue
    }

    @Test
    fun whenHeroIsRemovedFromParty_ShouldBeAddedToHeroContainer() {
        val luana = "luana"

        addHeroToParty(luana)

        assertThat(heroes.size).isEqualTo(12)
        assertThat(heroes.contains(luana)).isFalse
        assertThat(party.size).isEqualTo(2)
        assertThat(party.contains(luana)).isTrue

        removeHeroFromParty(luana)

        assertThat(heroes.size).isEqualTo(13)
        assertThat(heroes.contains(luana)).isTrue
        assertThat(party.size).isEqualTo(1)
        assertThat(party.contains(luana)).isFalse
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

        assertThat(party.getHeroWithHighestSkill(SkillItemId.THIEF)).isEqualTo(luana)
        assertThat(party.getHeroWithHighestSkill(SkillItemId.MECHANIC)).isEqualTo(valter)
        // no ranger present
        assertThat(party.getHeroWithHighestSkill(SkillItemId.RANGER)).isEqualTo(mozes)
        // two with same highest
        assertThat(party.getHeroWithHighestSkill(SkillItemId.THROWN)).isEqualTo(luana)
    }

}
