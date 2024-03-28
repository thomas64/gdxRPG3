package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.*
import nl.t64.cot.screens.world.map.isOutsideMap
import kotlin.math.abs


class PhysicsPlayer : PhysicsComponent() {

    private var isActionPressed = false

    init {
        boundingBoxWidthPercentage = 0.80f
        boundingBoxHeightPercentage = 0.30f
    }

    override fun receive(event: Event) {
        if (event is LoadEntityEvent) {
            direction = event.direction!!
            currentPosition = event.position
            setBoundingBox()
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
        if (event is ActionEvent) {
            isActionPressed = true
        }
    }

    override fun update(entity: Entity, dt: Float) {
        this.entity = entity
        checkActionPressed()
        relocate(dt)
        collisionObstacles(dt)
        brokerManager.detectionObservers.notifyDetection(velocity)
        entity.send(PositionEvent(currentPosition))
    }

    private fun checkActionPressed() {
        if (isActionPressed) {
            brokerManager.actionObservers.notifyActionPressed(getCheckRect(), direction, currentPosition)
            isActionPressed = false
        }
    }

    private fun collisionObstacles(dt: Float) {
        if (velocity != Constant.MOVE_SPEED_4) {
            brokerManager.bumpObservers.notifyBump(getALittleBitBiggerBoundingBox(), getCheckRect(), currentPosition)
            if (state == EntityState.WALKING) {
                collisionBlockers(dt)
            }
            brokerManager.collisionObservers.notifyCollision(boundingBox, direction)
        }
    }

    private fun collisionBlockers(dt: Float) {
        val blockers = brokerManager.blockObservers.getCurrentBlockersFor(boundingBox, state)
        if (blockers.isNotEmpty()) {
            handleBlockers(blockers, dt)
            handlePossibleOutsideMap()
        }
    }

    private fun handleBlockers(blockers: List<Rectangle>, dt: Float) {
        if (direction in listOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)) {
            if (blockers.size == 1) {
                moveSide(blockers.first(), dt)
            }
            setRoundPosition()
            if (hasHitAnotherBlockWhileSideStepping()) {
                return
            }
        }
        repeat(10000) {
            if (doesBoundingBoxOverlapsBlockers()) {
                moveBack(blockers)
                alterDirectionWhenWallGliding()
            } else return
        }
    }

    private enum class OverlapSide { NONE, TOP, BOTTOM, LEFT, RIGHT }

    private fun moveBack(blockers: List<Rectangle>) {
        blockers.forEach { blocker ->
            val overlapSide = getOverlapSide(blocker)
            when {
                direction == Direction.NORTH -> currentPosition.y -= 1f
                direction == Direction.SOUTH -> currentPosition.y += 1f
                direction == Direction.WEST -> currentPosition.x += 1f
                direction == Direction.EAST -> currentPosition.x -= 1f
                direction.isNorth() && overlapSide == OverlapSide.BOTTOM -> currentPosition.y -= 1f
                direction.isSouth() && overlapSide == OverlapSide.TOP -> currentPosition.y += 1f
                direction.isWest() && overlapSide == OverlapSide.LEFT -> currentPosition.x += 1f
                direction.isEast() && overlapSide == OverlapSide.RIGHT -> currentPosition.x -= 1f
                else -> handleEdgeCases(overlapSide)
            }
        }
        setRoundPosition()
    }

    private fun alterDirectionWhenWallGliding() {
        if (currentPosition.x == oldPosition.x) {
            when (direction) {
                Direction.NORTH_WEST, Direction.NORTH_EAST -> entity.send(DirectionEvent(Direction.NORTH))
                Direction.SOUTH_WEST, Direction.SOUTH_EAST -> entity.send(DirectionEvent(Direction.SOUTH))
                else -> {}
            }
        }
    }

    private fun handleEdgeCases(overlapSide: OverlapSide) {
        when (overlapSide) {
            OverlapSide.RIGHT -> currentPosition.x -= 1f
            OverlapSide.LEFT -> currentPosition.x += 1f
            OverlapSide.BOTTOM -> currentPosition.y += 1f
            OverlapSide.TOP -> currentPosition.y -= 1f
            OverlapSide.NONE -> {}
        }
    }

    private fun getOverlapSide(blocker: Rectangle): OverlapSide {
        val playerCenterX: Float = boundingBox.x + boundingBox.width / 2f
        val playerCenterY: Float = boundingBox.y + boundingBox.height / 2f
        val blockerCenterX: Float = blocker.x + blocker.width / 2f
        val blockerCenterY: Float = blocker.y + blocker.height / 2f

        val dx: Float = playerCenterX - blockerCenterX
        val dy: Float = playerCenterY - blockerCenterY

        val combinedHalfWidths: Float = boundingBox.width / 2f + blocker.width / 2f
        val combinedHalfHeights: Float = boundingBox.height / 2f + blocker.height / 2f

        val overlapX: Float = combinedHalfWidths - abs(dx)
        val overlapY: Float = combinedHalfHeights - abs(dy)

        if (overlapX > 0f && overlapY > 0f) {
            return if (overlapX >= overlapY) {
                if (dy > 0f) OverlapSide.TOP else OverlapSide.BOTTOM
            } else {
                if (dx > 0f) OverlapSide.LEFT else OverlapSide.RIGHT
            }
        }
        return OverlapSide.NONE
    }

    private fun handlePossibleOutsideMap() {
        val justALittleBitMore = 4f
        val xAdjustedCurrentPos = Vector2(currentPosition.x + justALittleBitMore, currentPosition.y)
        if (xAdjustedCurrentPos.isOutsideMap()) {
            when (direction) {
                Direction.NORTH -> currentPosition.y = oldPosition.y + Constant.HALF_TILE_SIZE
                Direction.SOUTH -> currentPosition.y = oldPosition.y - Constant.HALF_TILE_SIZE
                Direction.WEST -> currentPosition.x = oldPosition.x - Constant.TILE_SIZE
                Direction.EAST -> currentPosition.x = oldPosition.x + Constant.TILE_SIZE
                Direction.NORTH_WEST -> {
                    currentPosition.y = oldPosition.y + Constant.HALF_TILE_SIZE
                    currentPosition.x = oldPosition.x - Constant.TILE_SIZE
                }

                Direction.SOUTH_WEST -> {
                    currentPosition.y = oldPosition.y - Constant.HALF_TILE_SIZE
                    currentPosition.x = oldPosition.x - Constant.TILE_SIZE
                }

                Direction.NORTH_EAST -> {
                    currentPosition.y = oldPosition.y + Constant.HALF_TILE_SIZE
                    currentPosition.x = oldPosition.x + Constant.TILE_SIZE
                }

                Direction.SOUTH_EAST -> {
                    currentPosition.y = oldPosition.y - Constant.HALF_TILE_SIZE
                    currentPosition.x = oldPosition.x + Constant.TILE_SIZE
                }

                Direction.NONE -> throw IllegalArgumentException("Direction 'NONE' is not usable.")
            }
        }
    }

    private fun hasHitAnotherBlockWhileSideStepping(): Boolean {
        val blockers = brokerManager.blockObservers.getCurrentBlockersFor(boundingBox, state)
        if (blockers.size > 1) {
            currentPosition.set(oldPosition)
            setBoundingBox()
            return true
        }
        return false
    }

    private fun moveSide(blocker: Rectangle, dt: Float) {
        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            playerIsEastOf(blocker, dt)
            playerIsWestOf(blocker, dt)
        } else if (direction == Direction.WEST || direction == Direction.EAST) {
            playerIsSouthOf(blocker, dt)
            playerIsNorthOf(blocker, dt)
        }
    }

    private fun playerIsEastOf(blocker: Rectangle, dt: Float) {
        if (boundingBox.x + (boundingBox.width / 2) > blocker.x + blocker.width) {
            currentPosition.x += velocity * dt
            if (boundingBox.x > blocker.x + blocker.width) {
                currentPosition.x = blocker.x + blocker.width
            }
        }
    }

    private fun playerIsWestOf(blocker: Rectangle, dt: Float) {
        if (boundingBox.x + (boundingBox.width / 2) < blocker.x) {
            currentPosition.x -= velocity * dt
            if (boundingBox.x + boundingBox.width < blocker.x) {
                currentPosition.x = blocker.x - boundingBox.width
            }
        }
    }

    private fun playerIsSouthOf(blocker: Rectangle, dt: Float) {
        if (boundingBox.y + (boundingBox.height / 2) < blocker.y) {
            currentPosition.y -= velocity * dt
            if (boundingBox.y + boundingBox.height < blocker.y) {
                currentPosition.y = blocker.y - boundingBox.height
            }
        }
    }

    private fun playerIsNorthOf(blocker: Rectangle, dt: Float) {
        if (boundingBox.y + (boundingBox.height / 2) > blocker.y + blocker.height) {
            currentPosition.y += velocity * dt
            if (boundingBox.y > blocker.y + blocker.height) {
                currentPosition.y = blocker.y + blocker.height
            }
        }
    }

    private fun getALittleBitBiggerBoundingBox(): Rectangle {
        return Rectangle(boundingBox).apply {
            setWidth(boundingBox.width + 24f)
            setHeight(boundingBox.height + 24f)
            setX(boundingBox.x - 12f)
            setY(boundingBox.y - 12f)
        }
    }

    private fun getCheckRect(): Rectangle {
        return when (direction) {
            Direction.NORTH -> getNorth()
            Direction.SOUTH -> getSouth()
            Direction.WEST, Direction.NORTH_WEST, Direction.SOUTH_WEST -> getWest()
            Direction.EAST, Direction.NORTH_EAST, Direction.SOUTH_EAST -> getEast()
            Direction.NONE -> throw IllegalArgumentException("Direction 'NONE' is not usable.")
        }
    }

    private fun getNorth(): Rectangle {
        return Rectangle().apply {
            setWidth(boundingBox.width / 4f)
            setHeight(Constant.HALF_TILE_SIZE - 1f)
            setX(boundingBox.x + (boundingBox.width / 2f) - (boundingBox.width / 8f))
            setY(boundingBox.y + boundingBox.height)
        }
    }

    private fun getSouth(): Rectangle {
        return Rectangle().apply {
            setWidth(boundingBox.width / 4f)
            setHeight(Constant.HALF_TILE_SIZE - 1f)
            setX(boundingBox.x + (boundingBox.width / 2f) - (boundingBox.width / 8f))
            setY(boundingBox.y - Constant.HALF_TILE_SIZE + 1f)
        }
    }

    private fun getWest(): Rectangle {
        return Rectangle().apply {
            setWidth(Constant.HALF_TILE_SIZE - 1f)
            setHeight(boundingBox.height)
            setX(boundingBox.x - Constant.HALF_TILE_SIZE + 1f)
            setY(boundingBox.y)
        }
    }

    private fun getEast(): Rectangle {
        return Rectangle().apply {
            setWidth(Constant.HALF_TILE_SIZE - 1f)
            setHeight(boundingBox.height)
            setX(boundingBox.x + boundingBox.width)
            setY(boundingBox.y)
        }
    }

    override fun debug(shapeRenderer: ShapeRenderer) {
        shapeRenderer.color = Color.YELLOW
        shapeRenderer.rect(boundingBox.x, boundingBox.y, boundingBox.width, boundingBox.height)
        shapeRenderer.color = Color.GREEN
        shapeRenderer.rect(getALittleBitBiggerBoundingBox().x,
                           getALittleBitBiggerBoundingBox().y,
                           getALittleBitBiggerBoundingBox().width,
                           getALittleBitBiggerBoundingBox().height)
        shapeRenderer.color = Color.CYAN
        shapeRenderer.rect(getCheckRect().x,
                           getCheckRect().y,
                           getCheckRect().width,
                           getCheckRect().height)
    }

}
