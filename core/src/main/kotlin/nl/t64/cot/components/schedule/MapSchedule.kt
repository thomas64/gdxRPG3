package nl.t64.cot.components.schedule

import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.mapobjects.GameMapRelocator


data class MapSchedule(
    private val fromMapName: String = "",
    private val toMapName: String = "",
    private val direction: Direction = Direction.NONE,
    private val openStartTime: String = "",
    private val openEndTime: String = "",
    private val message: String = ""
) {

    fun update() {
        if (!gameData.clock.isCurrentTimeInBetween(openStartTime, openEndTime)) {
            brokerManager.componentObservers.notifyShowMessageDialog(message) {
                val autoPortal = GameMapRelocator.createAutoPortal(fromMapName, toMapName)
                mapManager.schedulePortal(autoPortal, direction)
            }
        }
    }

}
