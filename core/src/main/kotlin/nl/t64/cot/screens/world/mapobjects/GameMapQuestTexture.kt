package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.TextureMapObject
import ktx.tiled.property
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.quest.QuestState


class GameMapQuestTexture(val texture: TextureMapObject) {

    private val questId: String = texture.name
    private val isVisibleIfComplete: Boolean = texture.property("visibleIfComplete")
    private val taskId: String = texture.property("task")
    var isVisible: Boolean = texture.property("isVisible")

    fun update() {
        val isFinished = gameData.quests.isCurrentStateEqualOrHigherThan(questId, QuestState.FINISHED)
        val isComplete = gameData.quests.isTaskComplete(questId, taskId)
        isVisible = (isFinished || isComplete) == isVisibleIfComplete
    }

}
