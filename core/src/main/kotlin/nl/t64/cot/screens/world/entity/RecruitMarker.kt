package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.constants.Constant
import kotlin.math.sin


private const val BAR_WIDTH = 5f
private const val BAR_HEIGHT = 13f
private const val DOT_WIDTH = 5f
private const val DOT_HEIGHT = 5f
private const val GAP = 4f
private const val OUTLINE = 2f

private const val MARGIN_ABOVE_HEAD = 6f
private const val BOB_AMPLITUDE = 3f
private const val BOB_SPEED = 4f

class RecruitMarker {

    private var bobTime = 0f

    companion object {
        private val pixelTexture: Texture by lazy {
            val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
            pixmap.setColor(Color.WHITE)
            pixmap.fill()
            Texture(pixmap).also { pixmap.dispose() }
        }
    }

    fun update(dt: Float) {
        bobTime += dt
    }

    fun render(batch: Batch, position: Vector2) {
        val centerX: Float = position.x + Constant.TILE_SIZE / 2f
        val baseY: Float = position.y + Constant.TILE_SIZE + MARGIN_ABOVE_HEAD + sin(bobTime * BOB_SPEED) * BOB_AMPLITUDE
        val previousColor: Float = batch.packedColor

        drawRect(batch, centerX, baseY, DOT_WIDTH, DOT_HEIGHT)
        drawRect(batch, centerX, baseY + DOT_HEIGHT + GAP, BAR_WIDTH, BAR_HEIGHT)

        batch.packedColor = previousColor
    }

    private fun drawRect(batch: Batch, centerX: Float, bottomY: Float, width: Float, height: Float) {
        val x: Float = centerX - width / 2f
        batch.color = Color.BLACK
        batch.draw(pixelTexture, x - OUTLINE, bottomY - OUTLINE, width + OUTLINE * 2f, height + OUTLINE * 2f)
        batch.color = Color.GOLD
        batch.draw(pixelTexture, x, bottomY, width, height)
    }

}
