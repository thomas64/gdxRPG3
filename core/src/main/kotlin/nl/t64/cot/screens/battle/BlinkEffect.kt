package nl.t64.cot.screens.battle

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table


class BlinkEffect(
    private val battleFieldTable: Table,
    private val currentTargetName: String,
    private val blinkColor: Color
) {

    fun start() {
        val target: Actor? = findActorIn(battleFieldTable)
        target?.addAction(Actions.sequence(
            Actions.color(blinkColor, 0.1f),
            Actions.color(Color.WHITE, 0.1f),
            Actions.color(blinkColor, 0.1f),
            Actions.color(Color.WHITE, 0.1f)
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
