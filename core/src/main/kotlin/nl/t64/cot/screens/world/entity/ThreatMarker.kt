package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.components.battle.ThreatLevel
import nl.t64.cot.constants.Constant
import kotlin.math.sin


private const val CHEVRON_WIDTH = 18f
private const val CHEVRON_HEIGHT = 11f
private const val DASH_WIDTH = 16f
private const val DASH_HEIGHT = 4f
private const val OUTLINE = 2f
private const val STACK_STEP = 7f

private const val MARGIN_ABOVE_HEAD = 6f
private const val BOB_AMPLITUDE = 3f
private const val BOB_SPEED = 4f

/**
 * Tekent boven een vijand een zwevende threat-indicator: chevrons waarvan vorm en kleur aangeven hoe
 * zwaar de battle is voor de huidige party (zie [ThreatLevel]). ▼▼ trivial, ▼ zwakker, ▬ gelijk,
 * ▲ sterker, ▲▲ dodelijk. Elke glyph krijgt een zwarte rand voor contrast tegen elke achtergrond.
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
            ThreatLevel.DEADLY -> {
                drawChevron(batch, centerX, baseY, pointsUp = true)
                drawChevron(batch, centerX, baseY + STACK_STEP, pointsUp = true)
            }
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

        private fun createChevronTexture(pointsUp: Boolean): Texture {
            val width = 48
            val height = 28
            val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
            pixmap.setColor(Color.WHITE)
            // Pixmap-y loopt van boven (0) naar onder; een opwaartse pijl heeft de punt dus bovenaan.
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
    }

}
