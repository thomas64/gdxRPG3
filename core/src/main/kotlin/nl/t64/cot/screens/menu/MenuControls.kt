package nl.t64.cot.screens.menu

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ScreenUtils
import nl.t64.cot.Utils


private const val COLUMN_0_WIDTH = 240f
private const val COLUMN_1_WIDTH = 120f

class MenuControls : MenuScreen() {

    private var isGamepadConnected = false

    override fun setupScreen() {
        isGamepadConnected = Utils.isGamepadConnected()
        setFontColor()
        createTables()
        applyListeners()
        stage.addActor(table)
    }

    override fun render(dt: Float) {
        ScreenUtils.clear(Color.BLACK)
        stage.act(dt)
        stage.draw()
        if (isGamepadConnected != Utils.isGamepadConnected()) {
            refreshScreen()
        }
    }

    private fun createTables() {
        val textStyle = LabelStyle(menuFont, fontColor)
        val labels = createInputLabels(textStyle)
        labels.forEach { it.setAlignment(Align.center) }

        table = Table()
        table.setFillParent(true)
        table.columnDefaults(0).width(COLUMN_0_WIDTH)
        table.columnDefaults(1).width(COLUMN_1_WIDTH)
        labels.indices.forEach { addToTable(it, labels) }
        val logo = stage.actors.peek()
        table
            .top().padTop((logo.height * logo.scaleY) + LOGO_PAD + PAD_TOP)
            .right().padRight(((logo.width * logo.scaleX) / 2f) - (table.prefWidth / 2f) + LOGO_PAD)
    }

    private fun addToTable(index: Int, labels: List<Label>) {
        if (index % 2 == 0) {
            table.row()
        }
        table.add(labels[index])
    }

    private fun applyListeners() {
        stage.addListener(ListenerKeyConfirm { super.processBackButton() })
        stage.addListener(ListenerKeyCancel { super.processBackButton() })
    }

    private fun refreshScreen() {
        stage.root.clearListeners()
        table.clear()
        setupScreen()
    }

    private fun createInputLabels(textStyle: LabelStyle): List<Label> {
        return if (isGamepadConnected) {
            createGamepadLabels(textStyle)
        } else {
            createKeyboardLabels(textStyle)
        }
    }

    private fun createKeyboardLabels(textStyle: LabelStyle): List<Label> {
        return listOf(
            Label("Movement", textStyle),
            Label("Arrows", textStyle),
            Label("Move fast", textStyle),
            Label("Shift", textStyle),
            Label("Move slow", textStyle),
            Label("Ctrl", textStyle),
            Label("Action", textStyle),
            Label("A", textStyle),
            Label("Inventory", textStyle),
            Label("I", textStyle),
            Label("Quest log", textStyle),
            Label("L", textStyle),
            Label("Map", textStyle),
            Label("M", textStyle),
            Label("Party", textStyle),
            Label("P", textStyle),
            Label("Pause", textStyle),
            Label("Esc", textStyle)
        )
    }

    private fun createGamepadLabels(textStyle: LabelStyle): List<Label> {
        return listOf(
            Label("Movement", textStyle),
            Label("L3", textStyle),
            Label("Move fast", textStyle),
            Label("R1", textStyle),
            Label("Move slow", textStyle),
            Label("L1", textStyle),
            Label("Action", textStyle),
            Label("A", textStyle),
            Label("Inventory", textStyle),
            Label("Y", textStyle),
            Label("Quest log", textStyle),
            Label("X", textStyle),
            Label("Map", textStyle),
            Label("Select", textStyle),
            Label("Party", textStyle),
            Label("R3", textStyle),
            Label("Pause", textStyle),
            Label("Start", textStyle)
        )
    }

}