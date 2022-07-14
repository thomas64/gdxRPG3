package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.Event
import nl.t64.cot.screens.world.entity.events.UpdateScheduledEntityEvent


class GraphicsScheduledNpc(spriteId: String) : GraphicsComponent() {

    init {
        frameDuration = Constant.NORMAL_FRAMES
        loadWalkingAnimation(spriteId)
    }

    override fun receive(event: Event) {
        if (event is UpdateScheduledEntityEvent) {
            state = event.state
            position = event.position
            direction = event.direction
        }
    }

    override fun update(dt: Float) {
        setFrame(dt)
    }

    override fun render(batch: Batch) {
        batch.draw(currentFrame, position.x, position.y, Constant.TILE_SIZE, Constant.TILE_SIZE)
    }

    override fun renderOnMiniMap(entity: Entity, batch: Batch, shapeRenderer: ShapeRenderer) {
        // empty
    }

}
