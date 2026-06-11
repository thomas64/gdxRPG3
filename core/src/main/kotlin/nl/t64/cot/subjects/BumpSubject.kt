package nl.t64.cot.subjects

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.screens.world.entity.Direction


class BumpSubject {

    private val observers: MutableList<BumpObserver> = ArrayList()

    fun addObserver(observer: BumpObserver) {
        if (observer !in observers) {
            observers.add(observer)
        }
    }

    fun removeObserver(observer: BumpObserver) {
        observers.remove(observer)
    }

    fun removeAllObservers() {
        observers.clear()
    }

    fun notifyBump(biggerBoundingBox: Rectangle, checkRect: Rectangle, playerPosition: Vector2, playerDirection: Direction) {
        ArrayList(observers).forEach { it.onNotifyBump(biggerBoundingBox, checkRect, playerPosition, playerDirection) }
    }

}
