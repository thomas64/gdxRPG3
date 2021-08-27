package nl.t64.cot.screens.loot

import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.constants.ScreenType


class ReceiveScreen : LootScreen() {

    companion object {
        fun load(receive: Loot) {
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_REWARD)
            val receiveScreen = screenManager.getScreen(ScreenType.RECEIVE) as ReceiveScreen
            receiveScreen.loot = receive
            receiveScreen.lootTitle = "   Receive"
            screenManager.openParchmentLoadScreen(ScreenType.RECEIVE)
        }
    }

    override fun resolveAfterClearingContent() {
        brokerManager.lootObservers.notifyReceiveTaken()
    }

}
