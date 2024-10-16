package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.*
import nl.t64.cot.screens.world.pathfinding.TiledNode


private const val SECOND_NODE = 1
private const val DETECTION_RANGE_DIVIDER = 12f
private const val MINIMUM_DETECTION_RANGE = 5
private const val MAXIMUM_DETECTION_RANGE = 20

class InputEnemy : InputComponent() {

    private lateinit var enemyEntity: Entity
    private lateinit var state: EntityState
    private var path: DefaultGraphPath<TiledNode> = DefaultGraphPath()
    private var stateTime = 0f
    private var isDetectingPlayer = false

    override fun receive(event: Event) {
        if (event is LoadEntityEvent) {
            state = event.state!!
            direction = event.direction!!
        }
        if (event is CollisionEvent) {
            stateTime = 0f
        }
        if (event is FindPathEvent) {
            if (::enemyEntity.isInitialized) {
                path = getPathToPlayer(event)
                enemyEntity.send(PathUpdateEvent(path))
            }
        }
        if (event is OnDetectionEvent) {
            if (::enemyEntity.isInitialized) {
                setIsDetectingPlayer(event)
            }
        }
    }

    private fun getPathToPlayer(event: FindPathEvent): DefaultGraphPath<TiledNode> {
        val startPoint = enemyEntity.getPositionInGrid()
        val endPoint = event.playerGridPosition
        return mapManager.findPath(startPoint, endPoint, state)
    }

    private fun setIsDetectingPlayer(onDetectionEvent: OnDetectionEvent) {
        if (onDetectionEvent.moveSpeed == Constant.MOVE_SPEED_4) {
            isDetectingPlayer = false
            enemyEntity.send(SpeedEvent(Constant.MOVE_SPEED_1))
        } else if (path.count > 0
            && path.count < onDetectionEvent.moveSpeed / DETECTION_RANGE_DIVIDER
        ) {
            isDetectingPlayer = true
            enemyEntity.send(SpeedEvent(Constant.MOVE_SPEED_2))
        } else if (path.count > MAXIMUM_DETECTION_RANGE) {
            isDetectingPlayer = false
            enemyEntity.send(SpeedEvent(Constant.MOVE_SPEED_1))
        } else if (path.count == 0) {
            isDetectingPlayer = false
            enemyEntity.send(SpeedEvent(Constant.MOVE_SPEED_1))
        }
        enemyEntity.send(DetectionEvent(isDetectingPlayer))
    }

    override fun update(entity: Entity, dt: Float) {
        this.enemyEntity = entity
        if (isDetectingPlayer && path.count in 0 until MINIMUM_DETECTION_RANGE) {
            setIdleState()
        } else if (isDetectingPlayer) {
            setFollowPath()
        } else {
            setWandering(dt)
        }
        entity.send(StateEvent(state))
        entity.send(DirectionEvent(direction))
    }

    private fun setIdleState() {
        state = when (state) {
            EntityState.IDLE, EntityState.WALKING -> EntityState.IDLE
            EntityState.IDLE_ANIMATING, EntityState.FLYING -> EntityState.IDLE_ANIMATING
            else -> throw IllegalArgumentException("Unexpected value: $state")
        }
    }

    private fun setFollowPath() {
        val tiledNode = path[SECOND_NODE]
        val nodePosition = Vector2(tiledNode.x.toFloat(), tiledNode.y.toFloat())
        val currentGridPosition = Vector2(enemyEntity.getPositionInGrid())
        state = getFollowState()
        direction = getFollowDirection(nodePosition, currentGridPosition)
    }

    private fun getFollowState(): EntityState {
        return when (state) {
            EntityState.IDLE, EntityState.WALKING -> EntityState.WALKING
            EntityState.IDLE_ANIMATING, EntityState.FLYING -> EntityState.FLYING
            else -> throw IllegalArgumentException("Unexpected value: $state")
        }
    }

    private fun getFollowDirection(nodePosition: Vector2, currentGridPosition: Vector2): Direction {
        return when {
            nodePosition.y > currentGridPosition.y && nodePosition.x < currentGridPosition.x -> Direction.NORTH_WEST
            nodePosition.y > currentGridPosition.y && nodePosition.x > currentGridPosition.x -> Direction.NORTH_EAST
            nodePosition.y < currentGridPosition.y && nodePosition.x < currentGridPosition.x -> Direction.SOUTH_WEST
            nodePosition.y < currentGridPosition.y && nodePosition.x > currentGridPosition.x -> Direction.SOUTH_EAST
            nodePosition.y > currentGridPosition.y -> Direction.NORTH
            nodePosition.y < currentGridPosition.y -> Direction.SOUTH
            nodePosition.x < currentGridPosition.x -> Direction.WEST
            nodePosition.x > currentGridPosition.x -> Direction.EAST
            else -> throw IllegalStateException("Is this possible?")
        }
    }

    private fun setWandering(dt: Float) {
        stateTime -= dt
        if (stateTime < 0) {
            setRandom()
        }
    }

    private fun setRandom() {
        stateTime = MathUtils.random(1.0f, 2.0f)

        when (state) {
            EntityState.IDLE -> {
                state = EntityState.WALKING
                direction = Direction.getRandom()
            }
            EntityState.WALKING -> state = EntityState.IDLE
            EntityState.FLYING -> direction = Direction.getRandom()
            EntityState.IDLE_ANIMATING -> state = EntityState.FLYING
            EntityState.IMMOBILE -> state = EntityState.IMMOBILE
            else -> throw IllegalArgumentException("Unexpected value: $state")
        }
    }

}
