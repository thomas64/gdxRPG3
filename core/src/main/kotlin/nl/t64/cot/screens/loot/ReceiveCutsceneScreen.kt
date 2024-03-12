package nl.t64.cot.screens.loot

import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.constants.ScreenType


class ReceiveCutsceneScreen : LootScreen() {

    private lateinit var nextCutsceneScreen: ScreenType

    companion object {
        fun load(receive: Loot, nextCutsceneScreen: ScreenType) {
            playSe(AudioEvent.SE_SPARKLE)
            val receiveScreen = screenManager.getScreen(ScreenType.RECEIVE_CUTSCENE) as ReceiveCutsceneScreen
            receiveScreen.nextCutsceneScreen = nextCutsceneScreen
            receiveScreen.loot = receive
            receiveScreen.lootTitle = "   Receive"
            screenManager.openParchmentLoadScreen(ScreenType.RECEIVE_CUTSCENE)
        }
    }

    override fun resolveLootAndCloseScreen(isAllTheLootCleared: Boolean) {
        if (isAllTheLootCleared) {
            closeScreen(nextCutsceneScreen, AudioEvent.SE_CONVERSATION_NEXT)
        } else {
            playSe(AudioEvent.SE_MENU_ERROR)
        }
    }

}
