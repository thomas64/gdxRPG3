package nl.t64.cot.screens.world

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.constants.Constant


private const val FOG_OF_WAR_RADIUS = Constant.TILE_SIZE * 6 - 1

internal class FogOfWar {

    companion object {
        private var timer = 0f
    }

    private val container: MutableMap<String, Set<FogPoint>> = HashMap()

    fun putIfAbsent(currentMap: GameMap) {
        container.putIfAbsent(currentMap.mapTitle, fillFogOfWar(currentMap))
    }

    fun update(playerPosition: Vector2, currentMap: GameMap, dt: Float) {
        timer += dt
        if (timer > 1f) {
            timer -= 1f
            update(playerPosition, currentMap)
        }
    }

    private fun update(playerPosition: Vector2, currentMap: GameMap) {
        val sightRadius = Circle(playerPosition, FOG_OF_WAR_RADIUS)
        container[currentMap.mapTitle]!!
            .filter { sightRadius.contains(it) }
            .filter { !it.isOutsideMap(currentMap) }
            .forEach { it.isExplored = true }
    }

    fun draw(shapeRenderer: ShapeRenderer, mapTitle: String) {
        shapeRenderer.color = Color.BLACK
        container[mapTitle]!!
            .filter { !it.isExplored }
            .forEach { shapeRenderer.rect(it.x, it.y, Constant.HALF_TILE_SIZE, Constant.HALF_TILE_SIZE) }
    }

    private fun fillFogOfWar(gameMap: GameMap): Set<FogPoint> {
        val leftOfScreen: Int
        val screenWidth: Int
        if (gameMap.width < gameMap.height) {
            screenWidth = ((gameMap.height / 9f) * 16f).toInt()     // 16:9
            leftOfScreen = (0f - ((screenWidth - gameMap.width) / 2f) - (gameMap.width / 2f)).toInt()
        } else {
            screenWidth = gameMap.width
            leftOfScreen = 0
        }
        return createFogPoints(leftOfScreen, screenWidth, gameMap.height)
    }

    private fun createFogPoints(leftOfScreen: Int, screenWidth: Int, gameMapHeight: Int): Set<FogPoint> {
        return (leftOfScreen until screenWidth)
            .map { x -> createFogPoints(x, gameMapHeight) }
            .flatten()
            .toSet()
    }

    private fun createFogPoints(x: Int, gameMapHeight: Int): Set<FogPoint> {
        return (0 until gameMapHeight)
            .map { y -> FogPoint(x.toFloat(), y.toFloat()) }
            .toSet()
    }

}

private class FogPoint(
    x: Float = 0f, y: Float = 0f
) : Vector2(x * Constant.HALF_TILE_SIZE, y * Constant.HALF_TILE_SIZE) {
    var isExplored = false

    fun isOutsideMap(currentMap: GameMap): Boolean {
        return currentMap.isOutsideMap(this)
    }

}
