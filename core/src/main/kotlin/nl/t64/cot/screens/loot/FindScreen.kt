package nl.t64.cot.screens.loot

import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.constants.ScreenType


class FindScreen : LootScreen() {

    companion object {
        fun load(loot: Loot, event: AudioEvent) {
            playSe(event)
            val findScreen = screenManager.getScreen(ScreenType.FIND) as FindScreen
            findScreen.loot = loot
            findScreen.lootTitle = "   Found"
            screenManager.openParchmentLoadScreen(ScreenType.FIND)
        }
    }

    override fun resolveLootAndCloseScreen(isAllTheLootCleared: Boolean) {
        if (isAllTheLootCleared) {
            brokerManager.lootObservers.notifyLootTaken()
        }
        closeScreen(audioEvent = AudioEvent.SE_CONVERSATION_NEXT)
    }

}
