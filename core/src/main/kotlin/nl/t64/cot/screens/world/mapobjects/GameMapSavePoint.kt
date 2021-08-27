package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.profileManager
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.subjects.ActionObserver


class GameMapSavePoint(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle), ActionObserver {

    init {
        brokerManager.actionObservers.addObserver(this)
    }

    override fun onNotifyActionPressed(checkRect: Rectangle, playerDirection: Direction, playerPosition: Vector2) {
        if (checkRect.overlaps(rectangle)) {
            profileManager.saveProfile()
        }
    }

}
