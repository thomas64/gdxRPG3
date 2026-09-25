package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.components.battle.ThreatLevel
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.DirectionEvent
import nl.t64.cot.screens.world.entity.events.Event
import nl.t64.cot.screens.world.entity.events.RemoveScheduledEntityEvent
import nl.t64.cot.screens.world.entity.events.UpdateScheduledEntityEvent


class GraphicsScheduledNpc(spriteId: String) : GraphicsComponent() {

    private var stateMarker: StateMarker? = null
    private var currentStateIcon: StateIcon? = null
    private var threatMarker: ThreatMarker? = null
    private var isEnemy: Boolean = false

    init {
        frameDuration = Constant.NORMAL_FRAMES
        loadWalkingAnimation(spriteId)
    }

    override fun receive(event: Event) {
        if (event is UpdateScheduledEntityEvent) {
            state = event.state
            position = event.position
            direction = event.direction
            updateStateMarker(event)
            updateThreatMarker(event)
            setNewFrameDuration()
            refreshCurrentFrame()
        }
        if (event is DirectionEvent) {
            direction = event.direction
        }
        if (event is RemoveScheduledEntityEvent) {
            forgetThreatMarker()
        }
    }

    private fun updateStateMarker(event: UpdateScheduledEntityEvent) {
        if (event.stateIcon == currentStateIcon) return
        currentStateIcon = event.stateIcon
        stateMarker = event.stateIcon?.let { StateMarker(it) }
    }

    private fun updateThreatMarker(event: UpdateScheduledEntityEvent) {
        if (event.isEnemy == isEnemy) return
        isEnemy = event.isEnemy
        threatMarker = if (isEnemy) ThreatMarker(ThreatLevel.forBattle(event.conversationId)) else null
    }

    private fun forgetThreatMarker() {
        isEnemy = false
        threatMarker = null
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
        threatMarker?.update(dt)
    }

    override fun render(batch: Batch) {
        batch.draw(currentFrame, position.x, position.y, Constant.TILE_SIZE, Constant.TILE_SIZE)
        if (state != EntityState.INVISIBLE) {
            stateMarker?.render(batch, position)
            threatMarker?.render(batch, position)
        }
    }

    override fun renderOnMiniMap(entity: Entity, batch: Batch, shapeRenderer: ShapeRenderer) {
        renderOnMiniMap(entity.getConversationId(), state, position, batch, shapeRenderer)
    }

}
