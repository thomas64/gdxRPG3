package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.tiled.property
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.components.condition.ConditionDatabase
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.screens.world.entity.EntityState
import nl.t64.cot.subjects.BlockObserver


class GameMapConditionBlocker(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle), BlockObserver {

    private val quest: QuestGraph? = rectObject.name?.let { gameData.quests.getQuestById(it) }
    private val conditionIds: List<String> = createConditions(rectObject)
    private var isActive: Boolean = false

    override fun getBlockerFor(boundingBox: Rectangle, state: EntityState): Rectangle? {
        return rectangle.takeIf { isActive && boundingBox.overlaps(it) }
    }

    override fun isBlocking(point: Vector2, state: EntityState): Boolean {
        return isActive && rectangle.contains(point)
    }

    fun update() {
        val isMeetingConditions = ConditionDatabase.isMeetingConditions(conditionIds, quest?.id)
        checkBlocker(isMeetingConditions)
    }

    private fun checkBlocker(condition: Boolean) {
        val before = isActive
        isActive = condition
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

    private fun createConditions(rectObject: RectangleMapObject): List<String> {
        return rectObject.property<String>("condition")
            .let { ids -> ids.split(",").map { it.trim() } }
    }

}
