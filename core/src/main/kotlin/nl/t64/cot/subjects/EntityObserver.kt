package nl.t64.cot.subjects

import nl.t64.cot.screens.world.entity.Entity


interface EntityObserver {

    fun onNotifyPartyUpdate()
    fun onNotifyNpcsUpdate(newNpcEntities: List<Entity>)
    fun onNotifyAddScheduledEntity(entity: Entity)
    fun onNotifyRemoveScheduledEntity(entity: Entity)
}
