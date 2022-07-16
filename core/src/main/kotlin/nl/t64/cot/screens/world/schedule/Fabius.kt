package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.components.time.GameTime
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
            ScheduleItem("honeywood",      SOUTH,  IDLE,       7, 30,    7, 40,    550, 350,    550, 350,  "default"),
            ScheduleItem("honeywood",      EAST,   IDLE,       7, 40,    7, 41,    550, 350,    550, 350,  "default"),
            // @formatter:on
        )
    }

    private fun walkToInnWithDetour(): List<ScheduleItem> {
        return listOf(
            // @formatter:off
            ScheduleItem("honeywood",      EAST,   WALKING,    7, 41,    7, 42,    550, 350,    700, 350,  "default"),
            ScheduleItem("honeywood",      WEST,   WALKING,    7, 42,    7, 43,    700, 350,    600, 350,  "default"),
            ScheduleItem("honeywood",      NORTH,  WALKING,    7, 43,    7, 50,    600, 350,    624, 484),
            // @formatter:on
        )
    }

    private fun walkUpToCounterAndWait(): List<ScheduleItem> {
        return listOf(
            // @formatter:off
            ScheduleItem("honeywood_inn",  NORTH,  WALKING,    7, 50,    7, 53,    620,  40,    620, 250,  "default"),
            ScheduleItem("honeywood_inn",  NORTH,  IDLE,       7, 53,    8,  0,    620, 250,    620, 250,  "default"),
            // @formatter:on
        )
    }

    override fun handleSideEffects() {
        val timeOfDay = gameData.clock.getTimeOfDay()
        if (mapManager.currentMap.mapTitle == "honeywood") {
            if (timeOfDay == GameTime.of(7, 49)) {
                brokerManager.entityObservers.notifyUseDoor("door_simple_left1")
            }
        }
    }

}
