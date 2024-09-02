package nl.t64.cot.screens.world.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import nl.t64.cot.Utils


private const val TABLE_POSITION = 30f
private const val TABLE_WIDTH = 90f
private const val SECOND_COLUMN_WIDTH = 50f
private const val GAMEPAD_EXTRA_MARGIN = 30f
private const val PAD_LEFT = 25f
private const val PAD = 15f

internal class ButtonBox {

    private val table: Table = createTable()
    private val stage: Stage = Stage().apply { addActor(table) }
    private val shapeRenderer: ShapeRenderer = ShapeRenderer()
    private val whiteFontStyle = LabelStyle(BitmapFont(), Color.WHITE)
    private val grayFontStyle = LabelStyle(BitmapFont(), Color.GRAY)

    fun dispose() {
        stage.dispose()
        shapeRenderer.dispose()
    }

    fun update(isMinimapPossible: Boolean, dt: Float) {
        table.clear()

        val extraMargin: Float
        val actions = if (Utils.isGamepadConnected()) {
            listOf(
                "Action" to "[ A ]",
                "Inventory" to "[ Y ]",
                "Quest log" to "[ X ]",
                "Map" to "[ Select ]",
                "Party" to "[ R-Stick ]",
                "Manual" to "[ L-Stick ]"
            ).also { extraMargin = GAMEPAD_EXTRA_MARGIN }
        } else {
            listOf(
                "Action" to "[ A ]",
                "Inventory" to "[ I ]",
                "Quest log" to "[ L ]",
                "Map" to "[ M ]",
                "Party" to "[ P ]",
                "Manual" to "[ H ]"
            ).also { extraMargin = 0f }
        }

        actions.forEach { (action, button) ->
            val labelStyle = getLabelStyle(action, isMinimapPossible)
            table.add(Label(action, labelStyle))
            table.add(Label(button, labelStyle)).row()
        }

        table.width = TABLE_WIDTH + extraMargin
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH + extraMargin)

        table.pack()
        table.setPosition(Gdx.graphics.width - table.width - TABLE_POSITION, TABLE_POSITION)

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.WHITE
        shapeRenderer.rect(table.x, table.y, table.width + 1, table.height + 1)
        shapeRenderer.end()

        stage.act(dt)
        stage.draw()
    }

    private fun getLabelStyle(action: String, isMinimapPossible: Boolean): LabelStyle {
        return if (action == "Map" && !isMinimapPossible) grayFontStyle else whiteFontStyle
    }

    private fun createTable(): Table {
        return Table().apply {
            defaults().width(TABLE_WIDTH).align(Align.left)
            columnDefaults(1).width(SECOND_COLUMN_WIDTH)
            padLeft(PAD_LEFT).padTop(PAD).padBottom(PAD)
            background = Utils.createTransparency()
        }
    }

}
