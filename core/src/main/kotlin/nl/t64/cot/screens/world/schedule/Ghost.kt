package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.EntityState.CRAWLING
import nl.t64.cot.screens.world.entity.GraphicsScheduledNpc
import nl.t64.cot.screens.world.entity.InputEmpty
import nl.t64.cot.screens.world.entity.PhysicsScheduledNpc


class Ghost : EntitySchedule() {

    override val entity = Entity("ghost1", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("ghost1"))

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("lastdenn_house_garrin", "09:00", "09:07", EAST,  CRAWLING, "ghost1", "ghost2", "ghost_booing"),
        SchedulePart("lastdenn_house_garrin", "09:07", "09:16", NORTH, CRAWLING, "ghost2", "ghost3", "paton_hiding_behind_garrin"),
        SchedulePart("lastdenn_house_garrin", "09:16", "09:20", EAST,  CRAWLING, "ghost3", "ghost4", "paton_hiding_behind_garrin"),
        SchedulePart("lastdenn_house_garrin", "09:20", "09:28", SOUTH, CRAWLING, "ghost4", "ghost5", "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:28", "09:35", EAST,  CRAWLING, "ghost5", "ghost6", "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:35", "09:43", NORTH, CRAWLING, "ghost6", "ghost7", "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:43", "09:50", WEST,  CRAWLING, "ghost7", "ghost4", "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:50", "09:58", WEST,  CRAWLING, "ghost4", "ghost8", "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:58", "10:01", WEST,  CRAWLING, "ghost8", "ghost9", "ghost_scaring"),
        // @formatter:on
    )

    override fun handleSideEffects() {
        if (mapManager.currentMap.mapTitle == "lastdenn_house_garrin") {
            with(gameData.clock) {
                when {
                    isCurrentTimeInBetween("08:58", "09:00") ->
                        audioManager.fadeBgmAndPlayBgm(AudioEvent.BGM_HOUSE, AudioEvent.BGM_GHOST)
                    isCurrentTimeInBetween("09:00", "10:02") ->
                        audioManager.stopBgmAndPlayBgm(AudioEvent.BGM_HOUSE, AudioEvent.BGM_GHOST)
                    isCurrentTimeAfter("10:02") ->
                        audioManager.possibleBgmFade(AudioEvent.BGM_GHOST)
                }
            }
        }
    }

}
