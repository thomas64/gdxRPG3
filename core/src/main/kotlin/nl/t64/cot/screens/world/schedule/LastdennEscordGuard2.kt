package nl.t64.cot.screens.world.schedule

import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.Direction.EAST
import nl.t64.cot.screens.world.entity.Direction.NORTH
import nl.t64.cot.screens.world.entity.EntityState.WALKING


class LastdennEscordGuard2 : EntitySchedule() {

    override val entity = Entity("soldier02", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("soldier02"))
    override val invisibleTalking = Entity("", InputEmpty(), PhysicsEmpty(), GraphicsEmpty())

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("lastdenn", "14:00", "14:30", NORTH, WALKING,  "santino14",     "garrinToJail1", "guard_escorting_garrin"),
        SchedulePart("lastdenn", "14:30", "14:59", EAST,  WALKING,  "garrinToJail1", "garrinToJail2", "guard_escorting_garrin"),
        SchedulePart("lastdenn", "14:59", "15:02", NORTH, WALKING,  "garrinToJail2", "garrinToJail3", "guard_escorting_garrin"),
        // @formatter:on
    )

    override fun handleSideEffects() {
        // empty
    }

}
