package nl.t64.cot.screens.loot

import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.conversation.ConversationGraph
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType


class TradeScreen : LootScreen() {

    private lateinit var conversation: ConversationGraph

    companion object {
        fun load(trade: Loot, conversation: ConversationGraph) {
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_SPARKLE)
            val tradeScreen = screenManager.getScreen(ScreenType.TRADE) as TradeScreen
            tradeScreen.loot = trade
            tradeScreen.lootTitle = "   Exchange"
            tradeScreen.conversation = conversation
            screenManager.openParchmentLoadScreen(ScreenType.TRADE)
        }
    }

    override fun resolveLootAndCloseScreen(isAllTheLootCleared: Boolean) {
        if (isAllTheLootCleared) {
            conversation.currentPhraseId = Constant.PHRASE_ID_LOOT_TAKEN
        } else {
            conversation.currentPhraseId = Constant.PHRASE_ID_LOOT_LEFTOVER
        }
        closeScreen()
    }

}
