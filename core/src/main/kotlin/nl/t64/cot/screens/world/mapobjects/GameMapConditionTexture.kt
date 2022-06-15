package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.TextureMapObject
import ktx.tiled.property
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.condition.ConditionDatabase
import nl.t64.cot.components.quest.QuestGraph


class GameMapConditionTexture(
    val texture: TextureMapObject
) {
    private val quest: QuestGraph? = texture.name?.let { gameData.quests.getQuestById(it) }
    private val conditionIds: List<String> = createConditions()
    var isVisible: Boolean = false

    fun update() {
        isVisible = ConditionDatabase.isMeetingConditions(conditionIds, quest?.id)
    }

    private fun createConditions(): List<String> {
        return texture.property<String>("condition")
            .let { ids -> ids.split(",").map { it.trim() } }
    }

}
