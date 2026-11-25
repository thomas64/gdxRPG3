package nl.t64.cot.screens.battle.scene

import com.badlogic.gdx.scenes.scene2d.Action
import nl.t64.cot.constants.Constant
import kotlin.random.Random


private const val SECONDS_BETWEEN_OFFSETS = 1f / Constant.TUNED_AT_FRAMES_PER_SECOND

internal class ShakeAction(
    private val intensity: Float,
    private val duration: Float
) : Action() {

    private var elapsedTime: Float = 0f
    private var secondsSinceNewOffset: Float = 0f
    private var originalX: Float = 0f
    private var originalY: Float = 0f
    private var offsetFractionX: Float = 0f
    private var offsetFractionY: Float = 0f

    override fun act(delta: Float): Boolean {
        if (elapsedTime == 0f) {
            originalX = actor.x
            originalY = actor.y
            seedRandomOffset()
        }

        elapsedTime += delta
        if (elapsedTime < duration) {
            possibleSeedNewOffset(delta)
            val shakeAmount: Float = intensity * (1 - elapsedTime / duration)
            actor.setPosition(originalX + offsetFractionX * shakeAmount,
                              originalY + offsetFractionY * shakeAmount)
            return false
        }
        actor.setPosition(originalX, originalY)
        return true
    }

    // A new offset every frame would make the shake vibrate faster on a machine that renders more frames.
    // It rattles at a fixed rate now, which is what it did at TUNED_AT_FRAMES_PER_SECOND before.
    private fun possibleSeedNewOffset(delta: Float) {
        secondsSinceNewOffset += delta
        if (secondsSinceNewOffset < SECONDS_BETWEEN_OFFSETS) return

        secondsSinceNewOffset %= SECONDS_BETWEEN_OFFSETS
        seedRandomOffset()
    }

    private fun seedRandomOffset() {
        offsetFractionX = Random.nextFloat() - 0.5f
        offsetFractionY = Random.nextFloat() - 0.5f
    }

}
