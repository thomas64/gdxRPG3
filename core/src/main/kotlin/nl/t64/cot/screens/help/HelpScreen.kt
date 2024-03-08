package nl.t64.cot.screens.help

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.utils.Align
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.ParchmentScreen


private const val TEXT_FONT = "fonts/spectral_regular_24.ttf"
private const val TEXT_SIZE = 24
private const val TITLE = "Hints"
private const val SCROLL_SPEED = 100f
private const val HINT_LINE_SPACE = 50f
private const val WINDOW_POS_X = 100f
private const val WINDOW_POS_Y = 80f

class HelpScreen : ParchmentScreen() {

    private val hintFont: BitmapFont = resourceManager.getTrueTypeAsset(TEXT_FONT, TEXT_SIZE)
    private val windowWidth: Float = Gdx.graphics.width - (WINDOW_POS_X * 2f)
    private val windowHeight: Float = Gdx.graphics.height - 200f

    private lateinit var table: Table
    private lateinit var scrollPane: ScrollPane
    private lateinit var container: Table
    private lateinit var window: Window

    companion object {
        fun load() {
            playSe(AudioEvent.SE_SCROLL)
            screenManager.openParchmentLoadScreen(ScreenType.HELP)
        }
    }

    override fun show() {
        table = createTable()
        scrollPane = createScrollPane()
        container = createContainer()
        window = createWindow()
        setupStage()
    }

    override fun render(dt: Float) {
        renderStage(dt)
    }

    private fun createTable() = Table().apply {
        align(Align.top)
        gameData.events.getAllPlayedGuideEvents().forEach { fillRow(it) }
    }

    private fun Table.fillRow(text: String) {
        val label = Label(text, createLabelStyle()).apply { setAlignment(Align.center) }
        val amountOfLines: Int = text.lines().count()
        val height: Float = (amountOfLines * TEXT_SIZE) + HINT_LINE_SPACE
        add(label).height(height).width(windowWidth).row()
    }

    private fun createLabelStyle() = LabelStyle(hintFont, Color.BLACK).apply {
        background = Utils.createBottomBorder()
    }

    private fun createScrollPane() = ScrollPane(table).apply {
        setOverscroll(false, false)
        fadeScrollBars = false
        setScrollingDisabled(true, false)
        setForceScroll(false, false)
        setScrollBarPositions(false, false)
    }

    private fun createContainer() = Table().apply {
        background = Utils.createTopBorder()
        add(scrollPane).height(windowHeight)
    }

    private fun createWindow() = Utils.createDefaultWindow(TITLE, container, Align.center).apply {
        width = windowWidth
        setPosition(WINDOW_POS_X, WINDOW_POS_Y)
    }

    private fun setupStage() {
        setInputProcessors(stage)

        stage.addActor(window)
        stage.addListener(HelpScreenListener({ closeScreen() },
                                             { scrollUp() },
                                             { scrollDown() }))

        stage.keyboardFocus = scrollPane
        stage.scrollFocus = scrollPane
    }

    private fun scrollUp() {
        scrollPane.scrollY -= SCROLL_SPEED
    }

    private fun scrollDown() {
        scrollPane.scrollY += SCROLL_SPEED
    }

}
