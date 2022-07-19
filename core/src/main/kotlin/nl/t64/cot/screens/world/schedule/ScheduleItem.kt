package nl.t64.cot.screens.world.schedule

import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.components.time.GameTime
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState
import java.time.Duration
import java.time.LocalTime
import kotlin.math.abs


class ScheduleItem(
    private val mapTitle: String,
    startTime: String,
    endTime: String,
    val direction: Direction,
    val state: EntityState,
    private val startPositionId: String,
    private val endPositionId: String,
    val conversationId: String = "",
) {
    private val startTime: LocalTime = startTime.toLocalTime()
    private val endTime: LocalTime = endTime.toLocalTime()

    fun isCurrentMapInState(): Boolean {
        val currentMap: String = mapManager.currentMap.mapTitle
        return currentMap == mapTitle
    }

    fun isCurrentTimeInState(): Boolean {
        val currentTime: LocalTime = gameData.clock.getTimeOfDay()
        return (currentTime == startTime || currentTime.isAfter(startTime)) && currentTime.isBefore(endTime)
    }

    fun getCurrentPosition(): Vector2 {
        val percentage: Float = getTimePercentageOfCurrentTimeBetweenStartAndEndTime()
        val startPosition: Vector2 = startPositionId.toPosition()
        val endPosition: Vector2 = endPositionId.toPosition()
        val x: Float = percentage.toPosition(startPosition.x, endPosition.x)
        val y: Float = percentage.toPosition(startPosition.y, endPosition.y)
        return Vector2(x, y)
    }

    private fun getTimePercentageOfCurrentTimeBetweenStartAndEndTime(): Float {
        val currentTime: LocalTime = gameData.clock.getTimeOfDay()
        val currentSeconds: Long = Duration.between(startTime, currentTime).toSeconds()
        val targetSeconds: Long = Duration.between(startTime, endTime).toSeconds()
        return currentSeconds * 100f / targetSeconds
    }

    private fun Float.toPosition(startPos: Float, endPos: Float): Float {
        return if (endPos > startPos) {
            startPos + ((abs(endPos - startPos) / 100f) * this)
        } else {
            startPos - ((abs(endPos - startPos) / 100f) * this)
        }
    }

    private fun String.toPosition(): Vector2 {
        val scheduledPositionOfRectangleOnMap = mapManager.currentMap.schedules.single { it.name == this }.rectangle
        return Vector2(scheduledPositionOfRectangleOnMap.x, scheduledPositionOfRectangleOnMap.y)
    }

    private fun String.toLocalTime(): LocalTime {
        return removePrefix("0").split(":").let {
            GameTime.of(it[0].toInt(), it[1].toInt())
        }
    }

}
