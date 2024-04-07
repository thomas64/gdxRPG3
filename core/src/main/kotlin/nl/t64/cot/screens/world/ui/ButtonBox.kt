package nl.t64.cot.screens.world.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
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

    fun dispose() {
        stage.dispose()
        shapeRenderer.dispose()
    }

    fun update(dt: Float) {
        table.clear()

        val extraMargin: Float
        if (Utils.isGamepadConnected()) {
            table.add("Action")
            table.add("[ A ]").row()
            table.add("Inventory")
            table.add("[ Y ]").row()
            table.add("Quest log")
            table.add("[ X ]").row()
            table.add("Map")
            table.add("[ Select ]").row()
            table.add("Party")
            table.add("[ R-Stick ]").row()
            table.add("Manual")
            table.add("[ L-Stick ]")
            extraMargin = GAMEPAD_EXTRA_MARGIN
        } else {
            table.add("Action")
            table.add("[ A ]").row()
            table.add("Inventory")
            table.add("[ I ]").row()
            table.add("Quest log")
            table.add("[ L ]").row()
            table.add("Map")
            table.add("[ M ]").row()
            table.add("Party")
            table.add("[ P ]").row()
            table.add("Manual")
            table.add("[ H ]")
            extraMargin = 0f
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

    private fun createTable(): Table {
        val debugSkin = Skin()
        debugSkin.add("default", LabelStyle(BitmapFont(), Color.WHITE))

        return Table(debugSkin).apply {
            defaults().width(TABLE_WIDTH).align(Align.left)
            columnDefaults(1).width(SECOND_COLUMN_WIDTH)
            padLeft(PAD_LEFT).padTop(PAD).padBottom(PAD)
            background = Utils.createTransparency()
        }
    }


}
