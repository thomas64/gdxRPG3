package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.subjects.BlockObserver


class GameMapBlocker(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle), BlockObserver {

    init {
        brokerManager.blockObservers.addObserver(this)
    }

    override fun getBlockerFor(boundingBox: Rectangle): Rectangle? {
        return rectangle.takeIf { boundingBox.overlaps(it) }
    }

    override fun isBlocking(point: Vector2): Boolean {
        return rectangle.contains(point)
    }

}
