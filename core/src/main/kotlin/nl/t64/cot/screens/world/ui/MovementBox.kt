package nl.t64.cot.screens.world.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.Utils
import nl.t64.cot.constants.Constant


private const val TABLE_POSITION_X = 30f
private const val TABLE_POSITION_Y = 205f
private const val TABLE_WIDTH = 165f
private const val PAD = 15f

internal class MovementBox {

    private val style: LabelStyle = LabelStyle(BitmapFont(), Color.WHITE)
    private val table: Table = createTable()
    private val stage: Stage = Stage().apply { addActor(table) }
    private val shapeRenderer: ShapeRenderer = ShapeRenderer()

    fun dispose() {
        stage.dispose()
        shapeRenderer.dispose()
    }

    fun update(moveSpeed: Float, dt: Float) {
        table.clear()

        table.add(Container(Label(moveSpeed.toName(), style))).center()

        table.pack()
        table.setPosition(Gdx.graphics.width - table.width - TABLE_POSITION_X, TABLE_POSITION_Y)

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.WHITE
        shapeRenderer.rect(table.x, table.y, table.width + 1, table.height + 1)
        shapeRenderer.end()

        stage.act(dt)
        stage.draw()
    }

    private fun Float.toName(): String {
        return when (this) {
            Constant.MOVE_SPEED_1 -> "Crouching"
            Constant.MOVE_SPEED_2 -> "Walking"
            Constant.MOVE_SPEED_3 -> "Running"
            Constant.MOVE_SPEED_4 -> "Debugging"
            else -> throw IllegalArgumentException("Unknown moveSpeed: $this")
        }
    }

    private fun createTable(): Table {
        val debugSkin = Skin()
        debugSkin.add("default", style)

        return Table(debugSkin).apply {
            defaults().width(TABLE_WIDTH)
            padTop(PAD).padBottom(PAD)
            background = Utils.createTransparency()
        }
    }

}
