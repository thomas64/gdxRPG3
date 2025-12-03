package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.EntityState.CRAWLING


class Ghost : EntitySchedule() {

    override val entity = Entity("ghost1", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("ghost1"))
    override val invisibleTalking = Entity("", InputEmpty(), PhysicsScheduledNpc(), GraphicsEmpty())
    private var isScreenShaking: Boolean = false

    override val scheduleParts: List<SchedulePart> = listOf(
        // @formatter:off
        SchedulePart("lastdenn_house_garrin", "09:00", "09:07", EAST,  CRAWLING, "ghost1",  "ghost2",  "ghost_booing"),
        SchedulePart("lastdenn_house_garrin", "09:07", "09:16", NORTH, CRAWLING, "ghost2",  "ghost3",  "ghost_booing"),
        SchedulePart("lastdenn_house_garrin", "09:16", "09:20", EAST,  CRAWLING, "ghost3",  "ghost4",  "ghost_booing"),
        SchedulePart("lastdenn_house_garrin", "09:20", "09:28", SOUTH, CRAWLING, "ghost4",  "paton4",  "ghost_booing"),
        SchedulePart("lastdenn_house_garrin", "09:28", "09:35", EAST,  CRAWLING, "paton4",  "paton5",  "ghost_booing"),
        SchedulePart("lastdenn_house_garrin", "09:35", "09:43", NORTH, CRAWLING, "paton5",  "garrin4", "ghost_booing"),
        SchedulePart("lastdenn_house_garrin", "09:43", "09:54", WEST,  CRAWLING, "garrin4", "garrin6", "ghost_booing"),
        // @formatter:on
    )

    override fun handleSideEffects() {
        if (mapManager.currentMap.mapTitle == "lastdenn_house_garrin") {
            with(gameData.clock) {
                if (!isCurrentTimeAt("09:54")) {
                    isScreenShaking = false
                }
                when {
                    isCurrentTimeInBetween("08:58", "09:00") ->
                        audioManager.fadeBgmAndPlayBgm(AudioEvent.BGM_HOUSE, AudioEvent.BGM_GHOST)
                    isCurrentTimeAt("09:54") -> {
                        if (!isScreenShaking) {
                            isScreenShaking = true
                            playSe(AudioEvent.SE_MAGIC_BANG)
                            worldScreen.shakeCamera()
                            worldScreen.fadeOut(duration = 0.01f)
                        }
                    }
                    isCurrentTimeInBetween("09:00", "10:22") ->
                        audioManager.stopBgmAndPlayBgm(AudioEvent.BGM_HOUSE, AudioEvent.BGM_GHOST)
                    isCurrentTimeAfter("10:22") ->
                        audioManager.possibleBgmFade(AudioEvent.BGM_GHOST)
                }
            }
        }
    }

}
