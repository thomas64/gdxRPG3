package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.components.battle.ThreatLevel
import nl.t64.cot.constants.Constant
import kotlin.math.sin


private const val SPRITE_LAUREL = "sprites/laurel_crown.png"

private const val MARGIN_ABOVE_HEAD = 6f
private const val BOB_AMPLITUDE = 3f
private const val BOB_SPEED = 4f

private const val ICON_SIZE = 32f
private const val OUTLINE = 1f
private const val OUTLINE_DIRECTIONS = 32
private const val ALPHA = 0.6f
private const val ALPHA_MASK = 0x000000FF

class LaurelMarker(threatLevel: ThreatLevel) : OverheadMarker {

    private val laurelColor = Color(threatLevel.color).apply { a = ALPHA }
    private var bobTime = 0f

    companion object {
        private val laurelTexture: Texture by lazy { createOutlinedTexture() }

        private fun createOutlinedTexture(): Texture {
            val source = Pixmap(Gdx.files.internal(SPRITE_LAUREL))
            val radius: Int = (OUTLINE * source.width / ICON_SIZE).toInt()
            val silhouette: Pixmap = createBlackSilhouette(source)
            val result = Pixmap(source.width + radius * 2, source.height + radius * 2, Pixmap.Format.RGBA8888)
            for (i in 0 until OUTLINE_DIRECTIONS) {
                val angle: Float = MathUtils.PI2 * i / OUTLINE_DIRECTIONS
                val offsetX: Int = MathUtils.round(MathUtils.cos(angle) * radius)
                val offsetY: Int = MathUtils.round(MathUtils.sin(angle) * radius)
                result.drawPixmap(silhouette, radius + offsetX, radius + offsetY)
            }
            result.drawPixmap(source, radius, radius)
            silhouette.dispose()
            source.dispose()
            return Texture(result, true).also {
                it.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear)
                result.dispose()
            }
        }

        private fun createBlackSilhouette(source: Pixmap): Pixmap {
            val silhouette = Pixmap(source.width, source.height, Pixmap.Format.RGBA8888)
            silhouette.blending = Pixmap.Blending.None
            for (y in 0 until source.height) {
                for (x in 0 until source.width) {
                    silhouette.drawPixel(x, y, source.getPixel(x, y) and ALPHA_MASK)
                }
            }
            return silhouette
        }
    }

    override fun update(dt: Float) {
        bobTime += dt
    }

    override fun render(batch: Batch, position: Vector2) {
        val x: Float = position.x + Constant.TILE_SIZE / 2f - ICON_SIZE / 2f
        val y: Float = position.y + Constant.TILE_SIZE + MARGIN_ABOVE_HEAD + sin(bobTime * BOB_SPEED) * BOB_AMPLITUDE
        val previousColor: Float = batch.packedColor

        batch.color = laurelColor
        batch.draw(laurelTexture, x - OUTLINE, y - OUTLINE, ICON_SIZE + OUTLINE * 2, ICON_SIZE + OUTLINE * 2)

        batch.packedColor = previousColor
    }

}
