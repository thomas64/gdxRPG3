package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.tiled.propertyOrNull
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.condition.ConditionDatabase
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.subjects.ActionObserver


class GameMapQuestChecker(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle), ActionObserver {

    private val quest: QuestGraph = gameData.quests.getQuestById(rectObject.name)
    private val taskIdToComplete: String? = rectObject.propertyOrNull("task")
    private val taskIdToFail: String? = rectObject.propertyOrNull("fail")
    private val conditions: List<String> = createConditions(rectObject)

    init {
        brokerManager.actionObservers.addObserver(this)
    }

    override fun onNotifyActionPressed(checkRect: Rectangle, playerDirection: Direction, playerPosition: Vector2) {
        if (checkRect.overlaps(rectangle) && ConditionDatabase.isMeetingConditions(conditions, quest.id)) {
            when {
                taskIdToComplete == null
                    && taskIdToFail == null -> error("QuestChecker must have task or fail property.")

                taskIdToComplete != null
                    && taskIdToFail != null -> error("QuestChecker can't have both task and fail property.")

                taskIdToComplete != null -> quest.setTaskComplete(taskIdToComplete)

                taskIdToFail != null -> quest.setTaskFailed(taskIdToFail)

                else -> error("Unexpected state in QuestChecker.")
            }
        }
    }

}
