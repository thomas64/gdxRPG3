package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.*


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
        val blockers = brokerManager.blockObservers.getCurrentBlockersFor(boundingBox)
        if (blockers.isNotEmpty()) handleBlockers(blockers, dt)
    }

    private fun handleBlockers(blockers: List<Rectangle>, dt: Float) {
        if (blockers.size == 1) {
            moveSide(blockers[0], dt)
        }
        setRoundPosition()
        if (hasHitAnotherBlockWhileSideStepping()) {
            return
        }
        while (doesBoundingBoxOverlapsBlockers()) {
            moveBack()
        }
    }

    private fun hasHitAnotherBlockWhileSideStepping(): Boolean {
        val blockers = brokerManager.blockObservers.getCurrentBlockersFor(boundingBox)
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
            Direction.WEST -> getWest()
            Direction.EAST -> getEast()
            Direction.NONE -> throw IllegalArgumentException("Direction 'NONE' is not usable.")
        }
    }

    private fun getNorth(): Rectangle {
        return Rectangle().apply {
            setWidth(boundingBox.width / 4f)
            setHeight(Constant.HALF_TILE_SIZE + 1)
            setX(boundingBox.x + (boundingBox.width / 2f) - (boundingBox.width / 8f))
            setY(boundingBox.y + boundingBox.height)
        }
    }

    private fun getSouth(): Rectangle {
        return Rectangle().apply {
            setWidth(boundingBox.width / 4f)
            setHeight(Constant.HALF_TILE_SIZE)
            setX(boundingBox.x + (boundingBox.width / 2f) - (boundingBox.width / 8f))
            setY(boundingBox.y - Constant.HALF_TILE_SIZE)
        }
    }

    private fun getWest(): Rectangle {
        return Rectangle().apply {
            setWidth(Constant.HALF_TILE_SIZE)
            setHeight(boundingBox.height + 6f)
            setX(boundingBox.x - Constant.HALF_TILE_SIZE)
            setY(boundingBox.y - 3f)
        }
    }

    private fun getEast(): Rectangle {
        return Rectangle().apply {
            setWidth(Constant.HALF_TILE_SIZE)
            setHeight(boundingBox.height + 6f)
            setX(boundingBox.x + boundingBox.width)
            setY(boundingBox.y - 3f)
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
