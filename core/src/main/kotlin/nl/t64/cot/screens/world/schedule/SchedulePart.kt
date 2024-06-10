package nl.t64.cot.screens.world.schedule

import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState
import kotlin.math.abs


class SchedulePart(
    private val mapTitle: String,
    private val startTime: String,
    private val endTime: String,
    val direction: Direction,
    val state: EntityState,
    private val startPositionId: String,
    private val endPositionId: String,
    val conversationId: String = "",
) {

    fun isCurrentMapInState(): Boolean {
        val currentMap: String = mapManager.currentMap.mapTitle
        return currentMap == mapTitle
    }

    fun isCurrentTimeInState(): Boolean {
        return gameData.clock.isCurrentTimeInBetween(startTime, endTime)
    }

    fun getCurrentPosition(): Vector2 {
        val percentage: Float = gameData.clock.getPercentageOfCurrentTimeBetween(startTime, endTime)
        val startPosition: Vector2 = startPositionId.toPosition()
        val endPosition: Vector2 = endPositionId.toPosition()
        val x: Float = percentage.toPosition(startPosition.x, endPosition.x)
        val y: Float = percentage.toPosition(startPosition.y, endPosition.y)
        return Vector2(x, y)
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

}
