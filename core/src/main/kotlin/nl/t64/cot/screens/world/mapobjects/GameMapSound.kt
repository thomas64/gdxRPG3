package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Vector2


class GameMapSound(mapObject: MapObject) {

    val name: String = mapObject.name
    private val containsCheck: (Vector2) -> Boolean = mapObject.createContainsCheck()

    fun contains(point: Vector2): Boolean {
        return containsCheck.invoke(point)
    }

    private fun MapObject.createContainsCheck(): (Vector2) -> Boolean {
        return when (this) {
            is RectangleMapObject -> { point: Vector2 -> this.rectangle.contains(point) }
            is PolygonMapObject -> { point: Vector2 -> this.polygon.contains(point) }
            else -> error("Sound object must be a rectangle or polygon.")
        }
    }

}
