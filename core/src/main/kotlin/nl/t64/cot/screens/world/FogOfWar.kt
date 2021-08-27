package nl.t64.cot.screens.world

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.constants.Constant


private const val FOG_OF_WAR_RADIUS = Constant.TILE_SIZE * 6 - 1

internal class FogOfWar {

    private val container: MutableMap<String, Set<FogPoint>> = HashMap()

    fun putIfAbsent(gameMap: GameMap) {
        container.putIfAbsent(gameMap.mapTitle, fillFogOfWar(gameMap))
    }

    fun update(playerPosition: Vector2, mapTitle: String) {
        val sightRadius = Circle(playerPosition, FOG_OF_WAR_RADIUS)
        container[mapTitle]!!
            .filter { sightRadius.contains(it) }
            .forEach { it.isExplored = true }
    }

    fun draw(shapeRenderer: ShapeRenderer, mapTitle: String) {
        shapeRenderer.color = Color.BLACK
        container[mapTitle]!!
            .filter { !it.isExplored }
            .forEach { shapeRenderer.rect(it.x, it.y, Constant.HALF_TILE_SIZE, Constant.HALF_TILE_SIZE) }
    }

    private fun fillFogOfWar(gameMap: GameMap): Set<FogPoint> {
        return (0 until gameMap.width)
            .map { createFogPoints(it, gameMap) }
            .flatten()
            .toSet()
    }

    private fun createFogPoints(x: Int, gameMap: GameMap): Set<FogPoint> {
        return (0 until gameMap.height).map { FogPoint(x.toFloat(), it.toFloat()) }.toSet()
    }

}

private class FogPoint(
    x: Float = 0f, y: Float = 0f
) : Vector2(x * Constant.HALF_TILE_SIZE, y * Constant.HALF_TILE_SIZE) {
    var isExplored = false
}
