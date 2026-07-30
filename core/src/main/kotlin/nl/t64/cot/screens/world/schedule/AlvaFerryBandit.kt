package nl.t64.cot.screens.world.schedule

import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.EntityState.IDLE
import nl.t64.cot.screens.world.entity.EntityState.WALKING
import nl.t64.cot.screens.world.entity.GraphicsScheduledNpc
import nl.t64.cot.screens.world.entity.InputEmpty
import nl.t64.cot.screens.world.entity.PhysicsScheduledNpc


class AlvaFerryBandit : EntitySchedule() {

    override val entity = Entity("brown_bandit", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("brown_bandit"))

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("alva_road_west", "14:00", "14:02", SOUTH, WALKING, "alva_ferry2", "alva_ferry3",                       conditions = listOf("are_ferry_bandits_alive")),
        SchedulePart("alva_road_west", "14:02", "14:58", EAST,  IDLE,    "alva_ferry3", "alva_ferry3", "alva_ferry_bandits", conditions = listOf("are_ferry_bandits_alive")),
        SchedulePart("alva_road_west", "14:58", "15:00", NORTH, WALKING, "alva_ferry3", "alva_ferry2",                       conditions = listOf("are_ferry_bandits_alive")),
        // @formatter:on
    )

    override fun handleSideEffects() {
        // empty
    }

}
