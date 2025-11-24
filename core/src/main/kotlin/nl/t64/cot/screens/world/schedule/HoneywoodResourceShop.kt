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
        SchedulePart("honeywood", "08:29", "08:30", SOUTH, WALKING, "resource1", "resource2"),
        SchedulePart("honeywood", "08:30", "17:30", SOUTH, IDLE,    "resource2", "resource2", "resourceshop_honeywood"),
        SchedulePart("honeywood", "17:30", "17:31", NORTH, WALKING, "resource2", "resource1"),
        // @formatter:on
    )

    override fun handleSideEffects() {
        if (mapManager.currentMap.mapTitle == "honeywood"
            && gameData.clock.isCurrentTimeInBetween("08:30", "17:30")
        ) {
            val part = SchedulePart("honeywood", "08:30", "17:30", NONE, INVISIBLE, "resource3", "resource3", "resourceshop_honeywood")
            setupInvisibleTalking(part)
        } else {
            removeInvisibleTalking()
        }
    }

}
