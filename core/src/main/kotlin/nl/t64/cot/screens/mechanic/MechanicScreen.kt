package nl.t64.cot.screens.mechanic

import com.badlogic.gdx.scenes.scene2d.ui.Image
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.ParchmentScreen
import nl.t64.cot.screens.inventory.InventoryScreen


class MechanicScreen : ParchmentScreen() {

    private lateinit var mechanicUI: MechanicUI
    private lateinit var heroId: String
    private lateinit var listener: MechanicScreenListener

    companion object {
        fun load(heroId: String, screenShot: Image, parchment: Image) {
            playSe(AudioEvent.SE_MENU_CONFIRM)
            (screenManager.getScreen(ScreenType.MECHANIC) as MechanicScreen).apply {
                this.heroId = heroId
                this.setBackground(screenShot, parchment)
            }
            screenManager.setScreen(ScreenType.MECHANIC)
        }
    }

    override fun show() {
        setInputProcessors(stage)
        createAndSetListener()

        mechanicUI = MechanicUI(stage, heroId)
        MechanicButtonLabels(stage).create()
    }

    override fun render(dt: Float) {
        renderStage(dt)
        mechanicUI.update()
    }

    override fun removeTriggersListener() {
        listener.removeTriggers()
    }

    private fun createAndSetListener() {
        listener = MechanicScreenListener(stage,
                                          { backToInventoryScreen() },
                                          { doAction() },
                                          { selectPreviousTable() },
                                          { selectNextTable() },
                                          { toggleTooltip() })
        stage.addListener(listener)
    }

    private fun doAction() {
        mechanicUI.stopTablesScrolling()
        mechanicUI.doAction()
    }

    private fun selectPreviousTable() {
        mechanicUI.stopTablesScrolling()
        playSe(AudioEvent.SE_MENU_CURSOR)
        mechanicUI.selectPreviousTable()
    }

    private fun selectNextTable() {
        mechanicUI.stopTablesScrolling()
        playSe(AudioEvent.SE_MENU_CURSOR)
        mechanicUI.selectNextTable()
    }

    private fun toggleTooltip() {
        mechanicUI.toggleTooltip()
    }

    private fun backToInventoryScreen() {
        mechanicUI.stopTablesScrolling()
        val screenShot = stage.actors[0] as Image
        val parchment = stage.actors[1] as Image
        InventoryScreen.loadFromMechanic(screenShot, parchment)
    }

}
