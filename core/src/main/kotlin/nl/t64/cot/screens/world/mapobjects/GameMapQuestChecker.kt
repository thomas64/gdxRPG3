package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.tiled.property
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.subjects.ActionObserver


class GameMapQuestChecker(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle), ActionObserver {

    private val quest: QuestGraph = gameData.quests.getQuestById(rectObject.name)
    private val taskId: String = rectObject.property("task")

    init {
        brokerManager.actionObservers.addObserver(this)
    }

    override fun onNotifyActionPressed(checkRect: Rectangle, playerDirection: Direction, playerPosition: Vector2) {
        if (checkRect.overlaps(rectangle)) {
            quest.setTaskComplete(taskId)
        }
    }

}
