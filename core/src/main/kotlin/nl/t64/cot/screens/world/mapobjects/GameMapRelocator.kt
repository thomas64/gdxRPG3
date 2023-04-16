package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import ktx.tiled.type
import nl.t64.cot.screens.world.entity.Direction


abstract class GameMapRelocator(
    rectangle: Rectangle,
    val fromMapName: String,
    val toMapName: String,
    val toMapLocation: String,
    val fadeColor: Color
) : GameMapObject(rectangle) {

    lateinit var enterDirection: Direction

    companion object {
        val RectangleMapObject.toMapName: String get() = name
        val RectangleMapObject.toMapLocation: String get() = type.orEmpty()

        fun createAutoPortal(fromMapName: String, toMapName: String): GameMapRelocator {
            val mapObject = RectangleMapObject().apply { name = toMapName }
            return GameMapPortal(mapObject, fromMapName)
        }

        fun createPortalForNewLoad(mapTitle: String): GameMapRelocator {
            val dummyObject = RectangleMapObject()
            return GameMapPortal(dummyObject, mapTitle)
        }
    }

}
