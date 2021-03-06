package nl.t64.cot.screens.loot

import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.constants.ScreenType


class SpoilsCutsceneScreen : LootScreen() {

    private lateinit var currentCutsceneScreen: ScreenType

    companion object {
        fun load(spoils: Loot, currentCutsceneScreen: ScreenType) {
            playSe(AudioEvent.SE_SPARKLE)
            val spoilsScreen = screenManager.getScreen(ScreenType.SPOILS_CUTSCENE) as SpoilsCutsceneScreen
            spoilsScreen.currentCutsceneScreen = currentCutsceneScreen
            spoilsScreen.loot = spoils
            spoilsScreen.lootTitle = "   Loot"
            screenManager.openParchmentLoadScreen(ScreenType.SPOILS_CUTSCENE)
        }
    }

    override fun resolveLootAndCloseScreen(notUsedHere: Boolean) {
        closeScreen(currentCutsceneScreen, AudioEvent.SE_CONVERSATION_NEXT)
    }

}
