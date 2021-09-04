package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.tiled.property
import ktx.tiled.propertyOrNull
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.components.quest.QuestState
import nl.t64.cot.screens.world.entity.EntityState
import nl.t64.cot.subjects.BlockObserver


class GameMapQuestBlocker(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle), BlockObserver {

    private val quest: QuestGraph = gameData.quests.getQuestById(rectObject.name)
    private val isActiveIfComplete: Boolean = rectObject.property("activeIfComplete")
    private val taskId: String? = rectObject.propertyOrNull("task")
    private var isActive: Boolean = rectObject.property<Boolean>("isActive").also {
        if (it) brokerManager.blockObservers.addObserver(this)
    }

    override fun getBlockerFor(boundingBox: Rectangle, state: EntityState): Rectangle? {
        return rectangle.takeIf { isActive && boundingBox.overlaps(it) }
    }

    override fun isBlocking(point: Vector2, state: EntityState): Boolean {
        return isActive && rectangle.contains(point)
    }

    fun update() {
        val isFinished = quest.isCurrentStateEqualOrHigherThan(QuestState.FINISHED)
        val isComplete = quest.isTaskComplete(taskId)

        val before = isActive
        isActive = (isFinished || isComplete) == isActiveIfComplete
        val after = isActive
        if (before != after) {
            changeBlocker()
        }
    }

    private fun changeBlocker() {
        if (isActive) {
            brokerManager.blockObservers.addObserver(this)
        } else {
            brokerManager.blockObservers.removeObserver(this)
        }
        mapManager.setTiledGraph()
    }

}
