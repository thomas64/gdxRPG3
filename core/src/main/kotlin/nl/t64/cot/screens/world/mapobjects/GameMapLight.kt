package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2


class GameMapLight(rectObject: RectangleMapObject) {

    val id: String = rectObject.name ?: "default"
    val center: Vector2 = createCenter(rectObject)

    private fun createCenter(rectObject: RectangleMapObject): Vector2 {
        val rectangle: Rectangle = rectObject.rectangle
        return rectangle.getCenter(Vector2(rectangle.x, rectangle.y))
    }

}
