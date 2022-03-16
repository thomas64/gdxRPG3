package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.math.Vector2
import kotlin.math.abs


internal fun Vector2.turnToPlayer(playerPosition: Vector2): Direction {
    return when {
        isWestOfPlayer(playerPosition) -> Direction.EAST
        isEastOfPlayer(playerPosition) -> Direction.WEST
        isSouthOfPlayer(playerPosition) -> Direction.NORTH
        isNorthOfPlayer(playerPosition) -> Direction.SOUTH
        else -> throw IllegalStateException("Not possible to reach.")
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

