package nl.t64.cot.screens.world.map

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import nl.t64.cot.constants.Constant


private const val FOG_OF_WAR_RADIUS = Constant.TILE_SIZE * 5 - 1
private const val EXPLORED_CHAR = '#'
private const val UNEXPLORED_CHAR = '.'
private const val SCREEN_ASPECT_RATIO: Float = Constant.SCREEN_WIDTH.toFloat() / Constant.SCREEN_HEIGHT.toFloat()
// The sight radius counted in fog points, rounded up so the scanned square is never too small.
private const val SIGHT_IN_POINTS: Int = (FOG_OF_WAR_RADIUS / Constant.HALF_TILE_SIZE).toInt() + 1

// Which parts of every map the player has already seen. Stored with the profile in its own file (fow1.dat).
// Only maps you can zoom out on get fog, so a map smaller than the screen is missing from the container.
internal class FogOfWar : Json.Serializable {

    private val container: MutableMap<String, MapFog> = HashMap()

    fun putIfAbsent(currentMap: GameMap) {
        container.getOrPut(currentMap.mapTitle) { createMapFogFor(currentMap) }
    }

    fun update(playerPosition: Vector2, currentMap: GameMap) {
        val mapFog: MapFog = container[currentMap.mapTitle] ?: return
        // playerPosition is the bottom left corner of the sprite, so the center lies half a tile up and to the right.
        val centre = Vector2(playerPosition.x + Constant.HALF_TILE_SIZE,
                             playerPosition.y + Constant.HALF_TILE_SIZE)
        val sightRadius = Circle(centre, FOG_OF_WAR_RADIUS)
        mapFog.getPointsAround(centre)
            .filter { sightRadius.contains(it) }
            .forEach { it.isExplored = true }
    }

    fun draw(shapeRenderer: ShapeRenderer, mapTitle: String) {
        val mapFog: MapFog = container[mapTitle] ?: return
        shapeRenderer.color = Color.BLACK
        mapFog.createBlackRectangles().forEach { shapeRenderer.rect(it.x, it.y, it.width, it.height) }
    }

    override fun write(json: Json) {
        container.forEach { (mapTitle: String, mapFog: MapFog) ->
            json.writeArrayStart(mapTitle)
            createRowTextsFrom(mapFog).forEach { json.writeValue(it) }
            json.writeArrayEnd()
        }
    }

    override fun read(json: Json, jsonData: JsonValue) {
        jsonData.forEach { mapData: JsonValue ->
            container[mapData.name] = readMapFogFrom(mapData)
        }
    }

    private fun createMapFogFor(gameMap: GameMap): MapFog {
        return MapFog(gameMap.width, gameMap.height, createFogPoints(gameMap.width, gameMap.height))
    }

    // Top row first: y grows upwards in the game, but text lines grow downwards.
    private fun createRowTextsFrom(mapFog: MapFog): List<String> {
        return (mapFog.rowCount - 1 downTo 0)
            .map { row -> createRowTextFrom(mapFog, row) }
    }

    private fun createRowTextFrom(mapFog: MapFog, row: Int): String {
        return (0 until mapFog.columnCount)
            .map { column -> mapFog.getPoint(column, row) }
            .map { if (it.isExplored) EXPLORED_CHAR else UNEXPLORED_CHAR }
            .joinToString("")
    }

    // The stored rows are the only size there is: one line per row, one character per column.
    private fun readMapFogFrom(rowTexts: JsonValue): MapFog {
        val rowCount: Int = rowTexts.size
        val columnCount: Int = rowTexts.getString(0).length
        val fogPoints: List<FogPoint> = createFogPoints(columnCount, rowCount)
        val mapFog = MapFog(columnCount, rowCount, fogPoints)
        applyRowTextsTo(mapFog, rowTexts)
        return mapFog
    }

    private fun applyRowTextsTo(mapFog: MapFog, rowTexts: JsonValue) {
        rowTexts.forEachIndexed { index: Int, rowText: JsonValue ->
            applyRowTextTo(mapFog, mapFog.rowCount - 1 - index, rowText.asString())
        }
    }

    private fun applyRowTextTo(mapFog: MapFog, row: Int, rowText: String) {
        rowText.forEachIndexed { column: Int, char: Char ->
            mapFog.getPoint(column, row).isExplored = char == EXPLORED_CHAR
        }
    }

    // Column by column, y ascending. This order is what getPoint indexes into and what the save file stores.
    private fun createFogPoints(columnCount: Int, rowCount: Int): List<FogPoint> {
        return (0 until columnCount)
            .flatMap { column -> createColumnOfFogPoints(column, rowCount) }
    }

    private fun createColumnOfFogPoints(column: Int, rowCount: Int): List<FogPoint> {
        return (0 until rowCount)
            .map { row -> FogPoint(column.toFloat(), row.toFloat()) }
    }

}

private class MapFog(
    val columnCount: Int,
    val rowCount: Int,
    private val points: List<FogPoint>
) {

    // A square block around the player. The circle check in FogOfWar.update cuts off its corners.
    fun getPointsAround(player: Vector2): List<FogPoint> {
        val playerColumn: Int = (player.x / Constant.HALF_TILE_SIZE).toInt()
        val playerRow: Int = (player.y / Constant.HALF_TILE_SIZE).toInt()
        val rows: IntRange = createRangeAround(playerRow, rowCount)
        return createRangeAround(playerColumn, columnCount)
            .flatMap { column -> rows.map { row -> getPoint(column, row) } }
    }

    // The points are stored column by column, so point (column, row) sits at column * rowCount + row.
    fun getPoint(column: Int, row: Int): FogPoint {
        return points[column * rowCount + row]
    }

    fun createBlackRectangles(): List<Rectangle> {
        return createBlackRectanglesBesideMap() + createBlackRectanglesOnMap()
    }

    private fun createRangeAround(player: Int, count: Int): IntRange {
        return (player - SIGHT_IN_POINTS).coerceAtLeast(0)..(player + SIGHT_IN_POINTS).coerceAtMost(count - 1)
    }

    // A map narrower than the screen leaves a strip on either side. Those strips are one rectangle each,
    // instead of thousands of fog points that lie outside the map and could never be explored. Rounded
    // up, so an odd remainder is covered too; drawing one column past the screen edge harms nothing.
    private fun createBlackRectanglesBesideMap(): List<Rectangle> {
        val stripColumnCount: Int = ((rowCount * SCREEN_ASPECT_RATIO).toInt() - columnCount + 1) / 2
        if (stripColumnCount <= 0) return emptyList()

        val stripWidth: Float = stripColumnCount * Constant.HALF_TILE_SIZE
        val mapWidth: Float = columnCount * Constant.HALF_TILE_SIZE
        val mapHeight: Float = rowCount * Constant.HALF_TILE_SIZE
        return listOf(Rectangle(-stripWidth, 0f, stripWidth, mapHeight),
                      Rectangle(mapWidth, 0f, stripWidth, mapHeight))
    }

    private fun createBlackRectanglesOnMap(): List<Rectangle> {
        return points
            .filter { !it.isExplored }
            .groupBy { it.x }
            .flatMap { (x: Float, column: List<FogPoint>) -> createBlackRectanglesFor(x, column) }
    }

    // Neighboring dark points in one column become a single tall rectangle,
    // which turns thousands of draw calls into a few hundred.
    private fun createBlackRectanglesFor(x: Float, column: List<FogPoint>): List<Rectangle> {
        val rectangles: MutableList<Rectangle> = ArrayList()
        var startY: Float = column.first().y
        var endY: Float = startY

        column.drop(1).forEach { fogPoint: FogPoint ->
            if (fogPoint.y - endY > Constant.HALF_TILE_SIZE) {
                rectangles.add(createBlackRectangle(x, startY, endY))
                startY = fogPoint.y
            }
            endY = fogPoint.y
        }
        rectangles.add(createBlackRectangle(x, startY, endY))
        return rectangles
    }

    private fun createBlackRectangle(x: Float, startY: Float, endY: Float): Rectangle {
        return Rectangle(x, startY, Constant.HALF_TILE_SIZE, endY - startY + Constant.HALF_TILE_SIZE)
    }

}

private class FogPoint(
    column: Float,
    row: Float
) : Vector2(
    column * Constant.HALF_TILE_SIZE,
    row * Constant.HALF_TILE_SIZE
) {
    var isExplored: Boolean = false

}
