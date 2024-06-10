package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.EntityState
import nl.t64.cot.screens.world.entity.events.UpdateScheduledEntityEvent


abstract class EntitySchedule {

    protected abstract val entity: Entity
    protected abstract val scheduleParts: List<SchedulePart>

    fun update() {
        scheduleParts
            .filter { it.isCurrentMapInState() }
            .singleOrNull { it.isCurrentTimeInState() }
            ?.handle()
            ?: remove()
        handleSideEffects()
    }

    private fun SchedulePart.handle() {
        entity.send(UpdateScheduledEntityEvent(state, direction, getCurrentPosition(), conversationId))
        worldScreen.addScheduledEntity(entity)
        handleTalking()
        handleBlocking()
    }

    private fun SchedulePart.handleTalking() {
        if (conversationId.isBlank()) {
            brokerManager.actionObservers.removeObserver(entity)
        } else {
            brokerManager.actionObservers.addObserver(entity)
        }
    }

    private fun SchedulePart.handleBlocking() {
        if (state == EntityState.IDLE) {
            brokerManager.blockObservers.addObserver(entity)
            brokerManager.bumpObservers.addObserver(entity)
        } else {
            brokerManager.blockObservers.removeObserver(entity)
            brokerManager.bumpObservers.removeObserver(entity)
        }
    }

    private fun remove() {
        brokerManager.bumpObservers.removeObserver(entity)
        brokerManager.blockObservers.removeObserver(entity)
        brokerManager.actionObservers.removeObserver(entity)
        worldScreen.removeScheduledEntity(entity)
    }

    protected abstract fun handleSideEffects()

}
