package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.subjects.ActionObserver


class GameMapStorage(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle), ActionObserver {

    init {
        brokerManager.actionObservers.addObserver(this)
    }

    override fun onNotifyActionPressed(checkRect: Rectangle, playerDirection: Direction, playerPosition: Vector2) {
        if (checkRect.overlaps(rectangle) && playerDirection == Direction.NORTH) {
            screenManager.getWorldScreen().showStorageScreen()
        }
    }

}
