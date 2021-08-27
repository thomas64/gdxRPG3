package nl.t64.cot.subjects

import com.badlogic.gdx.math.Rectangle
import nl.t64.cot.screens.world.entity.Direction


class CollisionSubject {

    private val observers: MutableList<CollisionObserver> = ArrayList()

    fun addObserver(observer: CollisionObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: CollisionObserver) {
        observers.remove(observer)
    }

    fun removeAllObservers() {
        observers.clear()
    }

    fun notifyCollision(playerBoundingBox: Rectangle, playerDirection: Direction) {
        ArrayList(observers).forEach { it.onNotifyCollision(playerBoundingBox, playerDirection) }
    }

}
