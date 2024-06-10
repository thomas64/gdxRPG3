package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.EntityState.*
import nl.t64.cot.screens.world.entity.events.UpdateScheduledEntityEvent


class HoneywoodResourceShop : EntitySchedule() {

    override val entity = Entity("woman01", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("woman01"))
    private val invisibleTalking = Entity("woman01", InputEmpty(), PhysicsScheduledNpc(), GraphicsEmpty())

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("honeywood", "08:59", "09:00", SOUTH, CRAWLING, "resource1", "resource2"),
        SchedulePart("honeywood", "09:00", "17:00", SOUTH, IDLE,     "resource2", "resource2"),
        SchedulePart("honeywood", "17:00", "17:01", NORTH, CRAWLING, "resource2", "resource1"),
        // @formatter:on
    )

    override fun handleSideEffects() {
        if (mapManager.currentMap.mapTitle == "honeywood") {
            if (gameData.clock.isCurrentTimeInBetween("09:00", "17:00")) {
                val part = SchedulePart("honeywood", "09:00", "17:00", NONE, INVISIBLE, "resource3", "resource3", "resourceshop_honeywood")
                val event = UpdateScheduledEntityEvent(part.state, part.direction, part.getCurrentPosition(), part.conversationId)
                invisibleTalking.send(event)
                worldScreen.addScheduledEntity(invisibleTalking)
                brokerManager.actionObservers.addObserver(invisibleTalking)
            } else {
                brokerManager.actionObservers.removeObserver(invisibleTalking)
                worldScreen.removeScheduledEntity(invisibleTalking)
            }
        }
    }

}
