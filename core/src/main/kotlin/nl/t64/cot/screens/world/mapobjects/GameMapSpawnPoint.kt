package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import ktx.tiled.property
import ktx.tiled.type
import nl.t64.cot.screens.world.entity.Direction


class GameMapSpawnPoint(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle) {

    private val fromMapName: String = rectObject.name
    private val fromMapLocation: String = rectObject.type ?: ""
    val x: Float = rectObject.rectangle.x
    val y: Float = rectObject.rectangle.y
    val direction: Direction = Direction.valueOf(rectObject.property("direction", "NONE").uppercase())

    fun isInConnectionWith(portal: GameMapRelocator): Boolean {
        return fromMapName == portal.fromMapName &&
                fromMapLocation.equals(portal.toMapLocation, true)
    }

    fun isPortal(): Boolean {
        return fromMapName == "portal"
    }

}
