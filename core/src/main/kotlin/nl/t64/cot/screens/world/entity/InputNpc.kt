package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.screens.world.entity.events.*
import kotlin.math.abs


private const val DEFAULT_STATE_TIME = 5f

class InputNpc : InputComponent() {

    private var stateTime = 0f
    private lateinit var state: EntityState
    private lateinit var originalState: EntityState
    private lateinit var originalDirection: Direction

    override fun receive(event: Event) {
        if (event is LoadEntityEvent) {
            state = event.state!!
            direction = event.direction!!
            originalState = state
            originalDirection = direction
        }
        if (event is CollisionEvent) {
            stateTime = 0f
        }
        if (event is WaitEvent) {
            handleEvent(event)
        }
    }

    override fun update(entity: Entity, dt: Float) {
        stateTime -= dt
        if (stateTime < 0) {
            setRandom()
        }
        entity.send(StateEvent(state))
        entity.send(DirectionEvent(direction))
    }

    private fun setRandom() {
        stateTime = MathUtils.random(1.0f, 2.0f)

        when (state) {
            EntityState.INVISIBLE -> {
            }
            EntityState.WALKING -> {
                state = EntityState.IDLE
            }
            EntityState.IDLE -> {
                state = EntityState.WALKING
                direction = Direction.getRandom()
            }
            EntityState.FLYING -> {
                direction = Direction.getRandom()
            }
            EntityState.IMMOBILE, EntityState.IDLE_ANIMATING -> {
                state = originalState
                direction = originalDirection
            }
            else -> throw IllegalArgumentException("EntityState '$state' not usable.")
        }
    }

    private fun handleEvent(event: WaitEvent) {
        val npcPosition: Vector2 = event.npcPosition
        val playerPosition: Vector2 = event.playerPosition
        stateTime = DEFAULT_STATE_TIME
        state = stopMoving()
        direction = npcPosition.turnToPlayer(playerPosition)
    }

    private fun stopMoving(): EntityState {
        return when (state) {
            EntityState.WALKING -> EntityState.IDLE
            EntityState.FLYING -> EntityState.IDLE_ANIMATING
            EntityState.IMMOBILE,
            EntityState.IDLE,
            EntityState.IDLE_ANIMATING,
            EntityState.INVISIBLE -> state
            else -> throw IllegalArgumentException("EntityState '$state' not usable.")
        }
    }

    private fun Vector2.turnToPlayer(playerPosition: Vector2): Direction {
        return when {
            isWestOfPlayer(playerPosition) -> Direction.EAST
            isEastOfPlayer(playerPosition) -> Direction.WEST
            isSouthOfPlayer(playerPosition) -> Direction.NORTH
            isNorthOfPlayer(playerPosition) -> Direction.SOUTH
            else -> direction
        }
    }

    private fun Vector2.isWestOfPlayer(playerPosition: Vector2): Boolean =
        x < playerPosition.x && abs(y - playerPosition.y) < abs(x - playerPosition.x)

    private fun Vector2.isEastOfPlayer(playerPosition: Vector2): Boolean =
        x > playerPosition.x && abs(y - playerPosition.y) < abs(x - playerPosition.x)

    private fun Vector2.isSouthOfPlayer(playerPosition: Vector2): Boolean =
        y < playerPosition.y && abs(y - playerPosition.y) > abs(x - playerPosition.x)

    private fun Vector2.isNorthOfPlayer(playerPosition: Vector2): Boolean =
        y > playerPosition.y && abs(y - playerPosition.y) > abs(x - playerPosition.x)

}
