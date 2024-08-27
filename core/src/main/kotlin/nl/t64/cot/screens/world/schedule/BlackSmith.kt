package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.EntityState.IMMOBILE
import nl.t64.cot.screens.world.entity.EntityState.WALKING


class BlackSmith : EntitySchedule() {

    override val entity = Entity("man07", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("man07"))
    override val invisibleTalking = Entity("", InputEmpty(), PhysicsScheduledNpc(), GraphicsEmpty())

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("honeywood",             "07:34", "07:35", SOUTH, WALKING,  "smith1", "smith2"),
        SchedulePart("honeywood",             "07:35", "07:37", SOUTH, WALKING,  "smith2", "smith3",   "black_smith_on_the_way"),
        SchedulePart("honeywood",             "07:37", "07:47", SOUTH, IMMOBILE, "smith3", "smith3",   "black_and_lennor_meet"),
        SchedulePart("honeywood",             "07:47", "07:52", SOUTH, WALKING,  "smith3", "smith4",   "black_smith_on_the_way"),
        SchedulePart("honeywood",             "07:52", "07:54", EAST,  WALKING,  "smith4", "smith5",   "black_smith_on_the_way"),
        SchedulePart("honeywood",             "07:54", "07:55", EAST,  WALKING,  "smith5", "smith6",   "black_smith_on_the_way"),
        SchedulePart("honeywood",             "07:55", "07:57", NORTH, WALKING,  "smith6", "smith7"),

        SchedulePart("honeywood_house_elder", "07:57", "07:59", NORTH, WALKING,  "smith8",  "smith9"),
        SchedulePart("honeywood_house_elder", "07:59", "08:03", EAST,  WALKING,  "smith9",  "smith10", "black_smith_on_the_job"),
        SchedulePart("honeywood_house_elder", "08:03", "08:05", NORTH, WALKING,  "smith10", "smith11", "black_smith_on_the_job"),
        SchedulePart("honeywood_house_elder", "08:05", "08:07", WEST,  WALKING,  "smith11", "smith12", "black_smith_on_the_job"),
        SchedulePart("honeywood_house_elder", "08:07", "08:17", WEST,  IMMOBILE, "smith12", "smith12", "black_and_grahan_meet"),
        SchedulePart("honeywood_house_elder", "08:17", "08:19", EAST,  WALKING,  "smith12", "smith11", "black_smith_on_the_job"),
        SchedulePart("honeywood_house_elder", "08:19", "08:21", EAST,  WALKING,  "smith11", "smith13", "black_smith_on_the_job"),
        SchedulePart("honeywood_house_elder", "08:21", "08:23", EAST,  WALKING,  "smith13", "smith14", "black_smith_on_the_job"),
        SchedulePart("honeywood_house_elder", "08:23", "08:25", NORTH, WALKING,  "smith14", "smith15", "black_smith_on_the_job"),
        SchedulePart("honeywood_house_elder", "08:25", "08:31", WEST,  WALKING,  "smith15", "smith16", "black_smith_on_the_job"),
        SchedulePart("honeywood_house_elder", "08:31", "08:41", NORTH, IMMOBILE, "smith16", "smith16", "black_and_elenora_meet"),
        SchedulePart("honeywood_house_elder", "08:41", "10:00", NORTH, IMMOBILE, "smith16", "smith16", "black_smith_on_the_job"),
        SchedulePart("honeywood_house_elder", "10:00", "10:10", NORTH, IMMOBILE, "smith16", "smith16", "black_smith_is_done"),

        SchedulePart("honeywood_house_elder", "10:10", "10:16", EAST,  WALKING,  "smith16", "smith15", "black_smith_on_the_job"),
        SchedulePart("honeywood_house_elder", "10:16", "10:18", SOUTH, WALKING,  "smith15", "smith14", "black_smith_on_the_job"),
        SchedulePart("honeywood_house_elder", "10:18", "10:20", WEST,  WALKING,  "smith14", "smith13", "black_smith_on_the_job"),
        SchedulePart("honeywood_house_elder", "10:20", "10:22", WEST,  WALKING,  "smith13", "smith11", "black_smith_on_the_job"),
        SchedulePart("honeywood_house_elder", "10:22", "10:24", WEST,  WALKING,  "smith11", "smith12", "black_smith_on_the_job"),
        SchedulePart("honeywood_house_elder", "10:24", "10:34", WEST,  IMMOBILE, "smith12", "smith12", "black_smith_is_leaving"),
        SchedulePart("honeywood_house_elder", "10:34", "10:36", EAST,  WALKING,  "smith12", "smith11", "black_smith_on_the_way_back"),
        SchedulePart("honeywood_house_elder", "10:36", "10:38", SOUTH, WALKING,  "smith11", "smith10", "black_smith_on_the_way_back"),
        SchedulePart("honeywood_house_elder", "10:38", "10:42", WEST,  WALKING,  "smith10", "smith9",  "black_smith_on_the_way_back"),
        SchedulePart("honeywood_house_elder", "10:42", "10:44", SOUTH, WALKING,  "smith9",  "smith8"),

        SchedulePart("honeywood",             "10:44", "10:46", SOUTH, WALKING,  "smith7",  "smith6"),
        SchedulePart("honeywood",             "10:46", "10:47", WEST,  WALKING,  "smith6",  "smith5",  "black_smith_on_the_way_back"),
        SchedulePart("honeywood",             "10:47", "10:49", WEST,  WALKING,  "smith5",  "smith4",  "black_smith_on_the_way_back"),
        SchedulePart("honeywood",             "10:49", "10:54", NORTH, WALKING,  "smith4",  "smith3",  "black_smith_on_the_way_back"),
        SchedulePart("honeywood",             "10:54", "10:56", NORTH, WALKING,  "smith3",  "smith2",  "black_smith_on_the_way_back"),
        SchedulePart("honeywood",             "10:56", "10:57", NORTH, WALKING,  "smith2",  "smith1"),
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
        "07:33" to "door_honeywood_smith",
        "07:56" to "door_honeywood_elder",
        "10:43" to "door_honeywood_elder",
        "10:56" to "door_honeywood_smith"
    )

}
