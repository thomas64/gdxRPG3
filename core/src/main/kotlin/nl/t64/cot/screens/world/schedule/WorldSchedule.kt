package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.components.condition.areAllTrue
import nl.t64.cot.components.schedule.MapSchedule
import nl.t64.cot.components.schedule.MapScheduleDatabase
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.mapobjects.GameMapPortal


class WorldSchedule {

    private var entitySchedules: List<EntitySchedule> = listOf(Lennor(),
                                                               BlackSmith(),
                                                               HoneywoodResourceShop(),
                                                               HoneywoodEquipShop(),
                                                               HoneywoodAcademy1(),
                                                               HoneywoodAcademy2(),
                                                               Deryk(),
                                                               AlvaTraveler(),
                                                               LastdennEntranceGuard(),
                                                               Garrin(),
                                                               Ghost(),
                                                               Paton(),
                                                               Santino(),
                                                               LastdennEscordGuard1(),
                                                               LastdennEscordGuard2())

    fun update() {
        entitySchedules.forEach { it.update() }
        MapScheduleDatabase.getScheduleByMapName(mapManager.currentMap.mapTitle)?.update()
        showFinalWarningCycle4()
    }

    private fun MapSchedule.update() {
        if (conditions.areAllTrue()
            && gameData.clock.isCurrentTimeAfter(closingTime)
        ) {
            worldScreen.showMessageDialog(message) {
                val autoPortal = GameMapPortal(fromMapName, toMapName)
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
