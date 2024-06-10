package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.screens.world.entity.events.Event


class GraphicsEmpty : GraphicsComponent() {

    override fun receive(event: Event) {
        // empty
    }

    override fun update(dt: Float) {
        // empty
    }

    override fun render(batch: Batch) {
        // empty
    }

    override fun renderOnMiniMap(entity: Entity, batch: Batch, shapeRenderer: ShapeRenderer) {
        // empty
    }

}
