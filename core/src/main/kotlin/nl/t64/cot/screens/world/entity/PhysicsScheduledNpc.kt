package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.screens.world.entity.events.Event
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent
import nl.t64.cot.screens.world.entity.events.PositionEvent


class PhysicsScheduledNpc : PhysicsComponent() {

    private var isSelected = false

    override fun receive(event: Event) {
        if (event is LoadEntityEvent) {
            state = event.state!!
            currentPosition = event.position
            direction = event.direction!!
        }
    }

    override fun update(entity: Entity, dt: Float) {
        entity.send(PositionEvent(currentPosition))
    }

    override fun debug(shapeRenderer: ShapeRenderer) {
        // empty
    }

}
