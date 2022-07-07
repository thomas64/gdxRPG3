package nl.t64.cot.screens.world.schedule

import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent
import java.time.Duration
import java.time.LocalTime
import kotlin.math.abs


class Pietje1 : Schedule {

    private val pietje1 = Entity("pietje1", InputScheduledNpc(), PhysicsScheduledNpc(), GraphicsNpc("man01"))

    private val schedule: List<State> = listOf(
        State("honeywood", Direction.SOUTH, EntityState.IDLE, 7, 30, 7, 40, 550, 350, 550, 350),
        State("honeywood", Direction.EAST, EntityState.IDLE, 7, 40, 7, 41, 550, 350, 550, 350),
        State("honeywood", Direction.EAST, EntityState.WALKING, 7, 41, 7, 45, 550, 350, 600, 350),
        State("honeywood", Direction.NORTH, EntityState.WALKING, 7, 45, 7, 50, 600, 350, 600, 400),
    )

    override fun update() {
        val currentMap = mapManager.currentMap.mapTitle
        val currentTime = gameData.clock.getTimeOfDay()

        schedule
            .filter { currentMap == it.map }
            .singleOrNull { currentTime.isInState(it) }
            ?.handle()
            ?: remove()
    }

    private fun LocalTime.isInState(it: State): Boolean {
        return (this == it.startTime || this.isAfter(it.startTime)) && this.isBefore(it.endTime)
    }

    private fun State.handle() {
        pietje1.send(LoadEntityEvent(state, direction, getCurrentPosition()))
        brokerManager.entityObservers.notifyAddScheduledEntity(pietje1)
    }

    private fun remove() {
        brokerManager.entityObservers.notifyRemoveScheduledEntity(pietje1)
    }

    private fun State.getCurrentPosition(): Vector2 {
        val currentTime = gameData.clock.getTimeOfDay()

        val percentage: Long = Duration.between(startTime, currentTime)
            .multipliedBy(100)
            .dividedBy(Duration.between(startTime, endTime))

        val x = startPosition.x + ((abs(endPosition.x - startPosition.x) / 100f) * percentage)
        val y = startPosition.y + ((abs(endPosition.y - startPosition.y) / 100f) * percentage)
        return Vector2(x, y)
    }

}

private class State(
    val map: String,
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
    val startTime: LocalTime = LocalTime.of(startTimeHours, startTimeMinutes)
    val endTime: LocalTime = LocalTime.of(endTimeHours, endTimeMinutes)
    val startPosition: Vector2 = Vector2(startPositionX.toFloat(), startPositionY.toFloat())
    val endPosition: Vector2 = Vector2(endPositionX.toFloat(), endPositionY.toFloat())
}
