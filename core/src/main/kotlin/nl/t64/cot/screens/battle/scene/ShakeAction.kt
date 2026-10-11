package nl.t64.cot.screens.battle.scene

import com.badlogic.gdx.scenes.scene2d.Action
import kotlin.random.Random


internal class ShakeAction(
    private val intensity: Float,
    private val duration: Float
) : Action() {

    private var elapsedTime: Float = 0f
    private var originalX: Float = 0f
    private var originalY: Float = 0f

    override fun act(delta: Float): Boolean {
        if (elapsedTime == 0f) {
            originalX = actor.x
            originalY = actor.y
        }

        elapsedTime += delta
        if (elapsedTime < duration) {
            val shakeAmount: Float = intensity * (1 - elapsedTime / duration)
            actor.setPosition(
                originalX + (Random.nextFloat() - 0.5f) * shakeAmount,
                originalY + (Random.nextFloat() - 0.5f) * shakeAmount
            )
            return false
        }
        actor.setPosition(originalX, originalY)
        return true
    }

}
