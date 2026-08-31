package nl.t64.cot.screens.battle.effects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.screens.FontProvider


class FloatingNumberEffect(
    private val battleFieldTable: Table,
    private val currentTargetName: String,
    private val textToFloat: String,
    private val color: Color,
) {

    fun floatUp() {
        findActorIn(battleFieldTable)?.let {
            createFloatingNumber(it, initialYOffset = -15f, moveByY = 40f)
        }
    }

    fun floatDown() {
        findActorIn(battleFieldTable)?.let {
            createFloatingNumber(it, initialYOffset = 25f, moveByY = -40f)
        }
    }

    private fun findActorIn(root: Group): Actor? {
        root.children.forEach { actor ->
            if (actor.name == currentTargetName) return actor
            if (actor is Group) findActorIn(actor)?.let { return it }
        }
        return null
    }

    private fun createFloatingNumber(target: Actor, initialYOffset: Float, moveByY: Float) {
        val label = Label(textToFloat, LabelStyle(FontProvider.default, color))
        label.calculatePosition(target, initialYOffset)
        battleFieldTable.addActor(label)

        label.addAction(Actions.sequence(
            Actions.alpha(0f),
            Actions.fadeIn(0.1f),
            Actions.moveBy(0f, moveByY, 0.8f),
            Actions.fadeOut(0.1f),
            Actions.removeActor()
        ))
    }

    private fun Label.calculatePosition(target: Actor, initialYOffset: Float) {
        val targetCenterX = target.width / 2f
        val targetCenterY = target.height + initialYOffset
        val localPos = Vector2(targetCenterX, targetCenterY)
        val worldPos = target.localToAscendantCoordinates(battleFieldTable, localPos)
        this.setPosition(worldPos.x + 4f, worldPos.y)
    }
}
