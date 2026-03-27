package nl.t64.cot.screens.world.schedule

import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.EntityState.*


class Paton : EntitySchedule() {

    override val entity = Entity("boy17", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("boy17"))
    override val invisibleTalking = Entity("", InputEmpty(), PhysicsScheduledNpc(), GraphicsEmpty())

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("lastdenn_house_garrin", "07:30", "08:59", WEST,  IDLE,     "paton1", "paton1", "paton_hiding"),
        SchedulePart("lastdenn_house_garrin", "08:59", "09:03", WEST,  IDLE,     "paton1", "paton1", "paton_silent"),
        SchedulePart("lastdenn_house_garrin", "09:03", "09:04", WEST,  WALKING,  "paton1", "paton2", "paton_silent"),
        SchedulePart("lastdenn_house_garrin", "09:04", "09:06", NORTH, RUNNING,  "paton2", "ghost3", "ghost_entering"),
        SchedulePart("lastdenn_house_garrin", "09:06", "09:07", EAST,  RUNNING,  "ghost3", "paton3", "ghost_entering"),
        SchedulePart("lastdenn_house_garrin", "09:07", "09:08", SOUTH, RUNNING,  "paton3", "paton4", "ghost_entering"),
        SchedulePart("lastdenn_house_garrin", "09:08", "09:20", NORTH, IMMOBILE, "paton4", "paton4", "paton_hiding_behind_garrin"),
        SchedulePart("lastdenn_house_garrin", "09:20", "09:21", EAST,  RUNNING,  "paton4", "paton5", "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:21", "09:28", WEST,  IMMOBILE, "paton5", "paton5", "paton_hiding_behind_garrin"),
        SchedulePart("lastdenn_house_garrin", "09:28", "09:30", NORTH, RUNNING,  "paton5", "paton6", "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:30", "09:35", SOUTH, IMMOBILE, "paton6", "paton6", "paton_hiding_behind_garrin"),
        SchedulePart("lastdenn_house_garrin", "09:35", "09:37", WEST,  RUNNING,  "paton6", "ghost3", "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:37", "09:39", SOUTH, RUNNING,  "ghost3", "paton2", "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:39", "09:40", EAST,  WALKING,  "paton2", "paton1", "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:40", "09:50", NORTH, IMMOBILE, "paton1", "paton1", "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "11:00", "20:00", NORTH, IDLE,     "paton1", "paton1", "paton_hiding_again"),
        // @formatter:on
    )

    override fun handleSideEffects() {
        // empty
    }

}
