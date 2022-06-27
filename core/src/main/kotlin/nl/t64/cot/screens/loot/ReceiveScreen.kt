package nl.t64.cot.screens.loot

import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.conversation.ConversationGraph
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType


class ReceiveScreen : LootScreen() {

    private lateinit var quest: QuestGraph
    private lateinit var conversation: ConversationGraph

    companion object {
        fun load(receive: Loot, quest: QuestGraph, conversation: ConversationGraph) {
            playSe(AudioEvent.SE_SPARKLE)
            val receiveScreen = screenManager.getScreen(ScreenType.RECEIVE) as ReceiveScreen
            receiveScreen.loot = receive
            receiveScreen.lootTitle = "   Receive"
            receiveScreen.quest = quest
            receiveScreen.conversation = conversation
            screenManager.openParchmentLoadScreen(ScreenType.RECEIVE)
        }
    }

    override fun resolveLootAndCloseScreen(isAllTheLootCleared: Boolean) {
        if (isAllTheLootCleared) {
            quest.accept()
            conversation.currentPhraseId = Constant.PHRASE_ID_LOOT_TAKEN
        } else {
            quest.know()
            conversation.currentPhraseId = Constant.PHRASE_ID_LOOT_LEFTOVER
        }
        closeScreen(audioEvent = AudioEvent.SE_CONVERSATION_NEXT)
    }

}
