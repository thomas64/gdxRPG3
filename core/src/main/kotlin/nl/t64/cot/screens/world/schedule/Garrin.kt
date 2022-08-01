package nl.t64.cot.screens.world.schedule

import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.EntityState.IDLE
import nl.t64.cot.screens.world.entity.EntityState.RUNNING
import nl.t64.cot.screens.world.entity.GraphicsScheduledNpc
import nl.t64.cot.screens.world.entity.InputEmpty
import nl.t64.cot.screens.world.entity.PhysicsScheduledNpc


class Garrin : EntitySchedule() {

    override val entity = Entity("man12", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("man12"))

    override fun getScheduleOfEntity(): List<ScheduleItem> {
        return listOf(
            // @formatter:off
            ScheduleItem("lastdenn_house_garrin", "07:30", "08:59", NORTH, IDLE,    "garrin1", "garrin1", "garrin_waiting"),
            ScheduleItem("lastdenn_house_garrin", "08:59", "09:04", NORTH, IDLE,    "garrin1", "garrin1", "garrin_it_is_time"),
            ScheduleItem("lastdenn_house_garrin", "09:04", "09:06", NORTH, IDLE,    "garrin1", "garrin1", "ghost_entering"),
            ScheduleItem("lastdenn_house_garrin", "09:06", "09:07", WEST,  IDLE,    "garrin1", "garrin1", "ghost_entering"),
            ScheduleItem("lastdenn_house_garrin", "09:07", "09:08", WEST,  RUNNING, "garrin1", "garrin2", "ghost_entering"),
            ScheduleItem("lastdenn_house_garrin", "09:08", "09:20", NORTH, IDLE,    "garrin2", "garrin2", "paton_hiding_behind_garrin"),
            ScheduleItem("lastdenn_house_garrin", "09:20", "09:21", EAST,  RUNNING, "garrin2", "garrin3", "ghost_scaring"),
            ScheduleItem("lastdenn_house_garrin", "09:21", "09:23", WEST,  IDLE,    "garrin3", "garrin3", "ghost_scaring"),
            ScheduleItem("lastdenn_house_garrin", "09:23", "09:24", NORTH, RUNNING, "garrin3", "ghost7",  "ghost_scaring"),
            ScheduleItem("lastdenn_house_garrin", "09:24", "09:26", WEST,  RUNNING, "ghost7",  "ghost3",  "ghost_scaring"),
            ScheduleItem("lastdenn_house_garrin", "09:26", "09:28", SOUTH, RUNNING, "ghost3",  "paton2",  "ghost_scaring"),
            ScheduleItem("lastdenn_house_garrin", "09:28", "10:02", NORTH, IDLE,    "paton2",  "paton2",  "ghost_scaring"),
            ScheduleItem("lastdenn_house_garrin", "10:02", "20:00", NORTH, IDLE,    "paton2",  "paton2",  "garrin_leave_us"),
            // @formatter:on
        )
    }

    override fun handleSideEffects() {
        // empty
    }

}
