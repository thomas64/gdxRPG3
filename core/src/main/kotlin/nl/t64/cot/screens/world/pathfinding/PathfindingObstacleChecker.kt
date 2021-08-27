package nl.t64.cot.screens.world.pathfinding

import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.subjects.BlockSubject


class PathfindingObstacleChecker(
    positionInGrid: Vector2,
    private val direction: Direction
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
            Direction.NONE -> throw IllegalArgumentException("Direction 'NONE' is not usable.")
        }
    }

    private fun getDirectionWhenBlockersAreSouth(): Direction {
        return when {
            blockers.isBlockerBlockingGridPoint(x + 1, y - 1) -> Direction.WEST
            blockers.isBlockerBlockingGridPoint(x - 1, y - 1) -> Direction.EAST
            else -> Direction.SOUTH
        }
    }

    private fun getDirectionWhenBlockersAreNorth(): Direction {
        return when {
            blockers.isBlockerBlockingGridPoint(x + 1, y + 1) -> Direction.WEST
            blockers.isBlockerBlockingGridPoint(x - 1, y + 1) -> Direction.EAST
            else -> Direction.NORTH
        }
    }

    private fun getDirectionWhenBlockersAreWest(): Direction {
        return when {
            blockers.isBlockerBlockingGridPoint(x - 1, y + 1) -> Direction.SOUTH
            blockers.isBlockerBlockingGridPoint(x - 1, y - 1) -> Direction.NORTH
            else -> Direction.WEST
        }
    }

    private fun getDirectionWhenBlockersAreEast(): Direction {
        return when {
            blockers.isBlockerBlockingGridPoint(x + 1, y + 1) -> Direction.SOUTH
            blockers.isBlockerBlockingGridPoint(x + 1, y - 1) -> Direction.NORTH
            else -> Direction.EAST
        }
    }

}
