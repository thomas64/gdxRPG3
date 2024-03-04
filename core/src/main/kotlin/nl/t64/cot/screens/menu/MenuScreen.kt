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
import ktx.assets.disposeSafely
import nl.t64.cot.Utils
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.audio.stopAllSe
import nl.t64.cot.constants.ScreenType


const val LOGO_PAD = 20f
const val PAD_TOP = 40f
const val TITLE_LOGO_W = "sprites/accot_w.png"
const val TITLE_LOGO_B = "sprites/accot_b.png"
const val TITLE_LOGO_NAME = "titleLogo"

private const val LOGO_SCALE = 0.5f
private const val MENU_FONT = "fonts/barlow_regular_45.ttf"
private const val MENU_SIZE = 45

abstract class MenuScreen : Screen {

    abstract val titleLogo: Texture
    abstract val fontColor: Color
    abstract val selectColor: Color
    abstract val backScreen: ScreenType

    val stage: Stage = Stage()
    val menuFont: BitmapFont = resourceManager.getTrueTypeAsset(MENU_FONT, MENU_SIZE)

    lateinit var table: Table
    private lateinit var background: Image

    var selectedMenuIndex = 0

    abstract fun setupScreen()

    fun setBackground(background: Image) {
        this.background = background
        stage.addActor(background)
        val titleLogo = Image(titleLogo)
        titleLogo.name = TITLE_LOGO_NAME
        titleLogo.setScale(LOGO_SCALE)
        titleLogo.setPosition(Gdx.graphics.width - titleLogo.width * titleLogo.scaleX - LOGO_PAD,
                              Gdx.graphics.height - titleLogo.height * titleLogo.scaleY - LOGO_PAD)
        stage.addActor(titleLogo)
    }

    fun updateMenuIndex(newIndex: Int) {
        selectedMenuIndex = newIndex
        setAllTextButtonsToDefault()
        setCurrentTextButtonToSelected()
    }

    fun processButton(toScreen: ScreenType) {
        screenManager.getMenuScreen(toScreen).setBackground(background)
        screenManager.setScreen(toScreen)
    }

    fun processBackButton() {
        stopAllSe()
        playSe(AudioEvent.SE_MENU_BACK)
        screenManager.getMenuScreen(backScreen).setBackground(background)
        screenManager.setScreen(backScreen)
    }

    open fun setAllTextButtonsToDefault() {
        table.children.forEach { (it as TextButton).style.fontColor = fontColor }
    }

    open fun setCurrentTextButtonToSelected() {
        (table.getChild(selectedMenuIndex) as TextButton).style.fontColor = selectColor
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
        menuFont.disposeSafely()
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
