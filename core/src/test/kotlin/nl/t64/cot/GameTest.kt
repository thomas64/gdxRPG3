package nl.t64.cot

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.headless.HeadlessApplication
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration
import com.badlogic.gdx.graphics.GL20
import nl.t64.cot.components.party.HeroContainer
import nl.t64.cot.components.party.PartyContainer
import nl.t64.cot.constants.Constant
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class GameTest {

    private var application: Application? = null

    @BeforeAll
    fun setUp() {
        application = HeadlessApplication(createApplication(), createConfiguration())
        Gdx.gl20 = Mockito.mock(GL20::class.java)
        Gdx.gl = Gdx.gl20
    }

    @AfterAll
    fun tearDown() {
        application!!.exit()
        application = null
    }

    private fun createApplication(): CrystalOfTime {
        return object : CrystalOfTime() {
            override fun create() {}
            override fun resize(width: Int, height: Int) {}
            override fun render() {}
            override fun pause() {}
            override fun resume() {}
            override fun dispose() {}
        }
    }

    private fun createConfiguration(): HeadlessApplicationConfiguration {
        return HeadlessApplicationConfiguration().apply {
            updatesPerSecond = -1
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    lateinit var heroes: HeroContainer
    lateinit var party: PartyContainer

    @BeforeEach
    fun setup() {
        heroes = HeroContainer()
        party = PartyContainer()
        addHeroToParty(Constant.PLAYER_ID)
    }

    fun addHeroToParty(heroId: String) {
        val hero = heroes.getCertainHero(heroId)
        heroes.removeHero(heroId)
        party.addHero(hero)
    }

    fun removeHeroFromParty(heroId: String) {
        val hero = party.getCertainHero(heroId)
        party.removeHero(heroId)
        heroes.addHero(hero)
    }

}
