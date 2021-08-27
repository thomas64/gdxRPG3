package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.*
import nl.t64.cot.screens.world.pathfinding.PathfindingObstacleChecker
import nl.t64.cot.screens.world.pathfinding.TiledNode


class PhysicsPartyMember : PhysicsComponent() {

    private lateinit var path: DefaultGraphPath<TiledNode>

    init {
        boundingBoxWidthPercentage = 0.50f
        boundingBoxHeightPercentage = 0.20f
    }

    override fun receive(event: Event) {
        if (event is LoadEntityEvent) {
            state = event.state!!
            currentPosition = event.position
            setBoundingBox()
        }
        if (event is StateEvent) {
            state = event.state
        }
        if (event is DirectionEvent) {
            direction = event.direction
        }
        if (event is OnDetectionEvent) {
            velocity = event.moveSpeed
        }
        if (event is PathUpdateEvent) {
            path = event.path
        }
    }

    override fun update(entity: Entity, dt: Float) {
        this.entity = entity
        relocate(dt)
        checkObstacles(dt)
        entity.send(PositionEvent(currentPosition))
    }

    private fun checkObstacles(dt: Float) {
        if (brokerManager.blockObservers.getCurrentBlockersFor(boundingBox).isNotEmpty()) {
            val positionInGrid = entity.getPositionInGrid()
            direction = PathfindingObstacleChecker(positionInGrid, direction).getNewDirection()
            currentPosition.set(oldPosition)
            move(dt)
        }
    }

    override fun debug(shapeRenderer: ShapeRenderer) {
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
