package nl.t64.cot.subjects

import nl.t64.cot.screens.world.entity.Entity


class EntitySubject {

    private val observers: MutableList<EntityObserver> = ArrayList()

    fun addObserver(observer: EntityObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: EntityObserver) {
        observers.remove(observer)
    }

    fun removeAllObservers() {
        observers.clear()
    }

    fun notifyPartyUpdate() {
        observers.forEach { it.onNotifyPartyUpdate() }
    }

    fun notifyNpcsUpdate(newNpcEntities: List<Entity>) {
        observers.forEach { it.onNotifyNpcsUpdate(newNpcEntities) }
    }

    fun notifyAddScheduledEntity(entity: Entity) {
        observers.forEach { it.onNotifyAddScheduledEntity(entity) }
    }

    fun notifyRemoveScheduledEntity(entity: Entity) {
        observers.forEach { it.onNotifyRemoveScheduledEntity(entity) }
    }

}
