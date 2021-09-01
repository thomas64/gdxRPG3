package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.TextureMapObject
import ktx.tiled.property
import ktx.tiled.propertyOrNull
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.components.quest.QuestState


class GameMapQuestTexture(val texture: TextureMapObject) {

    private val quest: QuestGraph = gameData.quests.getQuestById(texture.name)
    private val isVisibleIfComplete: Boolean = texture.property("visibleIfComplete")
    private val taskId: String? = texture.propertyOrNull("task")
    var isVisible: Boolean = texture.property("isVisible")

    fun update() {
        val isFinished = quest.isCurrentStateEqualOrHigherThan(QuestState.FINISHED)
        val isComplete = quest.isTaskComplete(taskId)
        isVisible = (isFinished || isComplete) == isVisibleIfComplete
    }

}
