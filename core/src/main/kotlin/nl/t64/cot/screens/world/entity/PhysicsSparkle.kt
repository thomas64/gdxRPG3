package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.Event
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent
import nl.t64.cot.screens.world.entity.events.OnActionEvent


class PhysicsSparkle(
    private val sparkle: Loot
) : PhysicsComponent() {

    private var isSelected: Boolean = false

    override fun receive(event: Event) {
        if (event is LoadEntityEvent) {
            currentPosition = event.position
            setBoundingBox()
        }
        if (event is OnActionEvent) {
            if (event.checkRect.overlaps(boundingBox)) {
                isSelected = true
            }
        }
    }

    override fun update(entity: Entity, dt: Float) {
        if (isSelected) {
            isSelected = false
            tryToGatherSparkle()
        }
    }

    override fun setBoundingBox() {
        boundingBox.set(currentPosition.x, currentPosition.y, Constant.TILE_SIZE, Constant.TILE_SIZE)
    }

    private fun tryToGatherSparkle() {
        if (sparkle.canBeGatheredByParty()) {
            worldScreen.showFindScreen(sparkle, AudioEvent.SE_SPARKLE)
        } else {
            showRangerTooLowMessage()
        }
    }

    private fun showRangerTooLowMessage() {
        val message: String = ("There's something here, but you can't tell what it is."
            + System.lineSeparator() + "You need a Ranger of rank ${sparkle.gatherLevel} to gather it.")
        worldScreen.showMessageDialog(message)
    }

    override fun debug(shapeRenderer: ShapeRenderer) {
        // empty
    }

}
