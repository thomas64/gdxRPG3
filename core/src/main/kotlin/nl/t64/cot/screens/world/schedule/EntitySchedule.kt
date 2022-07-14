package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.EntityState
import nl.t64.cot.screens.world.entity.events.UpdateScheduledEntityEvent


abstract class EntitySchedule {

    abstract val entity: Entity

    fun update() {
        getScheduleOfEntity()
            .filter { it.isCurrentMapInState() }
            .singleOrNull { it.isCurrentTimeInState() }
            ?.handle()
            ?: remove()
        handleSideEffects()
    }

    private fun ScheduleItem.handle() {
        entity.send(UpdateScheduledEntityEvent(state, direction, getCurrentPosition(), conversationId))
        brokerManager.entityObservers.notifyAddScheduledEntity(entity)
        brokerManager.actionObservers.addObserver(entity)
        if (state == EntityState.IDLE) {
            brokerManager.blockObservers.addObserver(entity)
        } else {
            brokerManager.blockObservers.removeObserver(entity)
        }
    }

    private fun remove() {
        brokerManager.blockObservers.removeObserver(entity)
        brokerManager.actionObservers.removeObserver(entity)
        brokerManager.entityObservers.notifyRemoveScheduledEntity(entity)
    }

    abstract fun getScheduleOfEntity(): List<ScheduleItem>
    abstract fun handleSideEffects()

}
