package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.EntityState.*


class Garrin : EntitySchedule() {

    override val entity = Entity("man12", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("man12"))
    override val invisibleTalking = Entity("", InputEmpty(), PhysicsScheduledNpc(), GraphicsEmpty())

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("lastdenn_house_garrin", "07:30", "09:04", NORTH, IDLE,     "garrin1",  "garrin1",  "garrin_early_morning"),
        SchedulePart("lastdenn_house_garrin", "09:04", "09:07", WEST,  IMMOBILE, "garrin1",  "garrin1",  "ghost_entering"),
        SchedulePart("lastdenn_house_garrin", "09:07", "09:08", WEST,  RUNNING,  "garrin1",  "garrin2",  "ghost_entering"),
        SchedulePart("lastdenn_house_garrin", "09:08", "09:21", NORTH, IMMOBILE, "garrin2",  "garrin2",  "paton_hiding_behind_garrin"),
        SchedulePart("lastdenn_house_garrin", "09:21", "09:22", EAST,  RUNNING,  "garrin2",  "garrin3",  "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:22", "09:29", WEST,  IMMOBILE, "garrin3",  "garrin3",  "paton_hiding_behind_garrin"),
        SchedulePart("lastdenn_house_garrin", "09:29", "09:31", NORTH, RUNNING,  "garrin3",  "garrin4",  "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:31", "09:36", SOUTH, IMMOBILE, "garrin4",  "garrin4",  "paton_hiding_behind_garrin"),
        SchedulePart("lastdenn_house_garrin", "09:36", "09:38", WEST,  RUNNING,  "garrin4",  "garrin5",  "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:38", "09:50", EAST,  IMMOBILE, "garrin5",  "garrin5",  "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "11:00", "11:03", NORTH, IMMOBILE, "garrin6",  "garrin6"),
        SchedulePart("lastdenn_house_garrin", "11:03", "11:07", EAST,  CRAWLING, "garrin6",  "ghost4"),
        SchedulePart("lastdenn_house_garrin", "11:07", "11:14", SOUTH, CRAWLING, "ghost4",   "garrin2"),
        SchedulePart("lastdenn_house_garrin", "11:14", "11:19", EAST,  CRAWLING, "garrin2",  "garrin8"),
        SchedulePart("lastdenn_house_garrin", "11:19", "11:24", SOUTH, CRAWLING, "garrin8",  "garrin9"),

        SchedulePart("lastdenn",              "11:54", "11:55", SOUTH, WALKING, "garrin6",  "garrin7"),
        SchedulePart("lastdenn",              "11:55", "12:05", EAST,  WALKING, "garrin7",  "garrin8",  "garrin_what_to_do"),
        SchedulePart("lastdenn",              "12:05", "12:16", NORTH, WALKING, "garrin8",  "garrin9",  "garrin_what_to_do"),
        SchedulePart("lastdenn",              "12:16", "12:27", WEST,  WALKING, "garrin9",  "garrin10", "garrin_what_to_do"),
        SchedulePart("lastdenn",              "12:27", "12:34", NORTH, IDLE,    "garrin10", "garrin10", "garrin_what_to_do"),
        SchedulePart("lastdenn",              "12:34", "12:49", EAST,  WALKING, "garrin10", "garrin11", "garrin_what_to_do"),
        SchedulePart("lastdenn",              "12:49", "13:00", SOUTH, WALKING, "garrin11", "garrin12", "garrin_what_to_do"),

        SchedulePart("lastdenn",              "13:00", "14:00", WEST,  IDLE,    "garrin12", "garrin12"),

        SchedulePart("lastdenn",              "14:00", "14:18", NORTH, WALKING, "garrin13", "garrin14"),
        SchedulePart("lastdenn",              "14:18", "14:57", EAST,  WALKING, "garrin14", "garrin15"),
        SchedulePart("lastdenn",              "14:57", "15:00", NORTH, WALKING, "garrin15", "garrin16"),
        // @formatter:on
    )

    override fun handleSideEffects() {
        if (mapManager.currentMap.mapTitle == "lastdenn") {
            if (gameData.clock.isCurrentTimeAt("11:23")) {
                worldScreen.useDoor("door_wooden_left")
            }
            if (gameData.clock.isCurrentTimeAt("14:58")) {
                worldScreen.useDoor("door_forged_left")
            }
        }
    }

}
