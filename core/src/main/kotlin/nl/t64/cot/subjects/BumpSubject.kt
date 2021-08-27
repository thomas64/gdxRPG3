package nl.t64.cot.subjects

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2


class BumpSubject {

    private val observers: MutableList<BumpObserver> = ArrayList()

    fun addObserver(observer: BumpObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: BumpObserver) {
        observers.remove(observer)
    }

    fun removeAllObservers() {
        observers.clear()
    }

    fun notifyBump(biggerBoundingBox: Rectangle, checkRect: Rectangle, playerPosition: Vector2) {
        ArrayList(observers).forEach { it.onNotifyBump(biggerBoundingBox, checkRect, playerPosition) }
    }

}
