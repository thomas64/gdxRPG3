package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.mapManager
import nl.t64.cot.components.schedule.MapScheduleDatabase


class WorldSchedule {

    private var entitySchedules: List<EntitySchedule> = listOf(Santino())

    fun update() {
        entitySchedules.forEach { it.update() }
        MapScheduleDatabase.getScheduleByMapName(mapManager.currentMap.mapTitle)?.update()
    }

}
