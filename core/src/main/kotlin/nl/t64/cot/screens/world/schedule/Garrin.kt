package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.components.condition.isTrue
import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.EntityState.*


class Garrin : EntitySchedule() {

    override val entity = Entity("man12", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("man12"))
    override val invisibleTalking = Entity("", InputEmpty(), PhysicsScheduledNpc(), GraphicsEmpty())

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("lastdenn_house_garrin", "07:30", "09:04", NORTH, IDLE,     "garrin1",       "garrin1",       "garrin_early_morning"),
        SchedulePart("lastdenn_house_garrin", "09:04", "09:07", WEST,  IMMOBILE, "garrin1",       "garrin1",       "ghost_entering"),
        SchedulePart("lastdenn_house_garrin", "09:07", "09:08", WEST,  RUNNING,  "garrin1",       "garrin2",       "ghost_entering"),
        SchedulePart("lastdenn_house_garrin", "09:08", "09:21", NORTH, IMMOBILE, "garrin2",       "garrin2",       "paton_hiding_behind_garrin"),
        SchedulePart("lastdenn_house_garrin", "09:21", "09:22", EAST,  RUNNING,  "garrin2",       "garrin3",       "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:22", "09:29", WEST,  IMMOBILE, "garrin3",       "garrin3",       "paton_hiding_behind_garrin"),
        SchedulePart("lastdenn_house_garrin", "09:29", "09:31", NORTH, RUNNING,  "garrin3",       "garrin4",       "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:31", "09:36", SOUTH, IMMOBILE, "garrin4",       "garrin4",       "paton_hiding_behind_garrin"),
        SchedulePart("lastdenn_house_garrin", "09:36", "09:38", WEST,  RUNNING,  "garrin4",       "garrin5",       "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:38", "09:50", EAST,  IMMOBILE, "garrin5",       "garrin5",       "ghost_scaring"),

        SchedulePart("lastdenn_house_garrin", "11:00", "11:03", NORTH, IMMOBILE, "garrin6",       "garrin6",       "garrin_possessed_nothing", conditions = listOf("is_garrin_possessed")),
        SchedulePart("lastdenn_house_garrin", "11:03", "11:07", EAST,  CRAWLING, "garrin6",       "ghost4",        "garrin_possessed_go_away", conditions = listOf("is_garrin_possessed")),
        SchedulePart("lastdenn_house_garrin", "11:07", "11:14", SOUTH, CRAWLING, "ghost4",        "garrin2",       "garrin_possessed_go_away", conditions = listOf("is_garrin_possessed")),
        SchedulePart("lastdenn_house_garrin", "11:14", "11:19", EAST,  CRAWLING, "garrin2",       "garrin8",       "garrin_possessed_go_away", conditions = listOf("is_garrin_possessed")),
        SchedulePart("lastdenn_house_garrin", "11:19", "11:24", SOUTH, CRAWLING, "garrin8",       "garrin9",       "garrin_possessed_go_away", conditions = listOf("is_garrin_possessed")),

        SchedulePart("lastdenn",              "11:24", "11:25", SOUTH, CRAWLING, "possessed1",    "possessed2",    "garrin_possessed_go_away", conditions = listOf("is_garrin_possessed")),
        SchedulePart("lastdenn",              "11:25", "11:35", EAST,  CRAWLING, "possessed2",    "possessed3",    "garrin_possessed_go_away", conditions = listOf("is_garrin_possessed")),
        SchedulePart("lastdenn",              "11:35", "12:00", SOUTH, CRAWLING, "possessed3",    "santino13",     "garrin_possessed_go_away", conditions = listOf("is_garrin_possessed")),
        SchedulePart("lastdenn",              "12:00", "12:15", EAST,  CRAWLING, "santino13",     "possessed4",    "garrin_possessed_go_away", conditions = listOf("is_garrin_possessed")),
        SchedulePart("lastdenn",              "12:15", "12:20", NORTH, CRAWLING, "possessed4",    "possessed5",    "garrin_possessed_go_away", conditions = listOf("is_garrin_possessed")),
        SchedulePart("lastdenn",              "12:20", "12:25", EAST,  CRAWLING, "possessed5",    "possessed6",    "garrin_possessed_go_away", conditions = listOf("is_garrin_possessed")),
        SchedulePart("lastdenn",              "12:25", "12:27", NORTH, CRAWLING, "possessed6",    "possessed7",    "garrin_possessed_go_away", conditions = listOf("is_garrin_possessed")),
        SchedulePart("lastdenn",              "12:27", "12:33", WEST,  CRAWLING, "possessed7",    "possessed8",    "garrin_possessed_go_away", conditions = listOf("is_garrin_possessed")),
        SchedulePart("lastdenn",              "12:33", "12:35", NORTH, CRAWLING, "possessed8",    "possessed9",    "garrin_possessed_go_away", conditions = listOf("is_garrin_possessed")),
        SchedulePart("lastdenn",              "12:35", "12:38", WEST,  CRAWLING, "possessed9",    "possessed10",   "garrin_possessed_go_away", conditions = listOf("is_garrin_possessed")),
        SchedulePart("lastdenn",              "12:38", "12:58", WEST,  CRAWLING, "possessed10",   "possessed11",   "garrin_possessed_go_away", conditions = listOf("is_garrin_possessed")),
        SchedulePart("lastdenn",              "12:58", "13:00", SOUTH, CRAWLING, "possessed11",   "possessed12",   "garrin_possessed_go_away", conditions = listOf("is_garrin_possessed")),

        SchedulePart("lastdenn",              "13:00", "14:00", SOUTH, IMMOBILE, "possessed12",   "possessed12",   "",         StateIcon.TALK, conditions = listOf("is_garrin_possessed")),

        SchedulePart("lastdenn",              "14:00", "14:28", NORTH, WALKING,  "possessed12",   "garrinToJail1", "garrin_to_jail",           conditions = listOf("is_garrin_possessed")),
        SchedulePart("lastdenn",              "14:28", "14:57", EAST,  WALKING,  "garrinToJail1", "garrinToJail2", "garrin_to_jail",           conditions = listOf("is_garrin_possessed")),
        SchedulePart("lastdenn",              "14:57", "15:00", NORTH, WALKING,  "garrinToJail2", "garrinToJail3", "garrin_to_jail",           conditions = listOf("is_garrin_possessed")),
        // @formatter:on
    )

    override fun handleSideEffects() {
        if (mapManager.currentMap.mapTitle == "lastdenn") {
            if (gameData.clock.isCurrentTimeAt("11:23") && "is_garrin_possessed".isTrue()) {
                worldScreen.useDoor("door_lastdenn_garrin")
            }
            if (gameData.clock.isCurrentTimeInBetween("14:56", "15:00") && "is_garrin_possessed".isTrue()) {
                worldScreen.useDoor("door_lastdenn_jail")
            }
        }
    }

}
