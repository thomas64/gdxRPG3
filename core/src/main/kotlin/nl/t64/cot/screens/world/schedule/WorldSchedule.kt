package nl.t64.cot.screens.world.schedule


class WorldSchedule {

    private var allSchedules: List<Schedule> = listOf(Pietje1())

    fun update() {
        allSchedules.forEach { it.update() }
    }

}
