package nl.t64.cot.screens.world.schedule

import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.Direction.EAST
import nl.t64.cot.screens.world.entity.Direction.NORTH
import nl.t64.cot.screens.world.entity.EntityState.WALKING


class LastdennEscordGuard1 : EntitySchedule() {

    override val entity = Entity("soldier12", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("soldier12"))
    override val invisibleTalking = Entity("", InputEmpty(), PhysicsEmpty(), GraphicsEmpty())

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("lastdenn", "14:00", "14:26", NORTH, WALKING,  "possessed11",   "garrinToJail1", "guard_escorting_garrin", listOf("is_garrin_possessed")),
        SchedulePart("lastdenn", "14:26", "14:55", EAST,  WALKING,  "garrinToJail1", "garrinToJail2", "guard_escorting_garrin", listOf("is_garrin_possessed")),
        SchedulePart("lastdenn", "14:55", "14:58", NORTH, WALKING,  "garrinToJail2", "garrinToJail3", "guard_escorting_garrin", listOf("is_garrin_possessed")),
        // @formatter:on
    )

    override fun handleSideEffects() {
        // empty
    }

}
