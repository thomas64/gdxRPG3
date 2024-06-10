package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.components.schedule.MapSchedule
import nl.t64.cot.components.schedule.MapScheduleDatabase
import nl.t64.cot.screens.world.mapobjects.GameMapRelocator


class WorldSchedule {

    private var entitySchedules: List<EntitySchedule> = listOf(HoneywoodResourceShop(),
                                                               HoneywoodEquipShop(),
                                                               Garrin(),
                                                               Ghost(),
                                                               Paton(),
                                                               Santino())

    fun update() {
        entitySchedules.forEach { it.update() }
        MapScheduleDatabase.getScheduleByMapName(mapManager.currentMap.mapTitle)?.update()
    }

    private fun MapSchedule.update() {
        if (gameData.clock.isCurrentTimeAfter(closingTime)) {
            worldScreen.showMessageDialog(message) {
                val autoPortal = GameMapRelocator.createAutoPortal(fromMapName, toMapName)
                mapManager.schedulePortal(autoPortal, direction)
            }
        }
    }

}
