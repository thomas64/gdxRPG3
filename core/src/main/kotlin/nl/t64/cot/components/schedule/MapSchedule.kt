package nl.t64.cot.components.schedule

import com.fasterxml.jackson.annotation.JsonProperty
import nl.t64.cot.screens.world.entity.Direction


data class MapSchedule(
    @JsonProperty("condition")
    val conditions: List<String> = emptyList(),
    val fromMapName: String = "",
    val toMapName: String = "",
    val direction: Direction = Direction.NONE,
    val closingTime: String = "",
    val message: String = ""
)
