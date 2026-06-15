package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.components.battle.ThreatLevel
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.*


class GraphicsEnemy(spriteId: String) : GraphicsComponent() {

    private lateinit var threatMarker: ThreatMarker

    init {
        frameDuration = Constant.NORMAL_FRAMES
        loadWalkingAnimation(spriteId)
    }

    override fun receive(event: Event) {
        if (event is LoadEntityEvent) {
            state = event.state!!
            direction = event.direction!!
            threatMarker = createTreatMarker(event)
        }
        if (event is StateEvent) {
            state = event.state
        }
        if (event is DirectionEvent) {
            direction = event.direction
        }
        if (event is PositionEvent) {
            position = event.position
        }
        if (event is SpeedEvent) {
            setNewFrameDuration(event.moveSpeed)
        }
    }

    override fun update(dt: Float) {
        setFrame(dt)
        threatMarker.update(dt)
    }

    override fun render(batch: Batch) {
        batch.draw(currentFrame, position.x, position.y, Constant.TILE_SIZE, Constant.TILE_SIZE)
        if (state != EntityState.INVISIBLE) {
            threatMarker.render(batch, position)
        }
    }

    override fun renderOnMiniMap(entity: Entity, batch: Batch, shapeRenderer: ShapeRenderer) {
        // empty
    }

    private fun createTreatMarker(event: LoadEntityEvent): ThreatMarker {
        val conversationOrBattleId: String = event.conversationOrBattleId!!
        val threatLevel = ThreatLevel.forBattle(conversationOrBattleId)
        return ThreatMarker(threatLevel)
    }

}
