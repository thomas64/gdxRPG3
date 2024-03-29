package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.CollisionEvent
import kotlin.math.roundToInt
import kotlin.math.sqrt


private const val WANDER_BOX_SIZE = 240f
private const val WANDER_BOX_POSITION = -96f

abstract class PhysicsComponent : Component {

    lateinit var entity: Entity
    lateinit var state: EntityState
    lateinit var direction: Direction
    var velocity = 0f
    var boundingBox = Rectangle()
    lateinit var wanderBox: Rectangle
    var oldPosition = Vector2()
    var currentPosition = Vector2()
    var boundingBoxWidthPercentage = 0f
    var boundingBoxHeightPercentage = 0f

    override fun dispose() {
        // empty
    }

    abstract fun update(entity: Entity, dt: Float)

    abstract fun debug(shapeRenderer: ShapeRenderer)

    open fun setBoundingBox() {
        val width = Constant.TILE_SIZE * boundingBoxWidthPercentage
        val height = Constant.TILE_SIZE * boundingBoxHeightPercentage
        val widthReduction = 1f - boundingBoxWidthPercentage
        val x = currentPosition.x + (Constant.TILE_SIZE * (widthReduction / 2f))
        val y = currentPosition.y
        boundingBox.set(x, y, width, height)
    }

    fun setWanderBox() {
        wanderBox = Rectangle(currentPosition.x + WANDER_BOX_POSITION,
                              currentPosition.y + WANDER_BOX_POSITION,
                              WANDER_BOX_SIZE, WANDER_BOX_SIZE)
    }

    fun relocate(dt: Float) {
        when (state) {
            EntityState.WALKING,
            EntityState.FLYING,
            EntityState.PLAYING -> move(dt)
            EntityState.ALIGNING -> alignToGrid()
            EntityState.IDLE,
            EntityState.IMMOBILE,
            EntityState.INVISIBLE,
            EntityState.IDLE_ANIMATING -> Unit
            else -> throw IllegalArgumentException("EntityState '$state' not usable.")
        }
    }

    fun move(dt: Float) {
        oldPosition.set(currentPosition)

        when (direction) {
            Direction.NORTH -> currentPosition.y += velocity * dt
            Direction.SOUTH -> currentPosition.y -= velocity * dt
            Direction.WEST -> currentPosition.x -= velocity * dt
            Direction.EAST -> currentPosition.x += velocity * dt
            Direction.NORTH_WEST -> {
                currentPosition.y += (velocity / sqrt(2f)) * dt
                currentPosition.x -= (velocity / sqrt(2f)) * dt
            }
            Direction.NORTH_EAST -> {
                currentPosition.y += (velocity / sqrt(2f)) * dt
                currentPosition.x += (velocity / sqrt(2f)) * dt
            }
            Direction.SOUTH_WEST -> {
                currentPosition.y -= (velocity / sqrt(2f)) * dt
                currentPosition.x -= (velocity / sqrt(2f)) * dt
            }
            Direction.SOUTH_EAST -> {
                currentPosition.y -= (velocity / sqrt(2f)) * dt
                currentPosition.x += (velocity / sqrt(2f)) * dt
            }
            Direction.NONE -> throw IllegalArgumentException("Direction 'NONE' is not usable.")
        }
        setBoundingBox()
    }

    fun getRectangle(): Rectangle =
        Rectangle(currentPosition.x, currentPosition.y, Constant.TILE_SIZE, Constant.TILE_SIZE)

    fun checkObstacles() {
        when (state) {
            EntityState.WALKING,
            EntityState.FLYING,
            EntityState.PLAYING -> possibleSendCollisionEvent()
            EntityState.IDLE,
            EntityState.IMMOBILE,
            EntityState.INVISIBLE,
            EntityState.IDLE_ANIMATING -> Unit
            else -> throw IllegalStateException("EntityState '$state' not usable.")
        }
    }

    private fun possibleSendCollisionEvent() {
        if (hasMovedBackByWanderBox() || hasMovedBackByBlockers()) {
            entity.send(CollisionEvent())
        }
    }

    private fun hasMovedBackByWanderBox(): Boolean {
        if (!wanderBox.contains(boundingBox)) {
            moveBack()
            return true
        }
        return false
    }

    private fun hasMovedBackByBlockers(): Boolean {
        if (doesBoundingBoxOverlapsBlockers()) {
            moveBack()
            return true
        }
        return false
    }

    private fun doesBoundingBoxOverlapsBlockers(): Boolean {
        return brokerManager.blockObservers.getCurrentBlockersFor(boundingBox, state)
            .filter { boundingBox != it }
            .any { boundingBox.overlaps(it) }
    }

    private fun moveBack() {
        currentPosition.set(oldPosition)
    }

    private fun alignToGrid() {
        val roundedX = (currentPosition.x / Constant.TILE_SIZE).roundToInt() * Constant.TILE_SIZE
        val roundedY = (currentPosition.y / Constant.TILE_SIZE).roundToInt() * Constant.TILE_SIZE
        currentPosition.set(roundedX, roundedY)
    }

}
