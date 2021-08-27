package nl.t64.cot.subjects

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.screens.world.entity.Direction


class ActionSubject {

    private val observers: MutableList<ActionObserver> = ArrayList()

    fun addObserver(observer: ActionObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: ActionObserver) {
        observers.remove(observer)
    }

    fun removeAllObservers() {
        observers.clear()
    }

    fun notifyActionPressed(checkRect: Rectangle, playerDirection: Direction, playerPosition: Vector2) {
        ArrayList(observers).forEach { it.onNotifyActionPressed(checkRect, playerDirection, playerPosition) }
    }

}
