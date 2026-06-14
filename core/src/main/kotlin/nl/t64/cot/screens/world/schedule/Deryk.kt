package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.EntityState.*


class Deryk : EntitySchedule() {

    override val entity = Entity("deryk", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("deryk"))
    override val invisibleTalking = Entity("", InputEmpty(), PhysicsScheduledNpc(), GraphicsEmpty())

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("honeywood",             "17:34", "17:46", NORTH, WALKING,  "deryk1",  "deryk2", "deryk_on_the_way"),
        SchedulePart("honeywood",             "17:46", "17:49", WEST,  WALKING,  "deryk2",  "deryk3", "deryk_on_the_way"),
        SchedulePart("honeywood",             "17:49", "17:50", NORTH, WALKING,  "deryk3",  "deryk4", "deryk_on_the_way"),
        SchedulePart("honeywood",             "17:50", "17:52", EAST,  IMMOBILE, "deryk4",  "deryk4", "deryk_by_the_statue"),
        SchedulePart("honeywood",             "17:52", "17:53", NORTH, WALKING,  "deryk4",  "deryk5", "deryk_on_the_way"),
        SchedulePart("honeywood",             "17:53", "17:56", EAST,  WALKING,  "deryk5",  "deryk6", "deryk_on_the_way"),
        SchedulePart("honeywood",             "17:56", "17:59", NORTH, WALKING,  "deryk6",  "deryk7", "deryk_on_the_way"),
        SchedulePart("honeywood",             "17:59", "18:01", NORTH, WALKING,  "deryk7",  "deryk8"),

        SchedulePart("honeywood_house_elder", "18:01", "18:03", NORTH, WALKING,  "deryk9",  "deryk10"),
        SchedulePart("honeywood_house_elder", "18:03", "18:05", NORTH, WALKING,  "deryk10", "deryk11", "deryk_on_the_way"),
        SchedulePart("honeywood_house_elder", "18:05", "18:06", WEST,  WALKING,  "deryk11", "deryk12", "deryk_on_the_way"),
        SchedulePart("honeywood_house_elder", "18:06", "18:09", NORTH, WALKING,  "deryk12", "deryk13", "deryk_on_the_way"),
        SchedulePart("honeywood_house_elder", "18:09", "18:10", EAST,  WALKING,  "deryk13", "deryk14", "deryk_on_the_way"),
        SchedulePart("honeywood_house_elder", "18:10", "18:20", EAST,  IMMOBILE, "deryk14", "deryk14", "deryk_and_grahan_meet"),
        SchedulePart("honeywood_house_elder", "18:20", "18:21", WEST,  WALKING,  "deryk14", "deryk13", "deryk_on_the_way"),
        SchedulePart("honeywood_house_elder", "18:21", "18:24", SOUTH, WALKING,  "deryk13", "deryk12", "deryk_on_the_way"),
        SchedulePart("honeywood_house_elder", "18:24", "18:25", EAST,  WALKING,  "deryk12", "deryk11", "deryk_on_the_way"),
        SchedulePart("honeywood_house_elder", "18:25", "18:27", SOUTH, WALKING,  "deryk11", "deryk10", "deryk_on_the_way"),
        SchedulePart("honeywood_house_elder", "18:27", "18:29", SOUTH, WALKING,  "deryk10", "deryk9"),

        SchedulePart("honeywood",             "18:29", "18:31", SOUTH, WALKING,  "deryk8",  "deryk7"),
        SchedulePart("honeywood",             "18:31", "18:34", SOUTH, WALKING,  "deryk7",  "deryk6",  "deryk_on_the_way"),
        SchedulePart("honeywood",             "18:34", "18:40", WEST,  WALKING,  "deryk6",  "deryk15", "deryk_on_the_way"),
        SchedulePart("honeywood",             "18:40", "18:43", SOUTH, WALKING,  "deryk15", "deryk16", "deryk_on_the_way"),
        SchedulePart("honeywood",             "18:43", "18:45", WEST,  WALKING,  "deryk16", "deryk17", "deryk_on_the_way"),
        SchedulePart("honeywood",             "18:45", "18:46", NORTH, WALKING,  "deryk17", "deryk18"),

        SchedulePart("honeywood_inn",         "18:46", "18:48", NORTH, WALKING,  "deryk19", "deryk20"),
        SchedulePart("honeywood_inn",         "18:48", "18:50", NORTH, WALKING,  "deryk20", "deryk21", "deryk_one_moment"),
        SchedulePart("honeywood_inn",         "18:50", "19:00", NORTH, IMMOBILE, "deryk21", "deryk21", "deryk_checking_in"),
        SchedulePart("honeywood_inn",         "19:00", "19:07", WEST,  WALKING,  "deryk21", "deryk22", "deryk_one_moment"),
        SchedulePart("honeywood_inn",         "19:07", "19:10", NORTH, WALKING,  "deryk22", "deryk23", "deryk_one_moment"),
        SchedulePart("honeywood_inn",         "19:10", "19:12", WEST,  WALKING,  "deryk23", "deryk24", "deryk_one_moment"),
        SchedulePart("honeywood_inn",         "19:12", "19:15", NORTH, WALKING,  "deryk24", "deryk25", "deryk_one_moment"),
        SchedulePart("honeywood_inn",         "19:15", "20:00", NORTH, IDLE,     "deryk25", "deryk25", "quest_honeywood_swordmaster_school"),
        // @formatter:on
    )

    override fun handleSideEffects() {
        if (mapManager.currentMap.mapTitle == "honeywood") {
            doorsSchedule
                .filterKeys { gameData.clock.isCurrentTimeAt(it) }
                .values
                .singleOrNull()
                ?.let { worldScreen.useDoor(it) }
        }
    }

    private val doorsSchedule: Map<String, String> = mapOf(
        "18:00" to "door_honeywood_elder",
        "18:28" to "door_honeywood_elder",
        "18:45" to "door_honeywood_inn",
    )

}
