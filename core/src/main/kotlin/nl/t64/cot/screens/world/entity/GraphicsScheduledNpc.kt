package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.DirectionEvent
import nl.t64.cot.screens.world.entity.events.Event
import nl.t64.cot.screens.world.entity.events.UpdateScheduledEntityEvent


class GraphicsScheduledNpc(spriteId: String) : GraphicsComponent() {

    private var stateMarker: StateMarker? = null
    private var currentStateIcon: StateIcon? = null

    init {
        frameDuration = Constant.NORMAL_FRAMES
        loadWalkingAnimation(spriteId)
    }

    override fun receive(event: Event) {
        if (event is UpdateScheduledEntityEvent) {
            state = event.state
            position = event.position
            direction = event.direction
            if (event.stateIcon != currentStateIcon) {
                currentStateIcon = event.stateIcon
                stateMarker = event.stateIcon?.let { StateMarker(it) }
            }
            setNewFrameDuration()
        }
        if (event is DirectionEvent) {
            direction = event.direction
        }
    }

    private fun setNewFrameDuration() {
        setNewFrameDuration(0f)     // with overridden way of getting the new frame duration.
    }

    override fun getFrameDuration(moveSpeed: Float): Float {
        return when (state) {
            EntityState.CRAWLING -> Constant.SLOW_FRAMES
            EntityState.WALKING -> Constant.NORMAL_FRAMES
            EntityState.RUNNING -> Constant.FAST_FRAMES
            else -> frameDuration
        }
    }

    override fun update(dt: Float) {
        setFrame(dt)
        stateMarker?.update(dt)
    }

    override fun render(batch: Batch) {
        batch.draw(currentFrame, position.x, position.y, Constant.TILE_SIZE, Constant.TILE_SIZE)
        if (state != EntityState.INVISIBLE) {
            stateMarker?.render(batch, position)
        }
    }

    override fun renderOnMiniMap(entity: Entity, batch: Batch, shapeRenderer: ShapeRenderer) {
        renderOnMiniMap(entity.getConversationId(), state, position, batch, shapeRenderer)
    }

}
