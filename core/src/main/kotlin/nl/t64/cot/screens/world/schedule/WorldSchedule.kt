package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.components.condition.areAllTrue
import nl.t64.cot.components.schedule.MapSchedule
import nl.t64.cot.components.schedule.MapScheduleDatabase
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
                                                               AlvaFerry(),
                                                               AlvaFerryBandit(),
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
        possibleShowFinalWarning()
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

    private fun possibleShowFinalWarning() {
        gameData.events.getEventById("event_almost_1930").possibleStart()
    }

}
