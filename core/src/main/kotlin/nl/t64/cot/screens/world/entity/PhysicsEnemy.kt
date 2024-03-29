package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.*
import nl.t64.cot.screens.world.pathfinding.PathfindingObstacleChecker
import nl.t64.cot.screens.world.pathfinding.TiledNode


private const val MINIMAL_NODES_BEFORE_STARTING_A_BATTLE = 5

class PhysicsEnemy : PhysicsComponent() {

    private lateinit var battleId: String
    private lateinit var path: DefaultGraphPath<TiledNode>
    private var isDetectingPlayer: Boolean = false
    private var isBumped: Boolean = false
    private var isSelected: Boolean = false

    init {
        velocity = Constant.MOVE_SPEED_1
        boundingBoxWidthPercentage = 0.80f
        boundingBoxHeightPercentage = 0.30f
    }

    override fun receive(event: Event) {
        if (event is LoadEntityEvent) {
            initNpc(event)
        }
        if (event is StateEvent) {
            state = event.state
        }
        if (event is DirectionEvent) {
            direction = event.direction
        }
        if (event is SpeedEvent) {
            velocity = event.moveSpeed
        }
        if (event is PathUpdateEvent) {
            path = event.path
        }
        if (event is DetectionEvent) {
            isDetectingPlayer = event.isDetectingPlayer
        }
        if (event is OnBumpEvent) {
            if (event.biggerBoundingBox.overlaps(boundingBox)) {
                isBumped = true
            }
        }
        if (event is OnActionEvent) {
            if (event.checkRect.overlaps(boundingBox)) {
                isSelected = true
            }
        }
    }

    private fun initNpc(loadEvent: LoadEntityEvent) {
        state = loadEvent.state!!
        currentPosition = loadEvent.position
        direction = loadEvent.direction!!
        battleId = loadEvent.conversationOrBattleId!!
        setWanderBox()
        setBoundingBox()
    }

    override fun update(entity: Entity, dt: Float) {
        this.entity = entity
        relocate(dt)
        if (isDetectingPlayer) {
            checkObstaclesWhileDetecting(dt)
        } else {
            checkObstacles()
        }
        entity.send(PositionEvent(currentPosition))
        if (isNearbyPlayer()) {
            screenManager.getWorldScreen().showBattleScreen(battleId, entity)
        }
        if (isBumped) {
            isBumped = false
            screenManager.getWorldScreen().showBattleScreen(battleId, entity)
        }
        if (isSelected) {
            isSelected = false
            screenManager.getWorldScreen().showBattleScreen(battleId, entity)
        }
    }

    private fun checkObstaclesWhileDetecting(dt: Float) {
        setWanderBox()
        if (brokerManager.blockObservers.getCurrentBlockersFor(boundingBox, state).isNotEmpty()) {
            val positionInGrid = entity.getPositionInGrid()
            direction = PathfindingObstacleChecker(positionInGrid, direction, state).getNewDirection()
            currentPosition.set(oldPosition)
            move(dt)
        }
    }

    private fun isNearbyPlayer(): Boolean {
        return isDetectingPlayer && path.count in 1..MINIMAL_NODES_BEFORE_STARTING_A_BATTLE
    }

    override fun debug(shapeRenderer: ShapeRenderer) {
        shapeRenderer.color = Color.PURPLE
        if (state != EntityState.IMMOBILE
            && state != EntityState.IDLE_ANIMATING
        ) {
            shapeRenderer.rect(wanderBox.x, wanderBox.y, wanderBox.width, wanderBox.height)
        }
        shapeRenderer.color = Color.YELLOW
        shapeRenderer.rect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height)

        shapeRenderer.color = Color.MAGENTA
        path.forEach {
            val x = it.x * Constant.HALF_TILE_SIZE
            val y = it.y * Constant.HALF_TILE_SIZE
            shapeRenderer.rect(x, y, Constant.HALF_TILE_SIZE, Constant.HALF_TILE_SIZE)
        }
    }

}
