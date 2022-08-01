package nl.t64.cot.screens.loot

import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.constants.ScreenType


open class SpoilsScreen : LootScreen() {

    companion object {
        fun load(spoils: Loot) {
            playSe(AudioEvent.SE_SPARKLE)
            val spoilsScreen = screenManager.getScreen(ScreenType.SPOILS) as SpoilsScreen
            spoilsScreen.loot = spoils
            spoilsScreen.lootTitle = "   Loot"
            screenManager.openParchmentLoadScreen(ScreenType.SPOILS)
        }
    }

    override fun resolveLootAndCloseScreen(notUsedHere: Boolean) {
        screenManager.getWorldScreen().updateLoot()
        closeScreen(audioEvent = AudioEvent.SE_CONVERSATION_NEXT)
    }

}
