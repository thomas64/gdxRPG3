package nl.t64.cot.screens.battle.scene

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import nl.t64.cot.Utils.resourceManager


private const val SPRITE_PATH = "sprites/animations/"
private const val CELL_SIZE = 192
private const val CELLS_PER_ROW = 5
private const val BLEND_ADD = 1

class AttackAnimationActor(
    private val attackAnimation: AttackAnimation,
    private val animationScale: Float,
    private val isAttackFromRight: Boolean,
    private val onTargetFlash: (Color, Float) -> Unit
) : Actor() {

    private val texture: Texture = resourceManager.getTextureAsset("$SPRITE_PATH${attackAnimation.spriteName}.png")
    private val direction: Float = if (isAttackFromRight) -1f else 1f
    private var elapsedTime: Float = 0f
    private var lastHandledFrame: Int = -1
    private val currentFrame: Int get() = (elapsedTime / attackAnimation.frameDurationInSeconds).toInt()

    override fun act(delta: Float) {
        super.act(delta)
        elapsedTime += delta
        if (currentFrame >= attackAnimation.frames.size) {
            remove()
        } else {
            handleFlashesUntil(currentFrame)
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (currentFrame >= attackAnimation.frames.size) return

        attackAnimation.frames[currentFrame].forEach { drawCell(batch, it, parentAlpha) }
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        batch.color = Color.WHITE
    }

    // frames can be skipped in a slow moment, so every frame since the last one is handled.
    private fun handleFlashesUntil(frame: Int) {
        for (frameToHandle in (lastHandledFrame + 1)..frame) {
            attackAnimation.flashes
                .filter { it.atAnimationFrame == frameToHandle }
                .forEach { onTargetFlash.invoke(it.toColor(), it.frameCount * attackAnimation.frameDurationInSeconds) }
        }
        lastHandledFrame = frame
    }

    private fun drawCell(batch: Batch, cell: AnimationCell, parentAlpha: Float) {
        val srcX: Int = (cell.index % CELLS_PER_ROW) * CELL_SIZE
        val srcY: Int = (cell.index / CELLS_PER_ROW) * CELL_SIZE
        val half: Float = CELL_SIZE / 2f
        val cellScale: Float = cell.scale * animationScale

        if (cell.blend == BLEND_ADD) {
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
        } else {
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        }
        batch.setColor(1f, 1f, 1f, cell.opacity * color.a * parentAlpha)
        // the data counts y downwards and rotates clockwise, libGDX does both the other way around.
        batch.draw(texture,
                   x + (cell.x * animationScale * direction) - half,
                   y - (cell.y * animationScale) - half,
                   half, half,
                   CELL_SIZE.toFloat(), CELL_SIZE.toFloat(),
                   cellScale, cellScale,
                   -cell.rotation * direction,
                   srcX, srcY, CELL_SIZE, CELL_SIZE,
                   cell.mirror != isAttackFromRight, false)
    }

}
