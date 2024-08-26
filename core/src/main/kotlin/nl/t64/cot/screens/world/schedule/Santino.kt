package nl.t64.cot.screens.world.schedule

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.screens.world.entity.Direction.*
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.EntityState.IDLE
import nl.t64.cot.screens.world.entity.EntityState.WALKING
import nl.t64.cot.screens.world.entity.GraphicsScheduledNpc
import nl.t64.cot.screens.world.entity.InputEmpty
import nl.t64.cot.screens.world.entity.PhysicsScheduledNpc


class Santino : EntitySchedule() {

    override val entity = Entity("santino", InputEmpty(), PhysicsScheduledNpc(), GraphicsScheduledNpc("santino"))

    override val scheduleParts: List<SchedulePart> =
        morningRoutine() +
            toTheMayorChurch() +
            toTheMayorOutside() +
            beingMurdered()

    private fun morningRoutine() = listOf(
        // @formatter:off
        SchedulePart("lastdenn_church", "07:30", "09:00", EAST,  IDLE,    "santino1", "santino1", "santino_prepare"),
        SchedulePart("lastdenn_church", "09:00", "09:01", SOUTH, WALKING, "santino1", "santino2", "santino_service_start"),
        SchedulePart("lastdenn_church", "09:01", "09:20", EAST,  WALKING, "santino2", "santino3", "santino_service_start"),
        SchedulePart("lastdenn_church", "09:20", "09:45", NORTH, WALKING, "santino3", "santino4", "santino_service_start"),
        SchedulePart("lastdenn_church", "09:45", "09:55", EAST,  WALKING, "santino4", "santino5", "santino_service_start"),
        SchedulePart("lastdenn_church", "09:55", "10:00", SOUTH, IDLE,    "santino5", "santino5", "santino_service_start"),
        SchedulePart("lastdenn_church", "10:00", "11:00", SOUTH, IDLE,    "santino5", "santino5", "santino_first_service"),
        SchedulePart("lastdenn_church", "11:00", "11:02", WEST,  WALKING, "santino5", "santino4"),
        SchedulePart("lastdenn_church", "11:02", "12:00", SOUTH, IDLE,    "santino4", "santino4", "santino_first_counseling"),
        // @formatter:on
    )

    private fun toTheMayorChurch() = listOf(
        // @formatter:off
        SchedulePart("lastdenn_church", "12:00", "12:05", SOUTH, WALKING, "santino4", "santino3",  "santino_busy_errand"),
        SchedulePart("lastdenn_church", "12:05", "12:06", WEST,  WALKING, "santino3", "santino6",  "santino_busy_errand"),
        SchedulePart("lastdenn_church", "12:06", "12:07", NORTH, WALKING, "santino6", "santino7",  "santino_busy_errand"),
        SchedulePart("lastdenn_church", "12:07", "12:09", WEST,  WALKING, "santino7", "santino8",  "santino_busy_errand"),
        SchedulePart("lastdenn_church", "12:09", "12:11", NORTH, IDLE,    "santino8", "santino8",  "santino_busy_errand"),
        SchedulePart("lastdenn_church", "12:11", "12:13", EAST,  WALKING, "santino8", "santino7",  "santino_busy_errand"),
        SchedulePart("lastdenn_church", "12:13", "12:14", SOUTH, WALKING, "santino7", "santino6",  "santino_busy_errand"),
        SchedulePart("lastdenn_church", "12:14", "12:15", EAST,  WALKING, "santino6", "santino3",  "santino_busy_errand"),
        SchedulePart("lastdenn_church", "12:15", "12:17", EAST,  WALKING, "santino3", "santino9",  "santino_busy_errand"),
        SchedulePart("lastdenn_church", "12:17", "12:20", SOUTH, WALKING, "santino9", "santino10", "santino_busy_errand"),
        // @formatter:on
    )

    private fun toTheMayorOutside() = listOf(
        // @formatter:off
        SchedulePart("lastdenn", "12:20", "12:25", SOUTH, WALKING, "santino11", "santino12", "santino_busy_errand"),
        SchedulePart("lastdenn", "12:25", "12:35", WEST,  WALKING, "santino12", "santino13", "santino_busy_errand"),
        SchedulePart("lastdenn", "12:35", "13:00", NORTH, WALKING, "santino13", "santino14", "santino_busy_errand"),
        // @formatter:on
    )

    private fun beingMurdered() = listOf(
        // @formatter:off
        SchedulePart("lastdenn", "13:00", "14:00", EAST,  IDLE,    "santino14", "santino14"),
        // @formatter:on
    )

    override fun handleSideEffects() {
        if (mapManager.currentMap.mapTitle == "lastdenn_church") {
            if (gameData.clock.isCurrentTimeAt("10:00")) {
                worldScreen.justFadeAndReloadNpcs()
            }
            if (gameData.clock.isCurrentTimeAt("11:00")) {
                worldScreen.justFadeAndReloadNpcs()
            }
        }
        if (mapManager.currentMap.mapTitle == "lastdenn") {
            if (gameData.clock.isCurrentTimeAt("12:19")) {
                worldScreen.useDoor("door_large_round")
            }
        }
    }

}
