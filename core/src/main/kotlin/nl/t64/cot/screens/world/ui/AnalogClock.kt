package nl.t64.cot.screens.world.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Pixmap.Blending
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.disposeAndClear


private const val START_ANGLE: Float = 90f
private const val SIZE: Float = 150f
private const val CENTER: Float = SIZE / 2f
private const val PAD_RIGHT: Float = 181f
private const val PAD_BOTTOM: Float = 287f
private const val ALPHA: Float = 0.8f
private const val CIRCLE_SEGMENTS: Int = 48

private const val TRANSITION_RANGE: Float = 0.15f
private const val LIME_THRESHOLD: Float = 0.5f
private const val GOLD_THRESHOLD: Float = 0.35f
private const val ORANGE_THRESHOLD: Float = 0.2f
private const val RED_THRESHOLD: Float = 0.05f

class AnalogClock : Table() {

    private val display: Table = Table()
    private val texturesToDispose: MutableSet<Texture> = mutableSetOf()
    private var lastPercentage: Float = -1f

    init {
        display.setPosition(0f, 0f)
        super.addActor(display)
        super.setSize(SIZE, SIZE)
        super.setPosition(Gdx.graphics.width - PAD_RIGHT, PAD_BOTTOM)
    }

    fun dispose() {
        texturesToDispose.disposeAndClear()
    }

    fun update(percentageOfCircle: Float) {
        if (percentageOfCircle > 1f) return
        if (percentageOfCircle == lastPercentage) return
        lastPercentage = percentageOfCircle

        texturesToDispose.disposeAndClear()
        display.clear()

        val clockColor = getColorBasedOnTime(percentageOfCircle)
        super.setColor(clockColor)

        val clock = createTimer(percentageOfCircle)
        display.addActor(clock)
    }

    private fun createTimer(percentageOfCircle: Float): Image {
        val angle: Float = percentageOfCircle.calculateAngle()
        val pixmap = Pixmap(SIZE.toInt(), SIZE.toInt(), Pixmap.Format.RGBA8888)
        val theta: Float = 2f * MathUtils.PI * (angle / 360f) / CIRCLE_SEGMENTS
        val cos: Float = MathUtils.cos(theta)
        val sin: Float = MathUtils.sin(theta)

        var cx: Float = CENTER * MathUtils.cos(START_ANGLE * MathUtils.degreesToRadians)
        var cy: Float = CENTER * MathUtils.sin(-1 * START_ANGLE * MathUtils.degreesToRadians)

        pixmap.setColor(color)

        repeat(CIRCLE_SEGMENTS) {
            val pcx = cx
            val pcy = cy
            val temp = cx
            cx = cos * cx - sin * cy
            cy = sin * temp + cos * cy
            pixmap.fillTriangle(
                CENTER.toInt(),
                CENTER.toInt(),
                (CENTER + pcx).toInt(),
                (CENTER + pcy).toInt(),
                (CENTER + cx).toInt(),
                (CENTER + cy).toInt()
            )
        }
        pixmap.blending = Blending.None
        val texture = Texture(pixmap)
        texturesToDispose.add(texture)
        return Image(texture)
            .apply { setColor(1f, 1f, 1f, ALPHA) }
            .also { pixmap.dispose() }
    }

    private fun getColorBasedOnTime(percentageOfCircle: Float): Color {
        val clampedPercentage = MathUtils.clamp(percentageOfCircle, 0f, 1f)
        return when {
            clampedPercentage <= RED_THRESHOLD ->
                Color.RED.cpy()

            clampedPercentage <= ORANGE_THRESHOLD ->
                Color.RED.cpy().lerp(Color.ORANGE, (clampedPercentage - RED_THRESHOLD) / TRANSITION_RANGE)

            clampedPercentage <= GOLD_THRESHOLD ->
                Color.ORANGE.cpy().lerp(Color.GOLD, (clampedPercentage - ORANGE_THRESHOLD) / TRANSITION_RANGE)

            clampedPercentage <= LIME_THRESHOLD ->
                Color.GOLD.cpy().lerp(Color.LIME, (clampedPercentage - GOLD_THRESHOLD) / TRANSITION_RANGE)

            else ->
                Color.LIME.cpy()
        }
    }

    private fun Float.calculateAngle(): Float {
        val remainingPercentage = this
        return 360f - (360f * remainingPercentage)
    }
}
