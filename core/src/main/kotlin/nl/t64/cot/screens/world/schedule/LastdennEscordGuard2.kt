package nl.t64.cot.screens.world.schedule

import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.Direction.EAST
import nl.t64.cot.screens.world.entity.Direction.NORTH
import nl.t64.cot.screens.world.entity.EntityState.WALKING


class LastdennEscordGuard2 : EntitySchedule() {

    override val entity = Entity("soldier12", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("soldier12"))
    override val invisibleTalking = Entity("", InputEmpty(), PhysicsEmpty(), GraphicsEmpty())

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("lastdenn", "14:00", "14:30", NORTH, WALKING,  "santino14",     "garrinToJail1", "guard_escorting_garrin", conditions = listOf("is_garrin_possessed")),
        SchedulePart("lastdenn", "14:30", "14:59", EAST,  WALKING,  "garrinToJail1", "garrinToJail2", "guard_escorting_garrin", conditions = listOf("is_garrin_possessed")),
        SchedulePart("lastdenn", "14:59", "15:02", NORTH, WALKING,  "garrinToJail2", "garrinToJail3", "guard_escorting_garrin", conditions = listOf("is_garrin_possessed")),
        // @formatter:on
    )

    override fun handleSideEffects() {
        // empty
    }

}
