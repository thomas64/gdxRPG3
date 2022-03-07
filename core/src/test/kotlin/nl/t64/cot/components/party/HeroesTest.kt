package nl.t64.cot.components.party

import nl.t64.cot.GameTest
import nl.t64.cot.constants.Constant
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class HeroesTest : GameTest() {

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

}
