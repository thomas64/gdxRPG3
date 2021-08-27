package nl.t64.cot.screens.menu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.GdxRuntimeException
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType


const val LOGO_PAD = 20f
const val PAD_TOP = 40f

private const val TITLE_LOGO_W = "sprites/accot_w.png"
private const val TITLE_LOGO_B = "sprites/accot_b.png"
private const val LOGO_SCALE = 0.5f
private const val MENU_FONT = "fonts/barlow_regular_45.ttf"
private const val MENU_SIZE = 45

abstract class MenuScreen : Screen {

    val stage: Stage = Stage()
    val menuFont: BitmapFont = resourceManager.getTrueTypeAsset(MENU_FONT, MENU_SIZE)

    lateinit var startScreen: ScreenType            // only MenuMain or MenuPause
    lateinit var table: Table
    private lateinit var background: Image
    lateinit var fontColor: Color
    private lateinit var fromScreen: ScreenType     // any possible last screen

    var selectedMenuIndex = 0

    abstract fun setupScreen()

    fun setBackground(background: Image) {
        this.background = background
        stage.addActor(background)
        val image = Image(getLogo())
        image.setScale(LOGO_SCALE)
        image.setPosition(Gdx.graphics.width - image.width * image.scaleX - LOGO_PAD,
                          Gdx.graphics.height - image.height * image.scaleY - LOGO_PAD)
        stage.addActor(image)
    }

    fun updateMenuIndex(newIndex: Int) {
        selectedMenuIndex = newIndex
        setAllTextButtonsToDefault()
        setCurrentTextButtonToSelected()
    }

    fun processButton(from: ScreenType, toScreen: ScreenType) {
        val clickedScreen = screenManager.getMenuScreen(toScreen)
        clickedScreen.startScreen = startScreen
        clickedScreen.fromScreen = from
        clickedScreen.setBackground(background)
        screenManager.setScreen(toScreen)
    }

    fun processBackButton() {
        audioManager.handle(AudioCommand.SE_STOP_ALL)
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_MENU_BACK)
        val backScreen = screenManager.getMenuScreen(fromScreen)
        backScreen.setBackground(background)
        screenManager.setScreen(fromScreen)
    }

    open fun setAllTextButtonsToDefault() {
        table.children.forEach { (it as TextButton).style.fontColor = fontColor }
    }

    open fun setCurrentTextButtonToSelected() {
        (table.getChild(selectedMenuIndex) as TextButton).style.fontColor = Constant.DARK_RED
    }

    fun setFontColor() {
        fontColor = when (startScreen) {
            ScreenType.MENU_MAIN -> Color.BLACK
            ScreenType.MENU_PAUSE -> Color.WHITE
            else -> throw IllegalStateException("startScreen can only be Main or Pause Screen.")
        }
    }

    private fun getLogo(): Texture {
        return if (startScreen == ScreenType.MENU_PAUSE) {
            resourceManager.getTextureAsset(TITLE_LOGO_W)
        } else {
            resourceManager.getTextureAsset(TITLE_LOGO_B)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun show() {
        Gdx.input.inputProcessor = stage
        Utils.setGamepadInputProcessor(stage)
        setupScreen()
    }

    override fun hide() {
        stage.clear()
        Gdx.input.inputProcessor = null
        Utils.setGamepadInputProcessor(null)
    }

    override fun dispose() {
        try {
            menuFont.dispose()
        } catch (e: GdxRuntimeException) {
            // font is already exposed.
        }
        stage.dispose()
    }

    override fun resize(width: Int, height: Int) {
        // empty
    }

    override fun pause() {
        // empty
    }

    override fun resume() {
        // empty
    }

}
