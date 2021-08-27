package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.screens.world.entity.events.DirectionEvent
import nl.t64.cot.screens.world.entity.events.Event
import nl.t64.cot.screens.world.entity.events.PathUpdateEvent
import nl.t64.cot.screens.world.entity.events.StateEvent
import nl.t64.cot.screens.world.pathfinding.TiledNode


private const val TWO_NODES = 2
private const val SECOND_NODE = 1

class InputPartyMember : InputComponent() {

    private lateinit var partyMember: Entity
    private lateinit var path: DefaultGraphPath<TiledNode>

    override fun receive(event: Event) {
        if (event is PathUpdateEvent) {
            path = event.path
        }
    }

    override fun update(entity: Entity, dt: Float) {
        this.partyMember = entity
        setStateAndDirection()
    }

    private fun setStateAndDirection() {
        if (path.count > TWO_NODES) {
            setWalking()
        } else {
            setIdle()
        }
    }

    private fun setWalking() {
        val tiledNode = path[SECOND_NODE]
        val nodePosition = Vector2(tiledNode.x.toFloat(), tiledNode.y.toFloat())
        val currentGridPosition = Vector2(partyMember.getPositionInGrid())
        partyMember.send(StateEvent(EntityState.WALKING))
        setDirection(nodePosition, currentGridPosition)
    }

    private fun setDirection(nodePosition: Vector2, currentGridPosition: Vector2) {
        when {
            nodePosition.y > currentGridPosition.y -> partyMember.send(DirectionEvent(Direction.NORTH))
            nodePosition.y < currentGridPosition.y -> partyMember.send(DirectionEvent(Direction.SOUTH))
            nodePosition.x < currentGridPosition.x -> partyMember.send(DirectionEvent(Direction.WEST))
            nodePosition.x > currentGridPosition.x -> partyMember.send(DirectionEvent(Direction.EAST))
        }
    }

    private fun setIdle() {
        partyMember.send(StateEvent(EntityState.IDLE))
    }

}
