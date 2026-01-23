package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.constants.Constant
import kotlin.math.abs


fun renderOnMiniMap(conversationId: String,
                    state: EntityState,
                    position: Vector2,
                    batch: Batch,
                    shapeRenderer: ShapeRenderer) {

    if (state == EntityState.INVISIBLE) return
    shapeRenderer.color = getColorBasedOn(conversationId) ?: return
    shapeRenderer.circle(position.x + Constant.HALF_TILE_SIZE,
                         position.y + Constant.HALF_TILE_SIZE,
                         Constant.HALF_TILE_SIZE)
}

fun Vector2.turnToPlayer(playerPosition: Vector2, npcDirection: Direction): Direction {
    return when {
        isWestOfPlayer(playerPosition) -> Direction.EAST
        isEastOfPlayer(playerPosition) -> Direction.WEST
        isSouthOfPlayer(playerPosition) -> Direction.NORTH
        isNorthOfPlayer(playerPosition) -> Direction.SOUTH
        else -> npcDirection
    }
}

private fun getColorBasedOn(conversationId: String): Color? {
    return Constant.MINIMAP_ICONS.entries
        .firstOrNull { it.key in conversationId }
        ?.value
}

private fun Vector2.isWestOfPlayer(playerPosition: Vector2): Boolean {
    return x < playerPosition.x && abs(y - playerPosition.y) < abs(x - playerPosition.x)
}

private fun Vector2.isEastOfPlayer(playerPosition: Vector2): Boolean {
    return x > playerPosition.x && abs(y - playerPosition.y) < abs(x - playerPosition.x)
}

private fun Vector2.isSouthOfPlayer(playerPosition: Vector2): Boolean {
    return y < playerPosition.y && abs(y - playerPosition.y) > abs(x - playerPosition.x)
}

private fun Vector2.isNorthOfPlayer(playerPosition: Vector2): Boolean {
    return y > playerPosition.y && abs(y - playerPosition.y) > abs(x - playerPosition.x)
}
