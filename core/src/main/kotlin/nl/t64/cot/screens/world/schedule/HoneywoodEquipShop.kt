package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.EntityState.*


class HoneywoodEquipShop : EntitySchedule() {

    override val entity = Entity("man01", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("man01"))
    override val invisibleTalking = Entity("man01", InputEmpty(), PhysicsScheduledNpc(), GraphicsEmpty())

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("honeywood", "09:00", "09:01", SOUTH, WALKING, "equipment1", "equipment2"),
        SchedulePart("honeywood", "09:01", "17:01", SOUTH, IDLE,    "equipment2", "equipment2", "equipshop_honeywood"),
        SchedulePart("honeywood", "17:01", "17:02", NORTH, WALKING, "equipment2", "equipment1"),
        // @formatter:on
    )

    override fun handleSideEffects() {
        if (mapManager.currentMap.mapTitle == "honeywood"
            && gameData.clock.isCurrentTimeInBetween("09:01", "17:01")
        ) {
            val part = SchedulePart("honeywood", "09:01", "17:01", NONE, INVISIBLE, "equipment3", "equipment3", "equipshop_honeywood")
            setupInvisibleTalking(part)
        } else {
            removeInvisibleTalking()
        }
    }

}
