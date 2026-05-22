package nl.t64.cot.screens.help

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.FontProvider
import nl.t64.cot.screens.ParchmentScreen


private const val SCROLL_SPEED = 600f
private const val HINT_LINE_SPACE = 50f
private const val WINDOW_POS_X = 100f
private const val WINDOW_POS_Y = 80f
private const val LABEL_PADDING_BOTTOM = 52f
private const val SCROLLBAR_WIDTH = 16f
private const val SCROLLBAR_KNOB_MIN_HEIGHT = 36f
private const val SCROLLBAR_CONTENT_PADDING = 16f

private enum class HelpFilter { NORMAL, BATTLE }

class HelpScreen : ParchmentScreen() {

    private val hintFont: BitmapFont = FontProvider.inconsolata24
    private val windowWidth: Float = Gdx.graphics.width - (WINDOW_POS_X * 2f)
    private val windowHeight: Float = Gdx.graphics.height - 200f

    private lateinit var table: Table
    private lateinit var scrollPane: ScrollPane
    private lateinit var container: Table
    private lateinit var window: Window
    private lateinit var buttonLabel: Label

    private var currentFilter: HelpFilter = HelpFilter.NORMAL
    private var isScrollingUp: Boolean = false
    private var isScrollingDown: Boolean = false

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
        buttonLabel = createLabel()
        setupStage()
    }

    override fun render(dt: Float) {
        if (isScrollingUp) {
            scrollPane.scrollY -= SCROLL_SPEED * dt
        }
        if (isScrollingDown) {
            scrollPane.scrollY += SCROLL_SPEED * dt
        }
        renderStage(dt)
    }

    private fun setupStage() {
        setInputProcessors(stage)

        stage.addActor(window)
        stage.addActor(buttonLabel)
        stage.addListener(HelpScreenListener({ closeScreen() },
                                             { changeFilter(HelpFilter.NORMAL) },
                                             { changeFilter(HelpFilter.BATTLE) },
                                             { isScrollingUp = true },
                                             { isScrollingDown = true },
                                             { isScrollingUp = false },
                                             { isScrollingDown = false }))

        stage.keyboardFocus = scrollPane
        stage.scrollFocus = scrollPane
    }

    private fun changeFilter(filter: HelpFilter) {
        playSe(AudioEvent.SE_MENU_CURSOR)
        currentFilter = filter
        table = createTable()
        scrollPane.actor = table
        scrollPane.scrollY = 0f
        scrollPane.layout()
        window.titleLabel.setText(createWindowTitle())
    }

    private fun createTable(): Table {
        return Table().apply {
            align(Align.top)
            getAllPlayedGuideEvents().forEach { fillRow(it) }
        }
    }

    private fun getAllPlayedGuideEvents(): List<String> {
        return when (currentFilter) {
            HelpFilter.NORMAL -> gameData.events.getAllNonBattlePlayedGuideEvents()
            HelpFilter.BATTLE -> gameData.events.getOnlyBattleGuideEvents()
        }
    }

    private fun Table.fillRow(text: String) {
        val label = Label("[BLACK]$text", createLabelStyle()).apply { setAlignment(Align.center) }
        val amountOfLines: Int = text.lines().count()
        val fontSize: Int = hintFont.data.name.takeLast(2).toInt()
        val height: Float = (amountOfLines * fontSize) + HINT_LINE_SPACE
        val contentWidth: Float = windowWidth - SCROLLBAR_WIDTH - SCROLLBAR_CONTENT_PADDING
        add(label).height(height).width(contentWidth).row()
    }

    private fun createLabelStyle(): LabelStyle {
        return LabelStyle(hintFont, null).apply {
            background = Utils.createBottomBorder()
        }
    }

    private fun createScrollPane(): ScrollPane {
        val style: ScrollPaneStyle = createScrollPaneStyle()
        return ScrollPane(table, style).apply {
            setOverscroll(false, false)
            fadeScrollBars = false
            setScrollingDisabled(true, false)
            setForceScroll(false, false)
            setScrollBarPositions(false, true)
            setScrollbarsOnTop(true)
        }
    }

    private fun createContainer(): Table {
        return Table().apply {
            background = Utils.createTopBorder()
            add(scrollPane).width(windowWidth).height(windowHeight)
        }
    }

    private fun createWindow(): Window {
        val title: String = createWindowTitle()
        return Utils.createDefaultWindow(title, container, Align.center).apply {
            width = windowWidth
            setPosition(WINDOW_POS_X, WINDOW_POS_Y)
            titleLabel.style = LabelStyle(titleLabel.style).apply { fontColor = Color.WHITE }
        }
    }

    private fun createScrollPaneStyle(): ScrollPaneStyle {
        val track: Drawable = Utils.createRightBorder().apply {
            minWidth = SCROLLBAR_WIDTH
        }
        val knob: Drawable = Utils.createFullBorderBlack().apply {
            minWidth = SCROLLBAR_WIDTH
            minHeight = SCROLLBAR_KNOB_MIN_HEIGHT
        }
        return ScrollPaneStyle().apply {
            vScroll = track
            vScrollKnob = knob
        }
    }

    private fun createWindowTitle(): String {
        return when (currentFilter) {
            HelpFilter.NORMAL -> "[BLACK][Q] [FIREBRICK]Tutorial messages | [BLACK]Battle tutorial messages [W]"
            HelpFilter.BATTLE -> "[BLACK][Q] Tutorial messages | [FIREBRICK]Battle tutorial messages [BLACK][W]"
        }
    }

    private fun createLabel(): Label {
        val text: String = createButtonsText()
        val style = LabelStyle(FontProvider.default, Color.BLACK)
        return Label(text, style).apply {
            val centerX: Float = (Gdx.graphics.width - width) / 2f
            setPosition(centerX, LABEL_PADDING_BOTTOM)
        }
    }

    private fun createButtonsText(): String {
        return if (Utils.isGamepadConnected()) {
            "[LB] Messages            [RB] Battle messages           [B] Back"
        } else {
            "[Q] Messages            [W] Battle messages            [T] Back"
        }
    }

}
