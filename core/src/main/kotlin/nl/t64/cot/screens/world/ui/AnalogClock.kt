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
import kotlin.math.abs
import kotlin.math.cbrt
import kotlin.math.max


private const val START_ANGLE = 90f
private const val SIZE = 150f
private const val PAD_RIGHT = 181f
private const val PAD_BOTTOM = 207f
private const val ALPHA = 0.8f

class AnalogClock : Table() {

    private val display: Table = Table()
    private val texturesToDispose: MutableSet<Texture> = mutableSetOf()

    init {
        display.setPosition(0f, 0f)
        super.addActor(display)
        super.setSize(SIZE, SIZE)
        super.setPosition(Gdx.graphics.width - PAD_RIGHT, PAD_BOTTOM)
    }

    fun update(percentageOfCircle: Float) {
        texturesToDispose.disposeAndClear()
        display.clear()

        val clockColor = getColorBasedOnTime(percentageOfCircle)
        super.setColor(clockColor)

        val clock = createTimer(percentageOfCircle)
        display.addActor(clock)
    }

    private fun createTimer(percentageOfCircle: Float): Image {
        val radius: Float = (width / 2f).coerceAtMost(height / 2f)
        val angle = calculateAngle(percentageOfCircle)
        val segments = calculateSegments(angle)
        val pixmap = Pixmap(width.toInt(), height.toInt(), Pixmap.Format.RGBA8888)
        val theta: Float = 2f * MathUtils.PI * (angle / 360f) / segments
        val cos: Float = MathUtils.cos(theta)
        val sin: Float = MathUtils.sin(theta)

        var cx: Float = radius * MathUtils.cos(START_ANGLE * MathUtils.degreesToRadians)
        var cy: Float = radius * MathUtils.sin(-1 * START_ANGLE * MathUtils.degreesToRadians)

        pixmap.setColor(color)

        (0 until segments).forEach { _ ->
            val pcx = cx
            val pcy = cy
            val temp = cx
            cx = cos * cx - sin * cy
            cy = sin * temp + cos * cy
            pixmap.fillTriangle(
                width.toInt() / 2,
                height.toInt() / 2,
                (width / 2f + pcx).toInt(),
                (height / 2f + pcy).toInt(),
                (width / 2f + cx).toInt(),
                (height / 2f + cy).toInt()
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
            clampedPercentage <= 0.125 -> {
                Color.RED
            }

            clampedPercentage <= 0.375 -> {
                val startColor = Color.RED.cpy()
                val endColor = Color.ORANGE
                startColor.lerp(endColor, (clampedPercentage - 0.125f) / 0.25f)
            }

            clampedPercentage <= 0.625 -> {
                val startColor = Color.ORANGE.cpy()
                val endColor = Color.GOLD
                startColor.lerp(endColor, (clampedPercentage - 0.375f) / 0.25f)
            }

            clampedPercentage <= 0.875 -> {
                val startColor = Color.GOLD.cpy()
                val endColor = Color.LIME
                startColor.lerp(endColor, (clampedPercentage - 0.625f) / 0.25f)
            }

            else -> {
                Color.LIME
            }
        }
    }

    private fun calculateAngle(remainingPercentage: Float): Float {
        return 360f - (360f * remainingPercentage)
    }

    private fun calculateSegments(angle: Float): Int {
        return max(1, (6f * cbrt(abs(angle).toDouble()) * (abs(angle) / 360f)).toInt())
    }

}
