package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.EntityState.IDLE
import nl.t64.cot.screens.world.entity.EntityState.WALKING
import nl.t64.cot.screens.world.entity.GraphicsNpc
import nl.t64.cot.screens.world.entity.InputScheduledNpc
import nl.t64.cot.screens.world.entity.PhysicsScheduledNpc
import java.time.LocalTime


class Fabius : Schedule() {

    override val entity = Entity("fabius", InputScheduledNpc(), PhysicsScheduledNpc(), GraphicsNpc("man01"))

    override fun getSchedule(): List<ScheduleState> {
        return stareAroundBelowInn() +
                walkToInnWithDetour() +
                walkUpToCounterAndWait()
    }

    private fun stareAroundBelowInn(): List<ScheduleState> {
        return listOf(
            // @formatter:off
            ScheduleState("honeywood",      SOUTH,  IDLE,       7, 30,    7, 40,    550, 350,    550, 350),
            ScheduleState("honeywood",      EAST,   IDLE,       7, 40,    7, 41,    550, 350,    550, 350),
            // @formatter:on
        )
    }

    private fun walkToInnWithDetour(): List<ScheduleState> {
        return listOf(
            // @formatter:off
            ScheduleState("honeywood",      EAST,   WALKING,    7, 41,    7, 42,    550, 350,    700, 350),
            ScheduleState("honeywood",      WEST,   WALKING,    7, 42,    7, 43,    700, 350,    600, 350),
            ScheduleState("honeywood",      NORTH,  WALKING,    7, 43,    7, 50,    600, 350,    624, 484),
            // @formatter:on
        )
    }

    private fun walkUpToCounterAndWait(): List<ScheduleState> {
        return listOf(
            // @formatter:off
            ScheduleState("honeywood_inn",  NORTH,  WALKING,    7, 50,    7, 53,    620,  40,    620, 250),
            ScheduleState("honeywood_inn",  NORTH,  IDLE,       7, 53,    9, 55,    620, 250,    620, 250),
            // @formatter:on
        )
    }

    override fun handleSideEffects() {
        val timeOfDay = gameData.clock.getTimeOfDay()
        if (mapManager.currentMap.mapTitle == "honeywood"
            && timeOfDay == LocalTime.of(7, 49)
        ) {
            brokerManager.entityObservers.notifyUseDoor("door_simple_left1")
        }
    }

}
