package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import ktx.tiled.property
import ktx.tiled.type
import nl.t64.cot.screens.world.entity.Direction


class GameMapSpawnPoint(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle) {

    private val fromMapName: String = rectObject.name
    private val fromMapLocation: String = rectObject.type ?: ""
    val direction: Direction = Direction.valueOf(rectObject.property("direction", "NONE").uppercase())

    val x: Float get() = rectangle.getX()
    val y: Float get() = rectangle.getY()

    fun isInConnectionWith(portal: GameMapRelocator): Boolean {
        return fromMapName == portal.fromMapName &&
                fromMapLocation.equals(portal.toMapLocation, true)
    }

}
