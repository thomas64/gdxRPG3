package nl.t64.cot.screens.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.constants.Constant


private const val FONT = "fonts/calibri_light_28.ttf"
private const val FONT_SIZE = 28
private const val LABEL_WIDTH = 150f
private const val LABEL_HEIGHT = 75f
private const val PAD = 25f

internal class ClockBox {

    private val label: Label = createClockLabel()
    private val stage: Stage = Stage().apply { addActor(label) }
    private val shapeRenderer: ShapeRenderer = ShapeRenderer()

    fun dispose() {
        stage.dispose()
        shapeRenderer.dispose()
    }

    fun update(dt: Float) {
        if (gameData.clock.hasStarted()) {
            gameData.clock.update(dt)
            if (gameData.clock.isFinished()) {
                // todo, game over
            }
        }
    }

    fun render(dt: Float) {
        if (gameData.clock.hasStarted()) {
            val color = if (gameData.clock.isWarning()) Constant.LIGHT_RED else Color.WHITE
            createBorder(color)
            label.color = color
            label.setText(gameData.clock.getCountdownFormatted())
            stage.act(dt)
            stage.draw()
        }
    }

    private fun createBorder(color: Color) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = color
        shapeRenderer.rect(label.x, label.y, label.width + 1, label.height + 1)
        shapeRenderer.end()
    }

    private fun createClockLabel(): Label {
        val labelStyle = Label.LabelStyle(resourceManager.getTrueTypeAsset(FONT, FONT_SIZE), Color.WHITE).apply {
            background = Utils.createTransparency()
        }
        return Label(null, labelStyle).apply {
            setAlignment(Align.center)
            width = LABEL_WIDTH
            height = LABEL_HEIGHT
            x = Gdx.graphics.width - width - PAD
            y = PAD
        }
    }

}
