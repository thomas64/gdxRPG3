package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import ktx.tiled.propertyOrNull
import ktx.tiled.type
import nl.t64.cot.screens.world.entity.Direction


class GameMapSpawnPoint(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle) {

    private val fromMapName: String = rectObject.name
    private val fromMapLocation: String = rectObject.type.orEmpty()
    val x: Float = rectObject.rectangle.x
    val y: Float = rectObject.rectangle.y
    val direction: Direction? = rectObject.toDirectionOrNull()

    fun isInConnectionWith(portal: GameMapPortal): Boolean {
        return fromMapName == portal.fromMapName &&
                fromMapLocation.equals(portal.toMapLocation, true)
    }

    fun isPortal(): Boolean {
        return fromMapName == "portal"
    }

    private fun RectangleMapObject.toDirectionOrNull(): Direction? {
        return this.propertyOrNull<String>("direction")
            ?.uppercase()
            ?.let { Direction.valueOf(it) }
    }

}
