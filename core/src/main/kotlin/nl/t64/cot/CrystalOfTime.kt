package nl.t64.cot

import com.badlogic.gdx.Application
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controllers
import nl.t64.cot.audio.AudioManager
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.ScreenManager
import nl.t64.cot.screens.world.MapManager


open class CrystalOfTime : Game() {

    private var runtime: Float = 0f

    lateinit var preferenceManager: PreferenceManager
    val profileManager: ProfileManager = ProfileManager()
    val resourceManager: ResourceManager = ResourceManager()
    val gameData: GameData = GameData()
    val scenario: Scenario = Scenario()
    val screenManager: ScreenManager = ScreenManager()
    val audioManager: AudioManager = AudioManager()
    val mapManager: MapManager = MapManager()
    val brokerManager: BrokerManager = BrokerManager()
    val gamepadMapping: GamepadMapping = GamepadMapping()

    override fun create() {
        preferenceManager = PreferenceManager()
        if (Gdx.app.type == Application.ApplicationType.Desktop) {
            preferenceManager.setFullscreenAccordingToPreference()
        }

        brokerManager.profileObservers.addObserver(gameData)
        brokerManager.profileObservers.addObserver(mapManager)

        Controllers.addListener(gamepadMapping.controllerToInputAdapter)

        screenManager.setScreen(ScreenType.MENU_MAIN)
    }

    override fun render() {
        updateTitleBar()
        super.render()
    }

    override fun dispose() {
        screenManager.dispose()
    }

    private fun updateTitleBar() {
        runtime += Gdx.graphics.deltaTime
        Gdx.graphics.setTitle("${Constant.TITLE} - FPS: ${Gdx.graphics.framesPerSecond} - runtime: ${runtime.toTitle()}")
    }

    private fun Float.toTitle(): String {
        return with(toString()) { substring(0, if (length > 4) length - 4 else length) }
    }

}
