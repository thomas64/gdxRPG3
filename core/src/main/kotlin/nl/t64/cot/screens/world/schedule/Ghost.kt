package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.EntityState.CRAWLING


class Ghost : EntitySchedule() {

    override val entity = Entity("ghost1", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("ghost1"))
    override val invisibleTalking = Entity("", InputEmpty(), PhysicsScheduledNpc(), GraphicsEmpty())

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("lastdenn_house_garrin", "09:00", "09:07", EAST,  CRAWLING, "ghost1",  "ghost2",  "ghost_booing"),
        SchedulePart("lastdenn_house_garrin", "09:07", "09:16", NORTH, CRAWLING, "ghost2",  "ghost3",  "paton_hiding_behind_garrin"),
        SchedulePart("lastdenn_house_garrin", "09:16", "09:20", EAST,  CRAWLING, "ghost3",  "ghost4",  "paton_hiding_behind_garrin"),
        SchedulePart("lastdenn_house_garrin", "09:20", "09:28", SOUTH, CRAWLING, "ghost4",  "paton4",  "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:28", "09:35", EAST,  CRAWLING, "paton4",  "paton5",  "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:35", "09:43", NORTH, CRAWLING, "paton5",  "garrin4", "ghost_scaring"),
        SchedulePart("lastdenn_house_garrin", "09:43", "09:54", WEST,  CRAWLING, "garrin4", "garrin6", "ghost_scaring"),
        // @formatter:on
    )

    override fun handleSideEffects() {
        if (mapManager.currentMap.mapTitle == "lastdenn_house_garrin") {
            with(gameData.clock) {
                when {
                    isCurrentTimeInBetween("08:58", "09:00") ->
                        audioManager.fadeBgmAndPlayBgm(AudioEvent.BGM_HOUSE, AudioEvent.BGM_GHOST)
                    isCurrentTimeInBetween("09:00", "11:25") ->
                        audioManager.stopBgmAndPlayBgm(AudioEvent.BGM_HOUSE, AudioEvent.BGM_GHOST)
                    isCurrentTimeAfter("11:25") ->
                        audioManager.possibleBgmFade(AudioEvent.BGM_GHOST)
                }
            }
        }
    }

}
