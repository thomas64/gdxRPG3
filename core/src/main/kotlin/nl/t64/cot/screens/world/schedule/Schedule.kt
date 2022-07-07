package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent


abstract class Schedule {

    abstract val entity: Entity

    fun update() {
        getSchedule()
            .filter { it.isCurrentMapInState() }
            .singleOrNull { it.isCurrentTimeInState() }
            ?.handle()
            ?: remove()
    }

    private fun ScheduleState.handle() {
        entity.send(LoadEntityEvent(state, direction, getCurrentPosition(), "default"))
        brokerManager.entityObservers.notifyAddScheduledEntity(entity)
    }

    private fun remove() {
        brokerManager.entityObservers.notifyRemoveScheduledEntity(entity)
    }

    abstract fun getSchedule(): List<ScheduleState>

}
