package nl.t64.cot.screens.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.constants.Constant


private const val FONT = "fonts/calibri_light_28.ttf"
private const val FONT_SIZE = 28
private const val LABEL_WIDTH = 150f
private const val LABEL_HEIGHT = 75f
private const val RADIUS = 77f
private const val CIRCLE_PAD = 100f
private const val LABEL_PAD_RIGHT = 25f
private const val LABEL_PAD_BOTTOM = 66f

internal class ClockBox {

    private val label: Label = createClockLabel()
    private val analogClock: AnalogClock = AnalogClock()
    private val stage: Stage = Stage().apply {
        addActor(analogClock)
        addActor(label)
    }
    private val shapeRenderer: ShapeRenderer = ShapeRenderer()

    fun dispose() {
        stage.dispose()
        shapeRenderer.dispose()
    }

    fun update(dt: Float) {
        if (gameData.clock.isRunning()) {
            gameData.clock.update(dt)
            handleWarning()
            handleEnding(dt)
        }
    }

    fun render(dt: Float) {
        if (gameData.clock.isRunning()) {
            analogClock.update()
            fillCircle()
            borderCircle()
            label.color = if (gameData.clock.isWarning()) Color.BLACK else Color.WHITE
            label.setText(gameData.clock.getTimeOfDayFormatted())
            stage.act(dt)
            stage.draw()
        }
    }

    private fun handleWarning() {
        if (gameData.clock.isWarning()) {
            audioManager.fadeBgmForClockWarning()
        }
    }

    private fun handleEnding(dt: Float) {
        if (gameData.clock.isFinished()) {
            Utils.runWithDelay(Constant.FADE_DURATION) {
                brokerManager.mapObservers.notifyStartCutscene("scene_death", 1f)
            }
            audioManager.fadeAllInSeparateThreadForClockEnding(dt)
        }
    }

    private fun fillCircle() {
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Constant.TRANSPARENT
        shapeRenderer.circle(Gdx.graphics.width - CIRCLE_PAD, CIRCLE_PAD, RADIUS)
        shapeRenderer.end()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    private fun borderCircle() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.WHITE
        shapeRenderer.circle(Gdx.graphics.width - CIRCLE_PAD, CIRCLE_PAD, RADIUS)
        shapeRenderer.end()
    }

    private fun createClockLabel(): Label {
        val labelStyle = Label.LabelStyle(resourceManager.getTrueTypeAsset(FONT, FONT_SIZE), Color.WHITE)
        return Label(null, labelStyle).apply {
            setAlignment(Align.center)
            width = LABEL_WIDTH
            height = LABEL_HEIGHT
            x = Gdx.graphics.width - width - LABEL_PAD_RIGHT
            y = LABEL_PAD_BOTTOM
        }
    }

}
