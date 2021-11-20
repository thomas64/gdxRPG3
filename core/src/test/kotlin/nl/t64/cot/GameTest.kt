package nl.t64.cot

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.headless.HeadlessApplication
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration
import com.badlogic.gdx.graphics.GL20
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
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

    private fun createApplication(): GdxRpg3 {
        return object : GdxRpg3() {
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

}
