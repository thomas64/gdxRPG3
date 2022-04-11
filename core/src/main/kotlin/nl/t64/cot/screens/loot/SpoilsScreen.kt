package nl.t64.cot.screens.loot

import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.constants.ScreenType


class SpoilsScreen : LootScreen() {

    companion object {
        fun load(spoils: Loot) {
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_SPARKLE)
            val spoilsScreen = screenManager.getScreen(ScreenType.SPOILS) as SpoilsScreen
            spoilsScreen.loot = spoils
            spoilsScreen.lootTitle = "   Loot"
            screenManager.openParchmentLoadScreen(ScreenType.SPOILS)
        }
    }

    override fun closeScreen(isAllTheLootCleared: Boolean) {
        brokerManager.lootObservers.notifySpoilsUpdated()
        super.closeScreen(false)
        // Above argument is false so that resolveAfterClearingContent() is not called in closeScreen(),
        // which is empty anyway and also already resolved at the line with notifySpoilsUpdated().
        // This is necessary, so that loot is always updated, even if the spoils are not taken.
    }

    override fun resolveAfterClearingContent() {
        // empty
    }

}
