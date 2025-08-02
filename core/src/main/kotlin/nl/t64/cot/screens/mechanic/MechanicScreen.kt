package nl.t64.cot.screens.mechanic

import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.ParchmentScreen
import nl.t64.cot.screens.inventory.InventoryScreen


class MechanicScreen : ParchmentScreen() {

    private lateinit var listener: MechanicScreenListener

    companion object {
        fun load() {
            playSe(AudioEvent.SE_SCROLL)
            screenManager.openParchmentLoadScreen(ScreenType.MECHANIC)
        }
    }

    override fun show() {
        setInputProcessors(stage)
        createAndSetListener()
        stage.addListener(listener)
    }

    override fun render(dt: Float) {
        renderStage(dt)
    }

    private fun createAndSetListener() {
        listener = MechanicScreenListener({ InventoryScreen.load() })
    }

}
