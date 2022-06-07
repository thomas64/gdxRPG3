package nl.t64.cot.screens.world.conversation

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.components.loot.Spoil
import nl.t64.cot.components.quest.QuestGraph


class ConversationSpoilLoader private constructor(
    private val conversationId: String,
    private val getLoot: QuestGraph.() -> Loot
) {

    companion object {
        fun getLoot(conversationId: String, getLoot: QuestGraph.() -> Loot): Loot {
            return ConversationSpoilLoader(conversationId, getLoot).getSpoil().loot
        }
    }

    fun getSpoil(): Spoil {
        return if (gameData.spoils.containsActiveSpoil(conversationId)) {
            getActiveSpoil()
        } else {
            createNewSpoil()
        }
    }

    private fun getActiveSpoil(): Spoil {
        return gameData.spoils.getByConversationId(conversationId)
    }

    private fun createNewSpoil(): Spoil {
        val quest = gameData.quests.getQuestById(conversationId)
        return Spoil(loot = getLoot.invoke(quest)).also {
            gameData.spoils.addSpoil(conversationId, it)
        }
    }

}
