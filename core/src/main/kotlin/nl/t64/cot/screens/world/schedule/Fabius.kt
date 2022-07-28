package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.EntityState.IDLE
import nl.t64.cot.screens.world.entity.EntityState.WALKING
import nl.t64.cot.screens.world.entity.GraphicsScheduledNpc
import nl.t64.cot.screens.world.entity.InputEmpty
import nl.t64.cot.screens.world.entity.PhysicsScheduledNpc


class Fabius : EntitySchedule() {

    override val entity = Entity("man01", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("man01"))

    override fun getScheduleOfEntity(): List<ScheduleItem> {
        return stareAroundBelowInn() +
                walkToInnWithDetour() +
                walkUpToCounterAndWait()
    }

    private fun stareAroundBelowInn(): List<ScheduleItem> {
        return listOf(
            // @formatter:off
            ScheduleItem("honeywood", "07:30", "07:40", NORTH, IDLE, "fabius1", "fabius1", "default"),
            ScheduleItem("honeywood", "07:40", "07:41", SOUTH, IDLE, "fabius1", "fabius1", "default"),
            // @formatter:on
        )
    }

    private fun walkToInnWithDetour(): List<ScheduleItem> {
        return listOf(
            // @formatter:off
            ScheduleItem("honeywood", "07:41", "07:42", SOUTH, WALKING, "fabius1", "fabius2", "default"),
            ScheduleItem("honeywood", "07:42", "07:43", EAST,  WALKING, "fabius2", "fabius3", "default"),
            ScheduleItem("honeywood", "07:43", "07:44", WEST,  WALKING, "fabius3", "fabius4", "default"),
            ScheduleItem("honeywood", "07:44", "07:50", NORTH, WALKING, "fabius4", "fabius5"),
            // @formatter:on
        )
    }

    private fun walkUpToCounterAndWait(): List<ScheduleItem> {
        return listOf(
            // @formatter:off
            ScheduleItem("honeywood_inn", "07:50", "07:53", NORTH, WALKING, "fabius6", "fabius7", "default"),
            ScheduleItem("honeywood_inn", "07:53", "08:00", NORTH, IDLE,    "fabius7", "fabius7", "default"),
            // @formatter:on
        )
    }

    override fun handleSideEffects() {
        if (mapManager.currentMap.mapTitle == "honeywood") {
            if (gameData.clock.isCurrentTimeAt("07:49")) {
                brokerManager.entityObservers.notifyUseDoor("door_simple_left1")
            }
        }
    }

}
