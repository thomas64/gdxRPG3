package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.EntityState.IMMOBILE
import nl.t64.cot.screens.world.entity.EntityState.WALKING
import nl.t64.cot.screens.world.entity.GraphicsScheduledNpc
import nl.t64.cot.screens.world.entity.InputEmpty
import nl.t64.cot.screens.world.entity.PhysicsScheduledNpc


class BlackSmith : EntitySchedule() {

    override val entity = Entity("man07", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("man07"))

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("honeywood", "07:34", "07:35", SOUTH, WALKING,  "smith1", "smith2", "black_smith_on_the_way"),
        SchedulePart("honeywood", "07:35", "07:37", SOUTH, WALKING,  "smith2", "smith3", "black_smith_on_the_way"),
        SchedulePart("honeywood", "07:37", "07:47", SOUTH, IMMOBILE, "smith3", "smith3", "black_and_lennor_meet"),
        SchedulePart("honeywood", "07:47", "07:52", SOUTH, WALKING,  "smith3", "smith4", "black_smith_on_the_way"),
        SchedulePart("honeywood", "07:52", "07:54", EAST,  WALKING,  "smith4", "smith5", "black_smith_on_the_way"),
        SchedulePart("honeywood", "07:54", "07:55", EAST,  WALKING,  "smith5", "smith6", "black_smith_on_the_way"),
        SchedulePart("honeywood", "07:55", "07:57", NORTH, WALKING,  "smith6", "smith7", "black_smith_on_the_way"),
        // @formatter:on
    )

    override fun handleSideEffects() {
        if (mapManager.currentMap.mapTitle == "honeywood") {
            if (gameData.clock.isCurrentTimeAt("07:33")) {
                worldScreen.useDoor("door_honeywood_smith")
            }
            if (gameData.clock.isCurrentTimeAt("07:56")) {
                worldScreen.useDoor("door_honeywood_elder")
            }
        }
    }

}
