package nl.t64.cot.components.schedule

import nl.t64.cot.screens.world.entity.Direction


data class MapSchedule(
    val fromMapName: String = "",
    val toMapName: String = "",
    val direction: Direction = Direction.NONE,
    val closingTime: String = "",
    val message: String = ""
)
