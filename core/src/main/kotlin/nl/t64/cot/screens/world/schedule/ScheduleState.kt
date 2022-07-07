package nl.t64.cot.screens.world.schedule

import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState
import java.time.Duration
import java.time.LocalTime
import kotlin.math.abs


class ScheduleState(
    private val mapTitle: String,
    val direction: Direction,
    val state: EntityState,
    startTimeHours: Int,
    startTimeMinutes: Int,
    endTimeHours: Int,
    endTimeMinutes: Int,
    startPositionX: Int,
    startPositionY: Int,
    endPositionX: Int,
    endPositionY: Int,
) {
    private val startTime: LocalTime = LocalTime.of(startTimeHours, startTimeMinutes)
    private val endTime: LocalTime = LocalTime.of(endTimeHours, endTimeMinutes)
    private val startPosition: Vector2 = Vector2(startPositionX.toFloat(), startPositionY.toFloat())
    private val endPosition: Vector2 = Vector2(endPositionX.toFloat(), endPositionY.toFloat())

    fun isCurrentMapInState(): Boolean {
        val currentMap: String = mapManager.currentMap.mapTitle
        return currentMap == mapTitle
    }

    fun isCurrentTimeInState(): Boolean {
        val currentTime: LocalTime = gameData.clock.getTimeOfDay()
        return (currentTime == startTime || currentTime.isAfter(startTime)) && currentTime.isBefore(endTime)
    }

    fun getCurrentPosition(): Vector2 {
        val currentTime: LocalTime = gameData.clock.getTimeOfDay()
        val percentage: Long = getPercentage(currentTime)
        val x: Float = getPositionX(percentage)
        val y: Float = getPositionY(percentage)
        return Vector2(x, y)
    }

    private fun getPercentage(currentTime: LocalTime): Long {
        return Duration.between(startTime, currentTime)
            .multipliedBy(100)
            .dividedBy(Duration.between(startTime, endTime))
    }

    private fun getPositionX(percentage: Long): Float {
        return if (endPosition.x > startPosition.x) {
            startPosition.x + ((abs(endPosition.x - startPosition.x) / 100f) * percentage)
        } else {
            startPosition.x - ((abs(endPosition.x - startPosition.x) / 100f) * percentage)
        }
    }

    private fun getPositionY(percentage: Long): Float {
        return if (endPosition.y > startPosition.y) {
            startPosition.y + ((abs(endPosition.y - startPosition.y) / 100f) * percentage)
        } else {
            startPosition.y - ((abs(endPosition.y - startPosition.y) / 100f) * percentage)
        }
    }

}
