package nl.t64.cot.screens.world.schedule

import com.badlogic.gdx.utils.Timer
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.condition.isTrue
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.EntityState.*


class Santino : EntitySchedule() {

    override val entity = Entity("santino", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("santino"))
    override val invisibleTalking = Entity("santino", InputEmpty(), PhysicsScheduledNpc(), GraphicsEmpty())

    private var isBellRinging: Boolean = false
    private var timedBellTask: Timer.Task? = null


    override val scheduleParts: List<SchedulePart> =
        morningRoutine() +
            toTheMayorChurch() +
            toTheMayorOutside() +
            beingMurdered() +
            withTheMayorInside() +
            backToTheChurch() +
            toTheBooks()

    private fun morningRoutine() = listOf(
        // @formatter:off
        SchedulePart("lastdenn_church", "07:30", "09:00", EAST,  IDLE,     "santino1",  "santino1",  "santino_prepare"),
        SchedulePart("lastdenn_church", "09:00", "09:01", SOUTH, WALKING,  "santino1",  "santino2",  "santino_service_start"),
        SchedulePart("lastdenn_church", "09:01", "09:20", EAST,  WALKING,  "santino2",  "santino3",  "santino_service_start"),
        SchedulePart("lastdenn_church", "09:20", "09:45", NORTH, WALKING,  "santino3",  "santino4",  "santino_service_start"),
        SchedulePart("lastdenn_church", "09:45", "09:55", EAST,  WALKING,  "santino4",  "santino5",  "santino_service_start"),
        SchedulePart("lastdenn_church", "09:55", "10:00", SOUTH, IDLE,     "santino5",  "santino5",  "santino_service_start"),
        SchedulePart("lastdenn_church", "10:00", "11:00", SOUTH, IMMOBILE, "santino5",  "santino5",  "santino_service"),
        SchedulePart("lastdenn_church", "11:00", "11:02", WEST,  WALKING,  "santino5",  "santino4"),
        SchedulePart("lastdenn_church", "11:02", "12:00", SOUTH, IDLE,     "santino4",  "santino4",  "santino_counseling"),
        // @formatter:on
    )

    private fun toTheMayorChurch() = listOf(
        // @formatter:off
        SchedulePart("lastdenn_church", "12:00", "12:05", SOUTH, WALKING,  "santino4",  "santino3",  "santino_busy_errand"),
        SchedulePart("lastdenn_church", "12:05", "12:06", WEST,  WALKING,  "santino3",  "santino6",  "santino_busy_errand"),
        SchedulePart("lastdenn_church", "12:06", "12:07", NORTH, WALKING,  "santino6",  "santino7",  "santino_busy_errand"),
        SchedulePart("lastdenn_church", "12:07", "12:09", WEST,  WALKING,  "santino7",  "santino8",  "santino_busy_errand"),
        SchedulePart("lastdenn_church", "12:09", "12:11", NORTH, IDLE,     "santino8",  "santino8",  "santino_busy_errand"),
        SchedulePart("lastdenn_church", "12:11", "12:15", EAST,  WALKING,  "santino8",  "santino7",  "santino_busy_errand"),
        SchedulePart("lastdenn_church", "12:15", "12:18", SOUTH, WALKING,  "santino7",  "santino6",  "santino_busy_errand"),
        SchedulePart("lastdenn_church", "12:18", "12:20", EAST,  WALKING,  "santino6",  "santino3",  "santino_busy_errand"),
        SchedulePart("lastdenn_church", "12:20", "12:25", EAST,  WALKING,  "santino3",  "santino9",  "santino_busy_errand"),
        SchedulePart("lastdenn_church", "12:25", "12:30", SOUTH, WALKING,  "santino9",  "santino10", "santino_busy_errand"),
        // @formatter:on
    )

    private fun toTheMayorOutside() = listOf(
        // @formatter:off
        SchedulePart("lastdenn",        "12:30", "12:35", SOUTH, WALKING,  "santino11",       "santino12",       "santino_busy_errand"),
        SchedulePart("lastdenn",        "12:35", "12:45", WEST,  WALKING,  "santino12",       "santino13",       "santino_busy_errand"),
        SchedulePart("lastdenn",        "12:45", "13:00", NORTH, WALKING,  "santino13",       "santino14",       "santino_busy_errand"),
        SchedulePart("lastdenn",        "13:00", "13:20", NORTH, WALKING,  "santino14",       "entrance_guard5", "santino_busy_errand", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn",        "13:20", "13:30", EAST,  WALKING,  "entrance_guard5", "santino15",       "santino_busy_errand", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn",        "13:30", "13:50", NORTH, WALKING,  "santino15",       "santino16",       "santino_busy_errand", listOf("!is_garrin_possessed")),
        // @formatter:on
    )

    private fun beingMurdered() = listOf(
        // @formatter:off
        SchedulePart("lastdenn",        "13:00", "14:00", NORTH, IMMOBILE, "santino14", "santino14", "", listOf("is_garrin_possessed")),
        // @formatter:on
    )

    private fun withTheMayorInside() = listOf(
        // @formatter:off
        SchedulePart("lastdenn_house_mayor", "13:50", "14:05", NORTH, WALKING,  "santino17", "santino18", "", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn_house_mayor", "14:05", "14:15", NORTH, IMMOBILE, "santino18", "santino18", "", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn_house_mayor", "14:15", "14:20", EAST,  WALKING,  "santino18", "santino19", "", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn_house_mayor", "14:20", "14:25", NORTH, WALKING,  "santino19", "santino20", "", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn_house_mayor", "14:25", "14:27", EAST,  WALKING,  "santino20", "santino21", "", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn_house_mayor", "14:27", "14:37", EAST,  IMMOBILE, "santino21", "santino21", "", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn_house_mayor", "14:37", "14:39", WEST,  WALKING,  "santino21", "santino20", "", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn_house_mayor", "14:39", "14:44", SOUTH, WALKING,  "santino20", "santino19", "", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn_house_mayor", "14:44", "14:49", WEST,  WALKING,  "santino19", "santino18", "", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn_house_mayor", "14:49", "15:04", SOUTH, WALKING,  "santino18", "santino17", "", listOf("!is_garrin_possessed")),
        // @formatter:on
    )

    private fun backToTheChurch() = listOf(
        // @formatter:off
        SchedulePart("lastdenn",             "15:04", "15:24", SOUTH, WALKING,  "santino16",       "santino15",       "", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn",             "15:24", "15:34", WEST,  WALKING,  "santino15",       "entrance_guard5", "", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn",             "15:34", "15:50", SOUTH, WALKING,  "entrance_guard5", "possessed12",     "", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn",             "15:50", "15:53", EAST,  WALKING,  "possessed12",     "santino13a",      "", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn",             "15:53", "16:10", SOUTH, WALKING,  "santino13a",      "santino13",       "", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn",             "16:10", "16:20", EAST,  WALKING,  "santino13",       "santino12",       "", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn",             "16:20", "16:25", NORTH, WALKING,  "santino12",       "santino11",       "", listOf("!is_garrin_possessed")),
        // @formatter:on
    )

    private fun toTheBooks() = listOf(
        // @formatter:off
        SchedulePart("lastdenn_church",      "16:25", "16:30", NORTH, WALKING,  "santino10",       "santino9",        "", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn_church",      "16:30", "16:35", WEST,  WALKING,  "santino9",        "santino3",        "", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn_church",      "16:35", "16:37", WEST,  WALKING,  "santino3",        "santino6",        "", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn_church",      "16:37", "16:39", NORTH, WALKING,  "santino6",        "santino7",        "", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn_church",      "16:39", "16:43", WEST,  WALKING,  "santino7",        "santino11",       "", listOf("!is_garrin_possessed")),
        SchedulePart("lastdenn_church",      "16:43", "18:00", NORTH, IDLE,     "santino11",       "santino11",        "", listOf("!is_garrin_possessed")),
        // @formatter:on
    )

    override fun handleSideEffects() {
        handleTimedBells()

        if (mapManager.currentMap.mapTitle == "lastdenn_church") {
            if (gameData.clock.isCurrentTimeAt("09:59")) {
                worldScreen.onNotifyFadeAndReloadNpcs()
                Utils.runWithDelay(Constant.FADE_DURATION) { gameData.clock.setTimeOfDay("10:00") }
            }
            if (gameData.clock.isCurrentTimeInBetween("10:00", "11:00")) {
                val part = SchedulePart("lastdenn_church", "10:00", "11:00", NONE, INVISIBLE, "santino5a", "santino5a", "santino_service")
                setupInvisibleTalking(part)
            } else {
                removeInvisibleTalking()
            }
            if (gameData.clock.isCurrentTimeAt("10:59")) {
                worldScreen.onNotifyFadeAndReloadNpcs()
                Utils.runWithDelay(Constant.FADE_DURATION) { gameData.clock.setTimeOfDay("11:00") }
            }
        }
        if (mapManager.currentMap.mapTitle == "lastdenn") {
            if (gameData.clock.isCurrentTimeAt("12:29")) {
                worldScreen.useDoor("door_large_round")
            }
            if (gameData.clock.isCurrentTimeAt("13:49") && "!is_garrin_possessed".isTrue()) {
                worldScreen.useDoor("door_wooden_ring_shade_left")
            }
            if (gameData.clock.isCurrentTimeAt("15:03") && "!is_garrin_possessed".isTrue()) {
                worldScreen.useDoor("door_wooden_ring_shade_left")
            }
            if (gameData.clock.isCurrentTimeAt("16:24") && "!is_garrin_possessed".isTrue()) {
                worldScreen.useDoor("door_large_round")
            }
        }
    }

    private fun isIndoorBellWindow(): Boolean {
        return mapManager.currentMap.mapTitle == "lastdenn_church"
            && (gameData.clock.isCurrentTimeAt("09:56")
            || gameData.clock.isCurrentTimeAt("10:56"))
    }

    private fun isOutdoorBellWindow(): Boolean {
        return mapManager.currentMap.mapTitle == "lastdenn" &&
            gameData.clock.isCurrentTimeInBetween("09:45", "10:00")
    }

    private fun handleTimedBells() {
        if (isIndoorBellWindow()) {
            ringBellThrice()
        } else if (isOutdoorBellWindow()) {
            startTimedBells()
        } else {
            stopTimedBells()
        }
    }

    private fun startTimedBells() {
        if (timedBellTask != null) return

        timedBellTask = object : Timer.Task() {
            override fun run() {
                if (!isOutdoorBellWindow()) {
                    stopTimedBells()
                    return
                }
                playSe(AudioEvent.SE_BELL)
            }
        }
        Timer.schedule(timedBellTask, 0f, 2f)
    }

    private fun stopTimedBells() {
        timedBellTask?.cancel()
        timedBellTask = null
    }

    private fun ringBellThrice() {
        if (isBellRinging) return

        isBellRinging = true
        playSe(AudioEvent.SE_BELL)
        Timer.schedule(object : Timer.Task() {
            override fun run() {
                playSe(AudioEvent.SE_BELL)
            }
        }, 1.5f)
        Timer.schedule(object : Timer.Task() {
            override fun run() {
                playSe(AudioEvent.SE_BELL)
                isBellRinging = false
            }
        }, 3.0f)
    }

}
