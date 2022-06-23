package nl.t64.cot.screens.loot

import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.conversation.ConversationGraph
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType


class RewardScreen : LootScreen() {

    private lateinit var quest: QuestGraph
    private lateinit var conversation: ConversationGraph

    companion object {
        fun load(reward: Loot, quest: QuestGraph, conversation: ConversationGraph) {
            playSe(AudioEvent.SE_SPARKLE)
            val rewardScreen = screenManager.getScreen(ScreenType.REWARD) as RewardScreen
            rewardScreen.loot = reward
            rewardScreen.lootTitle = "   Reward"
            rewardScreen.quest = quest
            rewardScreen.conversation = conversation
            screenManager.openParchmentLoadScreen(ScreenType.REWARD)
        }
    }

    override fun resolveLootAndCloseScreen(isAllTheLootCleared: Boolean) {
        if (isAllTheLootCleared) {
            quest.finish(true)
            conversation.currentPhraseId = Constant.PHRASE_ID_REWARD_TAKEN
        } else {
            quest.unclaim()
            conversation.currentPhraseId = Constant.PHRASE_ID_REWARD_LEFTOVER
        }
        closeScreen()
    }

}
