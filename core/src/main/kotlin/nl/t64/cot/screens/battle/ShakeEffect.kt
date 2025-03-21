package nl.t64.cot.screens.battle

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table


class ShakeEffect(
    private val battleFieldTable: Table,
    private val currentTargetName: String
) {

    fun start() {
        val target: Actor? = findActorIn(battleFieldTable)
        target?.addAction(Actions.sequence(
            Actions.delay(0.4f),
            ShakeAction()
        ))
    }

    private fun findActorIn(root: Group): Actor? {
        root.children.forEach { actor ->
            if (actor.name == currentTargetName) return actor
            if (actor is Group) findActorIn(actor)?.let { return it }
        }
        return null
    }
}


private class ShakeAction(
    private val intensity: Float = 20f,
    private val duration: Float = 0.3f
) : Action() {
    private var elapsedTime = 0f
    private var originalX = 0f
    private var originalY = 0f

    override fun act(delta: Float): Boolean {
        if (elapsedTime == 0f) {
            originalX = actor.x
            originalY = actor.y
        }

        elapsedTime += delta
        if (elapsedTime < duration) {
            val shakeAmount: Float = intensity * (1 - elapsedTime / duration)
            actor.setPosition(
                originalX + (Math.random().toFloat() - 0.5f) * shakeAmount,
                originalY + (Math.random().toFloat() - 0.5f) * shakeAmount
            )
            return false
        }
        actor.setPosition(originalX, originalY)
        return true
    }
}
