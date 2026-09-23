package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.components.battle.ThreatLevel
import nl.t64.cot.constants.Constant
import kotlin.math.abs
import kotlin.math.sin


private const val CHEVRON_WIDTH = 18f
private const val CHEVRON_HEIGHT = 11f
private const val DASH_WIDTH = 16f
private const val DASH_HEIGHT = 4f
private const val X_WIDTH = 24f
private const val X_HEIGHT = 24f
private const val OUTLINE = 2f
private const val STACK_STEP = 7f
private const val DIAGONAL = 0.70710677f // 1/√2: component of a diagonal unit vector.

private const val MARGIN_ABOVE_HEAD = 6f
private const val BOB_AMPLITUDE = 3f
private const val BOB_SPEED = 4f

/**
 * Draws a floating threat indicator above an enemy: chevrons whose shape and color show how
 * hard the battle is for the current party (see [ThreatLevel]). ▼▼ trivial, ▼ weaker, ▬ even,
 * ▲ stronger, ▲▲ dangerous, ✖ deadly. Every glyph gets a black outline for contrast against any background.
 */
class ThreatMarker(
    private val threatLevel: ThreatLevel
): OverheadMarker {
    private var bobTime = 0f

    override fun update(dt: Float) {
        bobTime += dt
    }

    override fun render(batch: Batch, position: Vector2) {
        val centerX: Float = position.x + Constant.TILE_SIZE / 2f
        val baseY: Float = position.y + Constant.TILE_SIZE + MARGIN_ABOVE_HEAD + sin(bobTime * BOB_SPEED) * BOB_AMPLITUDE
        val previousColor: Float = batch.packedColor

        when (threatLevel) {
            ThreatLevel.TRIVIAL -> {
                drawChevron(batch, centerX, baseY, pointsUp = false)
                drawChevron(batch, centerX, baseY - STACK_STEP, pointsUp = false)
            }
            ThreatLevel.WEAKER -> drawChevron(batch, centerX, baseY, pointsUp = false)
            ThreatLevel.EVEN -> drawDash(batch, centerX, baseY)
            ThreatLevel.STRONGER -> drawChevron(batch, centerX, baseY, pointsUp = true)
            ThreatLevel.DANGEROUS -> {
                drawChevron(batch, centerX, baseY, pointsUp = true)
                drawChevron(batch, centerX, baseY + STACK_STEP, pointsUp = true)
            }
            ThreatLevel.DEADLY -> drawX(batch, centerX, baseY)
        }

        batch.packedColor = previousColor
    }

    private fun drawChevron(batch: Batch, centerX: Float, bottomY: Float, pointsUp: Boolean) {
        val texture: Texture = if (pointsUp) chevronUpTexture else chevronDownTexture
        val x: Float = centerX - CHEVRON_WIDTH / 2f
        drawWithOutline(batch, texture, x, bottomY, CHEVRON_WIDTH, CHEVRON_HEIGHT)
    }

    private fun drawDash(batch: Batch, centerX: Float, bottomY: Float) {
        val x: Float = centerX - DASH_WIDTH / 2f
        drawWithOutline(batch, pixelTexture, x, bottomY, DASH_WIDTH, DASH_HEIGHT)
    }

    private fun drawX(batch: Batch, centerX: Float, bottomY: Float) {
        // The black outline is already baked into the texture (see createXTexture), so no drawWithOutline:
        // a scaled-up separate outline would make black points stick out at the sharp tips.
        val x: Float = centerX - X_WIDTH / 2f
        batch.color = threatLevel.color
        batch.draw(xTexture, x, bottomY, X_WIDTH, X_HEIGHT)
    }

    private fun drawWithOutline(batch: Batch, texture: Texture, x: Float, y: Float, width: Float, height: Float) {
        batch.color = Color.BLACK
        batch.draw(texture, x - OUTLINE, y - OUTLINE, width + OUTLINE * 2, height + OUTLINE * 2)
        batch.color = threatLevel.color
        batch.draw(texture, x, y, width, height)
    }

    companion object {
        private val pixelTexture: Texture by lazy {
            val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
            pixmap.setColor(Color.WHITE)
            pixmap.fill()
            Texture(pixmap).also { pixmap.dispose() }
        }
        private val chevronUpTexture: Texture by lazy { createChevronTexture(pointsUp = true) }
        private val chevronDownTexture: Texture by lazy { createChevronTexture(pointsUp = false) }
        private val xTexture: Texture by lazy { createXTexture() }

        private fun createChevronTexture(pointsUp: Boolean): Texture {
            val width = 48
            val height = 28
            val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
            pixmap.setColor(Color.WHITE)
            // Pixmap y runs from top (0) to bottom, so an upward arrow has its point at the top.
            if (pointsUp) {
                pixmap.fillTriangle(width / 2, 0, 0, height - 1, width - 1, height - 1)
            } else {
                pixmap.fillTriangle(0, 0, width - 1, 0, width / 2, height - 1)
            }
            return Texture(pixmap).also {
                it.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
                pixmap.dispose()
            }
        }

        // A diagonal cross (✖): two diagonal bars from corner to corner, with the black outline baked in.
        // Deliberately not a medical plus sign, which reads as healing. Each bar is a rotated
        // rectangle (box), so the ends are cut off square — sharp and aggressive, not round.
        // White if the pixel falls within the narrow box; around it, up to border, black.
        // The texture is deliberately larger than the X itself: the black outline sticks out diagonally at the tips
        // and would otherwise be clipped by the texture edge (no black in the outer corners).
        private fun createXTexture(): Texture {
            val size = 60
            val center: Float = (size - 1) / 2f
            val halfThickness = 4.5f
            val border = 3.5f
            val halfLength = 28.5f
            val white: Int = Color.rgba8888(Color.WHITE)
            val black: Int = Color.rgba8888(Color.BLACK)
            val pixmap = Pixmap(size, size, Pixmap.Format.RGBA8888)
            for (y in 0 until size) {
                for (x in 0 until size) {
                    val relX: Float = x - center
                    val relY: Float = y - center
                    val onWhite: Boolean =
                        isInsideArm(relX, relY, DIAGONAL, DIAGONAL, halfLength, halfThickness) ||
                        isInsideArm(relX, relY, DIAGONAL, -DIAGONAL, halfLength, halfThickness)
                    val onBlack: Boolean =
                        isInsideArm(relX, relY, DIAGONAL, DIAGONAL, halfLength + border, halfThickness + border) ||
                        isInsideArm(relX, relY, DIAGONAL, -DIAGONAL, halfLength + border, halfThickness + border)
                    when {
                        onWhite -> pixmap.drawPixel(x, y, white)
                        onBlack -> pixmap.drawPixel(x, y, black)
                    }
                }
            }
            return Texture(pixmap).also {
                it.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
                pixmap.dispose()
            }
        }

        // Is (relX, relY) inside the rotated rectangle with axis direction (dirX, dirY)? halfLength is half the
        // length along the axis, halfWidth half the thickness perpendicular to it. The right angles make the sharp tips.
        private fun isInsideArm(
            relX: Float, relY: Float, dirX: Float, dirY: Float, halfLength: Float, halfWidth: Float,
        ): Boolean {
            val along: Float = relX * dirX + relY * dirY
            val across: Float = relX * -dirY + relY * dirX
            return abs(along) <= halfLength && abs(across) <= halfWidth
        }
    }

}
