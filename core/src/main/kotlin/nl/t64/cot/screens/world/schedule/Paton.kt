package nl.t64.cot.screens.world.schedule

import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.EntityState.*


class Paton : EntitySchedule() {

    override val entity = Entity("boy17", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("boy17"))
    override val invisibleTalking = Entity("", InputEmpty(), PhysicsScheduledNpc(), GraphicsEmpty())

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("lastdenn_house_garrin", "07:30", "08:59", WEST,  IDLE,    "paton1", "paton1", "paton_hiding"),
        SchedulePart("lastdenn_house_garrin", "08:59", "09:03", WEST,  IDLE,    "paton1", "paton1", "paton_silent"),
        SchedulePart("lastdenn_house_garrin", "09:03", "09:04", WEST,  WALKING, "paton1", "paton2", "paton_silent"),
        SchedulePart("lastdenn_house_garrin", "09:04", "09:06", NORTH, RUNNING, "paton2", "ghost3", "ghost_entering"),
        SchedulePart("lastdenn_house_garrin", "09:06", "09:07", EAST,  RUNNING, "ghost3", "paton3", "ghost_entering"),
        SchedulePart("lastdenn_house_garrin", "09:07", "09:08", SOUTH, RUNNING, "paton3", "ghost5", "ghost_entering"),
        SchedulePart("lastdenn_house_garrin", "09:08", "09:20", NORTH, IDLE,    "ghost5", "ghost5", "paton_hiding_behind_garrin"),
        SchedulePart("lastdenn_house_garrin", "09:20", "09:21", EAST,  RUNNING, "ghost5", "ghost6", "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:21", "09:22", WEST,  IDLE,    "ghost6", "ghost6", "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:22", "09:23", NORTH, RUNNING, "ghost6", "ghost7", "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:23", "09:25", WEST,  RUNNING, "ghost7", "ghost3", "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:25", "09:27", SOUTH, RUNNING, "ghost3", "paton2", "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:27", "09:28", EAST,  WALKING, "paton2", "paton1", "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:28", "10:02", NORTH, IDLE,    "paton1", "paton1", "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "10:02", "20:00", NORTH, IDLE,    "paton1", "paton1"),
        // @formatter:on
    )

    override fun handleSideEffects() {
        // empty
    }

}
