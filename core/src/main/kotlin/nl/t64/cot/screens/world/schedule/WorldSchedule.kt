package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import java.time.LocalTime


class WorldSchedule {

    private var allSchedules: List<Schedule> = listOf(Fabius())

    fun update() {
        allSchedules.forEach { it.update() }
        scheduleDoors()
    }

    private fun scheduleDoors() {
        val timeOfDay = gameData.clock.getTimeOfDay()
        if (mapManager.currentMap.mapTitle == "honeywood"
            && timeOfDay == LocalTime.of(7, 49)
        ) {
            brokerManager.entityObservers.notifyUseDoor("door_simple_left1")
        }
    }

}
