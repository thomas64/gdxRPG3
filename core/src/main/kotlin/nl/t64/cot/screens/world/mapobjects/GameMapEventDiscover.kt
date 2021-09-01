package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.event.Event
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.subjects.CollisionObserver


class GameMapEventDiscover(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle), CollisionObserver {

    private val event: Event = gameData.events.getEventById(rectObject.name)

    init {
        brokerManager.collisionObservers.addObserver(this)
    }

    override fun onNotifyCollision(playerBoundingBox: Rectangle, playerDirection: Direction) {
        if (playerBoundingBox.overlaps(rectangle)) {
            event.possibleStart()
        } else {
            event.resetRepeat()
        }
    }

}
