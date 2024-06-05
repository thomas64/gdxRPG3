package nl.t64.cot.screens.menu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.utils.ScreenUtils
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.createScreenshot
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.pauseAllBg
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType


private const val MENU_ITEM_CONTINUE = "Continue"
private const val MENU_ITEM_LOAD_GAME = "Load Game"
private const val MENU_ITEM_SETTINGS = "Settings"
private const val MENU_ITEM_MAIN_MENU = "Main Menu"

private const val NUMBER_OF_ITEMS = 4
private const val EXIT_INDEX = 0

private val DIALOG_MESSAGE = """
    All progress after the last save will be lost.
    Are you sure you want to exit?""".trimIndent()

class MenuPause : MenuScreen() {

    override val titleLogo: Texture = resourceManager.getTextureAsset(TITLE_LOGO_W)
    override val fontColor: Color = Color.WHITE
    override val selectColor: Color = Constant.DARK_RED
    override val backScreen: ScreenType = ScreenType.MENU_PAUSE

    private lateinit var listenerKeyVertical: ListenerKeyVertical

    companion object {
        fun load() {
            playSe(AudioEvent.SE_MENU_CONFIRM)
            val menuPause = screenManager.getMenuScreen(ScreenType.MENU_PAUSE)
            menuPause.setBackground(createScreenshot(true))
            screenManager.setScreen(ScreenType.MENU_PAUSE)
            menuPause.updateMenuIndex(EXIT_INDEX)
        }
    }

    init {
        super.selectedMenuIndex = EXIT_INDEX
    }

    override fun setupScreen() {
        stage.addAction(Actions.alpha(1f))
        table = createTable()
        applyListeners()
        stage.addActor(table)
        stage.keyboardFocus = table
        setCurrentTextButtonToSelected()
    }

    override fun render(dt: Float) {
        ScreenUtils.clear(Color.BLACK)
        stage.act(dt)
        audioManager.certainFadeBgmBgs()
        listenerKeyVertical.updateSelectedIndex(selectedMenuIndex)
        stage.draw()
    }

    private fun selectMenuItem() {
        pauseAllBg()
        when (selectedMenuIndex) {
            0 -> processContinueButton()
            1 -> processButton(ScreenType.MENU_LOAD_PAUSE)
            2 -> processButton(ScreenType.MENU_SETTINGS_PAUSE)
            3 -> processMainMenuButton()
            else -> throw IllegalArgumentException("SelectedIndex not found.")
        }
    }

    private fun processContinueButton() {
        screenManager.setScreen(ScreenType.WORLD)
    }

    private fun processMainMenuButton() {
        DialogQuestion({ openMenuMain() }, DIALOG_MESSAGE).show(stage)
    }

    private fun openMenuMain() {
        Gdx.input.inputProcessor = null
        Utils.setGamepadInputProcessor(null)
        stage.addAction(Actions.sequence(Actions.run { mapManager.disposeOldMaps() },
                                         Actions.fadeOut(Constant.FADE_DURATION),
                                         Actions.delay(0.5f),
                                         Actions.run { screenManager.setScreen(ScreenType.MENU_MAIN) }))
    }

    private fun createTable(): Table {
        // styles
        val buttonStyle = TextButtonStyle()
        buttonStyle.font = menuFont
        buttonStyle.fontColor = fontColor

        // actors
        val continueButton = TextButton(MENU_ITEM_CONTINUE, TextButtonStyle(buttonStyle))
        val loadGameButton = TextButton(MENU_ITEM_LOAD_GAME, TextButtonStyle(buttonStyle))
        val settingsButton = TextButton(MENU_ITEM_SETTINGS, TextButtonStyle(buttonStyle))
        val mainMenuButton = TextButton(MENU_ITEM_MAIN_MENU, TextButtonStyle(buttonStyle))

        // table
        val newTable = Table()
        newTable.setFillParent(true)
        newTable.defaults().center()
        newTable.add(continueButton).row()
        newTable.add(loadGameButton).row()
        newTable.add(settingsButton).row()
        newTable.add(mainMenuButton)
        val logo: Image = stage.root.findActor(TITLE_LOGO_NAME)
        newTable
            .top().padTop((logo.height * logo.scaleY) + LOGO_PAD + PAD_TOP)
            .right().padRight(((logo.width * logo.scaleX) / 2f) - (newTable.prefWidth / 2f) + LOGO_PAD)
        return newTable
    }

    private fun applyListeners() {
        listenerKeyVertical = ListenerKeyVertical({ super.updateMenuIndex(it) }, NUMBER_OF_ITEMS)
        val listenerKeyConfirm = ListenerKeyConfirm { selectMenuItem() }
        val listenerKeyCancel = ListenerKeyCancel({ super.updateMenuIndex(it) }, { selectMenuItem() }, EXIT_INDEX)
        val listenerKeyStart = ListenerKeyStart({ super.updateMenuIndex(it) }, { selectMenuItem() }, EXIT_INDEX)
        table.addListener(listenerKeyVertical)
        table.addListener(listenerKeyConfirm)
        table.addListener(listenerKeyCancel)
        table.addListener(listenerKeyStart)
    }

}
