package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.EntityState
import nl.t64.cot.screens.world.entity.events.UpdateScheduledEntityEvent


abstract class EntitySchedule {

    protected abstract val entity: Entity
    protected abstract val invisibleTalking: Entity
    protected abstract val scheduleParts: List<SchedulePart>

    fun update() {
        scheduleParts
            .filter { it.isCurrentMapInState() }
            .filter { it.isMeetingConditions() }
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
        handleBumping()
    }

    private fun SchedulePart.handleTalking() {
        if (conversationId.isBlank()) {
            brokerManager.actionObservers.removeObserver(entity)
        } else {
            brokerManager.actionObservers.addObserver(entity)
        }
    }

    private fun SchedulePart.handleBlocking() {
        if (state in listOf(EntityState.IDLE, EntityState.IMMOBILE)) {
            brokerManager.blockObservers.addObserver(entity)
        } else {
            brokerManager.blockObservers.removeObserver(entity)
        }
    }

    private fun SchedulePart.handleBumping() {
        if (state == EntityState.IDLE) {
            brokerManager.bumpObservers.addObserver(entity)
        } else {
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

    protected fun setupInvisibleTalking(part: SchedulePart) {
        val event = UpdateScheduledEntityEvent(part.state, part.direction, part.getCurrentPosition(), part.conversationId)
        invisibleTalking.send(event)
        worldScreen.addScheduledEntity(invisibleTalking)
        brokerManager.actionObservers.addObserver(invisibleTalking)
    }

    protected fun removeInvisibleTalking() {
        brokerManager.actionObservers.removeObserver(invisibleTalking)
        worldScreen.removeScheduledEntity(invisibleTalking)
    }

}
