package nl.t64.cot.screens.world.schedule

import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.EntityState.IDLE
import nl.t64.cot.screens.world.entity.EntityState.WALKING
import nl.t64.cot.screens.world.entity.GraphicsScheduledNpc
import nl.t64.cot.screens.world.entity.InputEmpty
import nl.t64.cot.screens.world.entity.PhysicsScheduledNpc


class LastdennPierGuard2 : EntitySchedule() {

    override val entity = Entity("soldier12", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("soldier12"))

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("lastdenn", "07:30", "19:15", SOUTH, IDLE,    "pier_guard21", "pier_guard21", "lastdenn_pier_guards"),
        SchedulePart("lastdenn", "19:15", "19:16", SOUTH, WALKING, "pier_guard21", "pier_guard22", "lastdenn_pier_guards"),
        SchedulePart("lastdenn", "19:16", "19:17", EAST,  WALKING, "pier_guard22", "pier_guard23", "lastdenn_pier_guards"),
        SchedulePart("lastdenn", "19:17", "20:00", WEST,  IDLE,    "pier_guard23", "pier_guard23", "lastdenn_pier_guards"),
        // @formatter:on
    )

    override fun handleSideEffects() {
        // empty
    }

}
