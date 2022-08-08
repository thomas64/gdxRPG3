package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.EntityState.*
import nl.t64.cot.screens.world.entity.GraphicsScheduledNpc
import nl.t64.cot.screens.world.entity.InputEmpty
import nl.t64.cot.screens.world.entity.PhysicsScheduledNpc


class Garrin : EntitySchedule() {

    override val entity = Entity("man12", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("man12"))

    override fun getScheduleOfEntity(): List<ScheduleItem> {
        return listOf(
            // @formatter:off
            ScheduleItem("lastdenn_house_garrin", "07:30", "08:59", NORTH, IDLE,    "garrin1",  "garrin1",  "garrin_waiting"),
            ScheduleItem("lastdenn_house_garrin", "08:59", "09:04", NORTH, IDLE,    "garrin1",  "garrin1",  "garrin_it_is_time"),
            ScheduleItem("lastdenn_house_garrin", "09:04", "09:06", NORTH, IDLE,    "garrin1",  "garrin1",  "ghost_entering"),
            ScheduleItem("lastdenn_house_garrin", "09:06", "09:07", WEST,  IDLE,    "garrin1",  "garrin1",  "ghost_entering"),
            ScheduleItem("lastdenn_house_garrin", "09:07", "09:08", WEST,  RUNNING, "garrin1",  "garrin2",  "ghost_entering"),
            ScheduleItem("lastdenn_house_garrin", "09:08", "09:20", NORTH, IDLE,    "garrin2",  "garrin2",  "paton_hiding_behind_garrin"),
            ScheduleItem("lastdenn_house_garrin", "09:20", "09:21", EAST,  RUNNING, "garrin2",  "garrin3",  "ghost_scaring"),
            ScheduleItem("lastdenn_house_garrin", "09:21", "09:23", WEST,  IDLE,    "garrin3",  "garrin3",  "ghost_scaring"),
            ScheduleItem("lastdenn_house_garrin", "09:23", "09:24", NORTH, RUNNING, "garrin3",  "ghost7",   "ghost_scaring"),
            ScheduleItem("lastdenn_house_garrin", "09:24", "09:26", WEST,  RUNNING, "ghost7",   "ghost3",   "ghost_scaring"),
            ScheduleItem("lastdenn_house_garrin", "09:26", "09:28", SOUTH, RUNNING, "ghost3",   "paton2",   "ghost_scaring"),
            ScheduleItem("lastdenn_house_garrin", "09:28", "10:02", NORTH, IDLE,    "paton2",   "paton2",   "ghost_scaring"),
            ScheduleItem("lastdenn_house_garrin", "10:02", "11:34", NORTH, IDLE,    "paton2",   "paton2",   "garrin_leave_us"),
            // todo, eerder gaan lopen
            ScheduleItem("lastdenn_house_garrin", "11:34", "11:41", NORTH, WALKING, "paton2",   "ghost3",   "garrin_what_to_do"),
            ScheduleItem("lastdenn_house_garrin", "11:41", "11:45", EAST,  WALKING, "ghost3",   "paton3",   "garrin_what_to_do"),
            ScheduleItem("lastdenn_house_garrin", "11:45", "11:49", SOUTH, WALKING, "paton3",   "garrin2",  "garrin_what_to_do"),
            ScheduleItem("lastdenn_house_garrin", "11:49", "11:52", EAST,  WALKING, "garrin2",  "garrin4",  "garrin_what_to_do"),
            ScheduleItem("lastdenn_house_garrin", "11:52", "11:54", SOUTH, WALKING, "garrin4",  "garrin5",  "garrin_what_to_do"),

            ScheduleItem("lastdenn",              "11:54", "11:55", SOUTH, WALKING, "garrin6",  "garrin7"),
            ScheduleItem("lastdenn",              "11:55", "12:05", EAST,  WALKING, "garrin7",  "garrin8",  "garrin_what_to_do"),
            ScheduleItem("lastdenn",              "12:05", "12:16", NORTH, WALKING, "garrin8",  "garrin9",  "garrin_what_to_do"),
            ScheduleItem("lastdenn",              "12:16", "12:27", WEST,  WALKING, "garrin9",  "garrin10", "garrin_what_to_do"),
            ScheduleItem("lastdenn",              "12:27", "12:34", NORTH, IDLE,    "garrin10", "garrin10", "garrin_what_to_do"),
            ScheduleItem("lastdenn",              "12:34", "12:49", EAST,  WALKING, "garrin10", "garrin11", "garrin_what_to_do"),
            ScheduleItem("lastdenn",              "12:49", "13:00", SOUTH, WALKING, "garrin11", "garrin12", "garrin_what_to_do"),

            ScheduleItem("lastdenn",              "13:00", "14:00", WEST,  IDLE,    "garrin12", "garrin12"),

            ScheduleItem("lastdenn",              "14:00", "14:18", NORTH, WALKING, "garrin13", "garrin14"),
            ScheduleItem("lastdenn",              "14:18", "14:57", EAST,  WALKING, "garrin14", "garrin15"),
            ScheduleItem("lastdenn",              "14:57", "15:00", NORTH, WALKING, "garrin15", "garrin16"),
            // @formatter:on
        )
    }

    override fun handleSideEffects() {
        if (mapManager.currentMap.mapTitle == "lastdenn") {
            if (gameData.clock.isCurrentTimeAt("11:53")) {
                screenManager.getWorldScreen().useDoor("door_wooden_left")
            }
            if (gameData.clock.isCurrentTimeAt("14:59")) {
                screenManager.getWorldScreen().useDoor("door_forged_left")
            }
        }
    }

}
