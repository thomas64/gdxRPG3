package nl.t64.cot.screens.world.schedule

import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.EntityState.IDLE
import nl.t64.cot.screens.world.entity.EntityState.WALKING


class AlvaTraveler : EntitySchedule() {

    override val entity = Entity("traveler", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("traveler"))
    override val invisibleTalking = Entity("", InputEmpty(), PhysicsScheduledNpc(), GraphicsEmpty())

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("alva_road_west", "08:59", "09:02", WEST,  WALKING, "alva_traveler1", "alva_traveler2"),
        SchedulePart("alva_road_west", "09:02", "09:03", NORTH, WALKING, "alva_traveler2", "alva_traveler3"),
        SchedulePart("alva_road_west", "09:03", "09:04", WEST,  WALKING, "alva_traveler3", "alva_traveler4"),
        SchedulePart("alva_road_west", "09:04", "09:24", WEST,  IDLE,    "alva_traveler4", "alva_traveler4", "alva_traveler_waiting"),
        SchedulePart("alva_road_west", "09:24", "09:25", EAST,  WALKING, "alva_traveler4", "alva_traveler3"),
        SchedulePart("alva_road_west", "09:25", "09:26", SOUTH, WALKING, "alva_traveler3", "alva_traveler2"),
        SchedulePart("alva_road_west", "09:26", "09:29", EAST,  WALKING, "alva_traveler2", "alva_traveler1")
        // @formatter:on
    )

    override fun handleSideEffects() {
        // empty
    }

}
