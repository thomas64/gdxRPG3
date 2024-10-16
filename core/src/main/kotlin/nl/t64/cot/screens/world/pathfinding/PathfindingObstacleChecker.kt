package nl.t64.cot.screens.world.pathfinding

import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState
import nl.t64.cot.subjects.BlockSubject


class PathfindingObstacleChecker(
    positionInGrid: Vector2,
    private val direction: Direction,
    private val state: EntityState
) {

    private val blockers: BlockSubject = brokerManager.blockObservers
    private val x: Float = positionInGrid.x
    private val y: Float = positionInGrid.y

    fun getNewDirection(): Direction {
        return when (direction) {
            Direction.SOUTH -> getDirectionWhenBlockersAreSouth()
            Direction.NORTH -> getDirectionWhenBlockersAreNorth()
            Direction.WEST -> getDirectionWhenBlockersAreWest()
            Direction.EAST -> getDirectionWhenBlockersAreEast()
            Direction.NORTH_WEST -> getDirectionWhenBlockersAreNorthWest()
            Direction.NORTH_EAST -> getDirectionWhenBlockersAreNorthEast()
            Direction.SOUTH_WEST -> getDirectionWhenBlockersAreSouthWest()
            Direction.SOUTH_EAST -> getDirectionWhenBlockersAreSouthEast()
            Direction.NONE -> throw IllegalArgumentException("Direction 'NONE' is not usable.")
        }
    }

    private fun getDirectionWhenBlockersAreSouth(): Direction {
        return when {
            blockers.isBlockerBlockingGridPoint(x + 1, y - 1, state) -> Direction.WEST
            blockers.isBlockerBlockingGridPoint(x - 1, y - 1, state) -> Direction.EAST
            else -> Direction.SOUTH
        }
    }

    private fun getDirectionWhenBlockersAreNorth(): Direction {
        return when {
            blockers.isBlockerBlockingGridPoint(x + 1, y + 1, state) -> Direction.WEST
            blockers.isBlockerBlockingGridPoint(x - 1, y + 1, state) -> Direction.EAST
            else -> Direction.NORTH
        }
    }

    private fun getDirectionWhenBlockersAreWest(): Direction {
        return when {
            blockers.isBlockerBlockingGridPoint(x - 1, y + 1, state) -> Direction.SOUTH
            blockers.isBlockerBlockingGridPoint(x - 1, y - 1, state) -> Direction.NORTH
            else -> Direction.WEST
        }
    }

    private fun getDirectionWhenBlockersAreEast(): Direction {
        return when {
            blockers.isBlockerBlockingGridPoint(x + 1, y + 1, state) -> Direction.SOUTH
            blockers.isBlockerBlockingGridPoint(x + 1, y - 1, state) -> Direction.NORTH
            else -> Direction.EAST
        }
    }

    private fun getDirectionWhenBlockersAreNorthWest(): Direction {
        return when {
            blockers.isBlockerBlockingGridPoint(x + 1, y + 1, state) -> Direction.WEST
            blockers.isBlockerBlockingGridPoint(x - 1, y - 1, state) -> Direction.NORTH
            else -> Direction.NORTH_WEST
        }
    }

    private fun getDirectionWhenBlockersAreNorthEast(): Direction {
        return when {
            blockers.isBlockerBlockingGridPoint(x - 1, y + 1, state) -> Direction.EAST
            blockers.isBlockerBlockingGridPoint(x + 1, y - 1, state) -> Direction.NORTH
            else -> Direction.NORTH_EAST
        }
    }

    private fun getDirectionWhenBlockersAreSouthWest(): Direction {
        return when {
            blockers.isBlockerBlockingGridPoint(x + 1, y - 1, state) -> Direction.WEST
            blockers.isBlockerBlockingGridPoint(x - 1, y + 1, state) -> Direction.SOUTH
            else -> Direction.SOUTH_WEST
        }
    }

    private fun getDirectionWhenBlockersAreSouthEast(): Direction {
        return when {
            blockers.isBlockerBlockingGridPoint(x - 1, y - 1, state) -> Direction.EAST
            blockers.isBlockerBlockingGridPoint(x + 1, y + 1, state) -> Direction.SOUTH
            else -> Direction.SOUTH_EAST
        }
    }

}
