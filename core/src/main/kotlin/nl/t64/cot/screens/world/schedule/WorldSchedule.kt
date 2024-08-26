package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.components.schedule.MapSchedule
import nl.t64.cot.components.schedule.MapScheduleDatabase
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.mapobjects.GameMapRelocator


class WorldSchedule {

    private var entitySchedules: List<EntitySchedule> = listOf(Lennor(),
                                                               BlackSmith(),
                                                               HoneywoodResourceShop(),
                                                               HoneywoodEquipShop(),
                                                               HoneywoodAcademy1(),
                                                               HoneywoodAcademy2(),
                                                               Garrin(),
                                                               Ghost(),
                                                               Paton(),
                                                               Santino())

    fun update() {
        entitySchedules.forEach { it.update() }
        MapScheduleDatabase.getScheduleByMapName(mapManager.currentMap.mapTitle)?.update()
        showFinalWarningCycle4()
    }

    private fun MapSchedule.update() {
        if (gameData.clock.isCurrentTimeAfter(closingTime)) {
            worldScreen.showMessageDialog(message) {
                val autoPortal = GameMapRelocator.createAutoPortal(fromMapName, toMapName)
                mapManager.schedulePortal(autoPortal, direction)
            }
        }
    }

    private fun showFinalWarningCycle4() {
        if (gameData.numberOfCycles == 4 && gameData.clock.isCurrentTimeAt("18:59")) {
            gameData.clock.setTimeOfDay("19:00")
            worldScreen.showConversationDialogFromEvent("almost_1930", Constant.PLAYER_ID)
        }
    }

}
