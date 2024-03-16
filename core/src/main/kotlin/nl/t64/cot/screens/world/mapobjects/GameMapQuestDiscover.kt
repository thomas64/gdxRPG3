package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import ktx.tiled.property
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.condition.ConditionDatabase
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.subjects.CollisionObserver


class GameMapQuestDiscover(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle), CollisionObserver {

    private val quest: QuestGraph = gameData.quests.getQuestById(rectObject.name)
    private val taskId: String = rectObject.property("task")
    private val conditionIds: List<String> = createConditions(rectObject)

    init {
        brokerManager.collisionObservers.addObserver(this)
    }

    override fun onNotifyCollision(playerBoundingBox: Rectangle, playerDirection: Direction) {
        if (playerBoundingBox.overlaps(rectangle) && ConditionDatabase.isMeetingConditions(conditionIds)) {
            quest.setTaskComplete(taskId)
        }
    }

}
