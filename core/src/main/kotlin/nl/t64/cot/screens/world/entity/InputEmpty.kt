package nl.t64.cot.screens.world.entity

import nl.t64.cot.screens.world.entity.events.Event


class InputEmpty : InputComponent() {

    override fun receive(event: Event) {
        // empty
    }

    override fun update(entity: Entity, dt: Float) {
        // empty
    }

}
