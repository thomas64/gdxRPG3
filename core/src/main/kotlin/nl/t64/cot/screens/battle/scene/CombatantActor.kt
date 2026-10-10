package nl.t64.cot.screens.battle.scene

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor


abstract class CombatantActor(
    protected val region: TextureRegion
) : Actor() {

    val centerX: Float get() = x + (width / 2f)
    val centerY: Float get() = y + (height / 2f)

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha)
        drawMirrored(batch, getCurrentRegion())
        batch.color = Color.WHITE
    }

    protected abstract fun getCurrentRegion(): TextureRegion

    private fun drawMirrored(batch: Batch, region: TextureRegion) {
        batch.draw(region, x + width, y, -width, height)
    }

}
