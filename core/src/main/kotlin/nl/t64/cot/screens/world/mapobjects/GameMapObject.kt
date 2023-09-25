package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.tiled.property
import ktx.tiled.propertyOrNull


abstract class GameMapObject(val rectangle: Rectangle) {

    protected fun createConditions(rectObject: RectangleMapObject): List<String> {
        return rectObject.propertyOrNull<String>("condition")
            ?.let { ids -> ids.split(",").map { it.trim() } }
            ?: emptyList()
    }

    protected fun createCertainConditions(rectObject: RectangleMapObject): List<String> {
        return rectObject.property<String>("condition")
            .let { ids -> ids.split(",").map { it.trim() } }
    }

    protected fun RectangleMapObject.getCenter(): Vector2 {
        return rectangle.getCenter(Vector2(rectangle.x, rectangle.y))
    }

}
