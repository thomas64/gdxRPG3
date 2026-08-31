package nl.t64.cot.screens.battle.effects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table


private const val BLINK_DURATION = 0.1f

class BlinkEffect(
    private val battleFieldTable: Table,
    private val currentTargetName: String,
    private vararg val blinkColors: Color
) {

    fun start() {
        val target: Actor = findActorIn(battleFieldTable) ?: return
        val colorSequence: List<Color> = createColorSequence()
        if (colorSequence.isEmpty()) return

        target.addAction(Actions.sequence(
            *colorSequence.map { Actions.color(it, BLINK_DURATION) }.toTypedArray()
        ))
    }

    private fun createColorSequence(): List<Color> {
        if (blinkColors.isEmpty()) return emptyList()

        if (blinkColors.size == 1) {
            return listOf(blinkColors[0], Color.WHITE, blinkColors[0], Color.WHITE)
        }

        val sequence = mutableListOf<Color>()
        blinkColors.forEachIndexed { index, color ->
            sequence.add(color)
            if (color != Color.WHITE && index < blinkColors.lastIndex) {
                sequence.add(Color.WHITE)
            }
        }

        if (sequence.lastOrNull() != Color.WHITE) {
            sequence.add(Color.WHITE)
        }

        return sequence
    }

    private fun findActorIn(root: Group): Actor? {
        root.children.forEach { actor ->
            if (actor.name == currentTargetName) return actor
            if (actor is Group) findActorIn(actor)?.let { return it }
        }
        return null
    }
}
