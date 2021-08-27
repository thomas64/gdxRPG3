package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.subjects.CollisionObserver


class GameMapPortal(rectObject: RectangleMapObject, fromMapName: String) : GameMapRelocator(
    rectObject.rectangle,
    fromMapName,
    rectObject.name,
    createToMapLocation(rectObject),
    Color.BLACK
), CollisionObserver {

    init {
        brokerManager.collisionObservers.addObserver(this)
    }

    override fun onNotifyCollision(playerBoundingBox: Rectangle, playerDirection: Direction) {
        if (playerBoundingBox.overlaps(rectangle)) {
            mapManager.collisionPortal(this, playerDirection)
        }
    }

}
