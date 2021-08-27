package nl.t64.cot.screens.menu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ScreenUtils


private const val CREDITS_PATH = "licenses/credits.txt"
private const val SCROLL_SPEED = 100
private const val EMPTY_LINES = 20

class MenuCredits : MenuScreen() {

    private val scrollPane: ScrollPane

    init {
        val emptyLines = System.lineSeparator().repeat(EMPTY_LINES)
        val creditsText = emptyLines + Gdx.files.internal(CREDITS_PATH).readString() + emptyLines
        val textStyle = LabelStyle(menuFont, Color.BLACK)
        val credits = Label(creditsText, textStyle)
        credits.setAlignment(Align.top or Align.center)
        credits.wrap = true
        scrollPane = ScrollPane(credits)
        createTable()
    }

    override fun setupScreen() {
        scrollPane.scrollY = 0f
        scrollPane.updateVisualScroll()
        applyListeners()
        stage.addActor(table)
    }

    override fun render(dt: Float) {
        ScreenUtils.clear(Color.BLACK)
        stage.act(dt)
        scrollPane.scrollY = scrollPane.scrollY + dt * SCROLL_SPEED
        stage.draw()
    }

    private fun createTable() {
        table = Table()
        table.center()
        table.setFillParent(true)
        table.defaults().width(Gdx.graphics.width.toFloat())
        table.add(scrollPane)
    }

    private fun applyListeners() {
        stage.addListener(ListenerKeyConfirm { super.processBackButton() })
        stage.addListener(ListenerKeyCancel { super.processBackButton() })
    }

}