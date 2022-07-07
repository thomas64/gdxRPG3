package nl.t64.cot.screens.world.schedule


class WorldSchedule {

    private var allSchedules: List<Schedule> = listOf(Fabius())

    fun update() {
        allSchedules.forEach { it.update() }
    }

}
