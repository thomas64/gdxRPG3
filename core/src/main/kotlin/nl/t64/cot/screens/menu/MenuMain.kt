package nl.t64.cot.screens.menu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.utils.ScreenUtils
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playBgm
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType


private const val BACKGROUND = "sprites/titlescreen.jpg"
private const val MENU_ITEM_START = "Start"
private const val MENU_ITEM_SETTINGS = "Settings"
private const val MENU_ITEM_CREDITS = "Credits"
private const val MENU_ITEM_EXIT = "Exit"
private const val NUMBER_OF_ITEMS = 4

class MenuMain : MenuScreen() {

    override val titleLogo: Texture = resourceManager.getTextureAsset(TITLE_LOGO_B)
    override val fontColor: Color = Color.BLACK
    override val selectColor: Color = Constant.LIGHT_RED
    override val backScreen: ScreenType = ScreenType.MENU_MAIN

    private lateinit var listenerKeyVertical: ListenerKeyVertical

    init {
        super.selectedMenuIndex = 0
    }

    override fun setupScreen() {
        playBgm(AudioEvent.BGM_TITLE)
        val texture = resourceManager.getTextureAsset(BACKGROUND)
        setBackground(Image(texture))
        table = createTable()
        applyListeners()
        stage.addActor(table)
        stage.keyboardFocus = table
        setCurrentTextButtonToSelected()
    }

    override fun render(dt: Float) {
        ScreenUtils.clear(Color.BLACK)
        stage.act(dt)
        listenerKeyVertical.updateSelectedIndex(selectedMenuIndex)
        stage.draw()
    }

    private fun selectMenuItem() {
        when (selectedMenuIndex) {
            0 -> processButton(ScreenType.MENU_LOAD_MAIN)
            1 -> processButton(ScreenType.MENU_SETTINGS_MAIN)
            2 -> processButton(ScreenType.MENU_CREDITS)
            3 -> processExitButton()
            else -> throw IllegalArgumentException("SelectedIndex not found.")
        }
    }

    private fun processExitButton() {
        Gdx.app.exit()
    }

    private fun createTable(): Table {
        // styles
        val buttonStyle = TextButtonStyle()
        buttonStyle.font = menuFont
        buttonStyle.fontColor = fontColor

        // actors
        val startButton = TextButton(MENU_ITEM_START, TextButtonStyle(buttonStyle))
        val settingsButton = TextButton(MENU_ITEM_SETTINGS, TextButtonStyle(buttonStyle))
        val creditsButton = TextButton(MENU_ITEM_CREDITS, TextButtonStyle(buttonStyle))
        val exitButton = TextButton(MENU_ITEM_EXIT, TextButtonStyle(buttonStyle))

        // table
        val newTable = Table()
        newTable.setFillParent(true)
        newTable.defaults().center()
        newTable.add(startButton).row()
        newTable.add(settingsButton).row()
        newTable.add(creditsButton).row()
        newTable.add(exitButton)
        val logo = stage.actors.peek()
        newTable
            .top().padTop((logo.height * logo.scaleY) + LOGO_PAD + PAD_TOP)
            .right().padRight(((logo.width * logo.scaleX) / 2f) - (newTable.prefWidth / 2f) + LOGO_PAD)
        return newTable
    }

    private fun applyListeners() {
        listenerKeyVertical = ListenerKeyVertical({ super.updateMenuIndex(it) }, NUMBER_OF_ITEMS)
        val listenerKeyConfirm = ListenerKeyConfirm { selectMenuItem() }
        table.addListener(listenerKeyVertical)
        table.addListener(listenerKeyConfirm)
    }

}
