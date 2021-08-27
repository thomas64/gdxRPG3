package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.screens.world.entity.events.Event


class PhysicsEmpty : PhysicsComponent() {

    override fun receive(event: Event) {
        // empty
    }

    override fun update(entity: Entity, dt: Float) {
        // empty
    }

    override fun debug(shapeRenderer: ShapeRenderer) {
        // empty
    }

}
