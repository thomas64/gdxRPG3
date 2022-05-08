package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.screens.world.entity.EntityState
import nl.t64.cot.subjects.BlockObserver


class GameMapBlockerLow(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle), BlockObserver {

    init {
        brokerManager.blockObservers.addObserver(this)
    }

    override fun getBlockerFor(boundingBox: Rectangle, state: EntityState): Rectangle? {
        return rectangle.takeIf {
            state != EntityState.FLYING && boundingBox.overlaps(it)
        }
    }

    override fun isBlocking(point: Vector2, state: EntityState): Boolean {
        return state != EntityState.FLYING && rectangle.contains(point)
    }

}
