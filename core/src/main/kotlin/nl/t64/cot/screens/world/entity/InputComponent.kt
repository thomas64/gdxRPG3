package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.math.Vector2


abstract class InputComponent : Component {

    protected lateinit var direction: Direction

    abstract fun update(entity: Entity, dt: Float)

    override fun dispose() {
        // empty
    }

    open fun reset() {
        // empty
    }

    open fun resetStance() {
        // empty
    }

    protected fun getFollowDirection(nodePosition: Vector2, currentGridPosition: Vector2): Direction {
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

}
