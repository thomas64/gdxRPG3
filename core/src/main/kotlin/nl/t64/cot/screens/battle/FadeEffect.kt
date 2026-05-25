package nl.t64.cot.screens.battle

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table


class FadeEffect(
    private val battleFieldTable: Table,
    private val currentTargetName: String,
    private val initialDelay: Float = 0.2f,
    private val fadeDuration: Float = 1f
) {

    fun start() {
        val target: Actor? = findActorIn(battleFieldTable)
        target?.addAction(Actions.sequence(
            Actions.delay(initialDelay),
            Actions.fadeOut(fadeDuration),
            Actions.removeActor()
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
