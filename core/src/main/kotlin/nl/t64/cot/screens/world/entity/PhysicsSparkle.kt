package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.Event
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent
import nl.t64.cot.screens.world.entity.events.OnActionEvent


class PhysicsSparkle(private val sparkle: Loot) : PhysicsComponent() {

    private var isSelected = false

    override fun receive(event: Event) {
        if (event is LoadEntityEvent) {
            currentPosition = event.position
            setBoundingBox()
        }
        if (event is OnActionEvent) {
            isSelected = true
        }
    }

    override fun update(entity: Entity, dt: Float) {
        if (isSelected) {
            isSelected = false
            worldScreen.showFindScreen(sparkle, AudioEvent.SE_SPARKLE)
        }
    }

    override fun setBoundingBox() {
        boundingBox.set(currentPosition.x, currentPosition.y, Constant.TILE_SIZE, Constant.TILE_SIZE)
    }

    override fun debug(shapeRenderer: ShapeRenderer) {
        // empty
    }

}
