package nl.t64.cot.screens.cutscene

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
private const val TABLE_WIDTH = 125f
private const val PAD_LEFT = 25f
private const val PAD = 15f

internal class SkipBox {

    private val table: Table = createTable()
    private val stage: Stage = Stage().apply { addActor(table) }
    private val shapeRenderer: ShapeRenderer = ShapeRenderer()

    fun dispose() {
        stage.dispose()
        shapeRenderer.dispose()
    }

    fun update(dt: Float) {
        table.clear()

        if (Utils.isGamepadConnected()) {
            table.add("Hold [Start] to skip")
        } else {
            table.add("Hold [Esc] to skip")
        }

        table.pack()
        table.setPosition(TABLE_POSITION, TABLE_POSITION)

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
            pad(PAD, PAD_LEFT, PAD, PAD)
            background = Utils.createTransparency()
        }
    }

}
