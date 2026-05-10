package nl.t64.cot.subjects

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.mapobjects.GameMapNote


class ActionSubject {

    private val observers: MutableCollection<ActionObserver> = mutableListOf()

    fun addObserver(observer: ActionObserver) {
        if (observer !in observers) {
            observers.add(observer)
        }
    }

    fun removeObserver(observer: ActionObserver) {
        observers.remove(observer)
    }

    fun removeAllObservers() {
        observers.clear()
    }

    fun notifyActionPressed(checkRect: Rectangle, playerDirection: Direction, playerPosition: Vector2) {
        val allObservers = ArrayList(observers)
        val npcObserver: Entity? = allObservers
            .filterIsInstance<Entity>()
            .filter { it.isNpc() }
            .firstOrNull { it.isInActionRect(checkRect) }
        val nonEntityAndNonNoteObservers: List<ActionObserver> = allObservers
            .filterNot { it is Entity }
            .filterNot { it is GameMapNote }

        // example: if you talk to a npc, don't also check a door (is an entity, like the garrin house guard door in lastdenn).
        // if you talk to a npc, DO still check non-entity observers (for example quest checkers, like failing the quest with mr isembert at the honeywood farm).
        // except GameMapNote, which should never trigger during npc interaction.
        //
        // if you don't talk to a npc, check everything like normal.

        if (npcObserver != null) {
            npcObserver.onNotifyActionPressed(checkRect, playerDirection, playerPosition)
            nonEntityAndNonNoteObservers.forEach { it.onNotifyActionPressed(checkRect, playerDirection, playerPosition) }
        } else {
            allObservers.forEach { it.onNotifyActionPressed(checkRect, playerDirection, playerPosition) }
        }
    }

}
