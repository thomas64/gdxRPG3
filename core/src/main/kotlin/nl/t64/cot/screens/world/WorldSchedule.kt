package nl.t64.cot.screens.world

import nl.t64.cot.screens.world.schedule.Pietje1
import nl.t64.cot.screens.world.schedule.Schedule


class WorldSchedule {

    private var allSchedules: List<Schedule> = listOf(Pietje1())

    fun update() {
        allSchedules.forEach { it.update() }
    }

}
