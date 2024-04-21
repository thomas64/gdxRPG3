package nl.t64.cot.components.schedule

import nl.t64.cot.resources.ConfigDataLoader


object MapScheduleDatabase {

    private val schedules: Map<String, MapSchedule> = ConfigDataLoader.createSchedules()

    fun getScheduleByMapName(mapName: String): MapSchedule? = schedules[mapName]

}
