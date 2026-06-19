package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.constants.Constant
import kotlin.math.sin


private const val SPRITE_LAUREL = "sprites/laurel_crown.png"

private const val MARGIN_ABOVE_HEAD = 6f
private const val BOB_AMPLITUDE = 3f
private const val BOB_SPEED = 4f

private const val ICON_SIZE = 32f
private const val ALPHA = 0.6f
private val LAUREL_COLOR = Color(Color.GOLD).apply { a = ALPHA }

class LaurelMarker : OverheadMarker {

    private var bobTime = 0f

    companion object {
        private val laurelTexture: Texture by lazy {
            resourceManager.getTextureAsset(SPRITE_LAUREL)
        }
    }

    override fun update(dt: Float) {
        bobTime += dt
    }

    override fun render(batch: Batch, position: Vector2) {
        val x: Float = position.x + Constant.TILE_SIZE / 2f - ICON_SIZE / 2f
        val y: Float = position.y + Constant.TILE_SIZE + MARGIN_ABOVE_HEAD + sin(bobTime * BOB_SPEED) * BOB_AMPLITUDE
        val previousColor: Float = batch.packedColor

        batch.color = LAUREL_COLOR
        batch.draw(laurelTexture, x, y, ICON_SIZE, ICON_SIZE)

        batch.packedColor = previousColor
    }

}
