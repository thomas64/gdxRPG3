package nl.t64.cot.screens.world.schedule


class WorldSchedule {

    private var allSchedules: List<EntitySchedule> = listOf(Fabius())

    fun update() {
        allSchedules.forEach { it.update() }
    }

}
