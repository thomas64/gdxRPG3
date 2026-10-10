package nl.t64.cot.screens.battle.scene

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor


abstract class CombatantActor(
    protected val region: TextureRegion
) : Actor() {

    private var flashColor: Color = Color.WHITE
    private var flashDuration: Float = 0f
    private var flashTimeLeft: Float = 0f

    val centerX: Float get() = x + (width / 2f)
    val centerY: Float get() = y + (height / 2f)

    fun flash(color: Color, duration: Float) {
        flashColor = color
        flashDuration = duration
        flashTimeLeft = duration
    }

    override fun act(delta: Float) {
        super.act(delta)
        flashTimeLeft = (flashTimeLeft - delta).coerceAtLeast(0f)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val region: TextureRegion = getCurrentRegion()
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)
        drawMirrored(batch, region)
        if (flashTimeLeft > 0f) {
            drawFlash(batch, region, parentAlpha)
        }
        batch.color = Color.WHITE
    }

    protected abstract fun getCurrentRegion(): TextureRegion

    // drawing the same image again with additive blending makes it light up. twice, so that it lights up enough.
    private fun drawFlash(batch: Batch, region: TextureRegion, parentAlpha: Float) {
        val flashAlpha: Float = flashColor.a * (flashTimeLeft / flashDuration)
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
        batch.setColor(flashColor.r, flashColor.g, flashColor.b, flashAlpha * color.a * parentAlpha)
        drawMirrored(batch, region)
        drawMirrored(batch, region)
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    }

    private fun drawMirrored(batch: Batch, region: TextureRegion) {
        batch.draw(region, x + width, y, -width, height)
    }

}
