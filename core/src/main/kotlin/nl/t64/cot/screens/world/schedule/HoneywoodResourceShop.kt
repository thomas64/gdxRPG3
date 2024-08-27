package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.EntityState.*


class HoneywoodResourceShop : EntitySchedule() {

    override val entity = Entity("woman01", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("woman01"))
    override val invisibleTalking = Entity("woman01", InputEmpty(), PhysicsScheduledNpc(), GraphicsEmpty())

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("honeywood", "08:59", "09:00", SOUTH, WALKING, "resource1", "resource2"),
        SchedulePart("honeywood", "09:00", "17:00", SOUTH, IDLE,    "resource2", "resource2", "resourceshop_honeywood"),
        SchedulePart("honeywood", "17:00", "17:01", NORTH, WALKING, "resource2", "resource1"),
        // @formatter:on
    )

    override fun handleSideEffects() {
        if (mapManager.currentMap.mapTitle == "honeywood"
            && gameData.clock.isCurrentTimeInBetween("09:00", "17:00")
        ) {
            val part = SchedulePart("honeywood", "09:00", "17:00", NONE, INVISIBLE, "resource3", "resource3", "resourceshop_honeywood")
            setupInvisibleTalking(part)
        } else {
            removeInvisibleTalking()
        }
    }

}
