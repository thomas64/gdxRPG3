package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.components.condition.areAllTrue


class DoorSchedulePart(
    private val mapTitle: String,
    private val startTime: String,
    private val endTime: String,
    private val doorId: String,
    private val conditions: List<String> = emptyList()
) {

    fun isCurrentlyActiveFor(requestedDoorId: String): Boolean {
        return doorId == requestedDoorId
            && isMeetingConditions()
            && isCurrentMapInState()
            && isCurrentTimeInState()
    }

    private fun isMeetingConditions(): Boolean {
        return conditions.areAllTrue()
    }

    private fun isCurrentMapInState(): Boolean {
        val currentMap: String = mapManager.currentMap.mapTitle
        return currentMap == mapTitle
    }

    private fun isCurrentTimeInState(): Boolean {
        return gameData.clock.isCurrentTimeInBetween(startTime, endTime)
    }

}
