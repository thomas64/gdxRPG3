package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.tiled.propertyOrNull
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.screens.world.entity.EntityState
import nl.t64.cot.subjects.BlockObserver


abstract class GameMapQuestBlocker(
    rectObject: RectangleMapObject
) : GameMapObject(rectObject.rectangle), BlockObserver {

    protected val quest: QuestGraph? = rectObject.name?.let { gameData.quests.getQuestById(it) }
    private var isActive: Boolean = false

    companion object {
        fun load(rectObject: RectangleMapObject): GameMapQuestBlocker {
            rectObject.propertyOrNull<Boolean>("activeWhenTaskIsActive")?.let {
                return GameMapQuestBlockerActive(rectObject, it)
            }
            rectObject.propertyOrNull<Boolean>("activeWhenTaskIsComplete")?.let {
                return GameMapQuestBlockerComplete(rectObject, it)
            }
            rectObject.propertyOrNull<Boolean>("activeWhenQuestIsAccepted")?.let {
                return GameMapQuestBlockerAccepted(rectObject, it)
            }
            rectObject.propertyOrNull<Boolean>("activeWhenQuestIsFinished")?.let {
                return GameMapQuestBlockerFinished(rectObject, it)
            }
            rectObject.propertyOrNull<String>("activeWhenCondition")?.let { ids ->
                val conditionIds: List<String> = ids.split(",").map { it.trim() }
                return GameMapQuestBlockerCondition(rectObject, conditionIds)
            }
            throw IllegalArgumentException("Not an usable QuestBlocker property available.")
        }
    }

    override fun getBlockerFor(boundingBox: Rectangle, state: EntityState): Rectangle? {
        return rectangle.takeIf { isActive && boundingBox.overlaps(it) }
    }

    override fun isBlocking(point: Vector2, state: EntityState): Boolean {
        return isActive && rectangle.contains(point)
    }

    abstract fun update()

    protected fun checkBlocker(condition: Boolean) {
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

}
