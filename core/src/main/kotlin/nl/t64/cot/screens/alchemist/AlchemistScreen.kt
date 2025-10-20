package nl.t64.cot.screens.alchemist

import com.badlogic.gdx.scenes.scene2d.ui.Image
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.ParchmentScreen
import nl.t64.cot.screens.inventory.InventoryScreen


class AlchemistScreen : ParchmentScreen() {

    private lateinit var alchemistUI: AlchemistUI
    private lateinit var heroId: String
    private lateinit var listener: AlchemistScreenListener

    companion object {
        fun load(heroId: String, screenShot: Image, parchment: Image) {
            playSe(AudioEvent.SE_MENU_CONFIRM)
            (screenManager.getScreen(ScreenType.ALCHEMIST) as AlchemistScreen).apply {
                this.heroId = heroId
                this.setBackground(screenShot, parchment)
            }
            screenManager.setScreen(ScreenType.ALCHEMIST)
        }
    }

    override fun show() {
        setInputProcessors(stage)
        createAndSetListener()

        alchemistUI = AlchemistUI(stage, heroId)
        AlchemistButtonLabels(stage).create()
    }

    override fun render(dt: Float) {
        renderStage(dt)
        alchemistUI.update()
    }

    private fun createAndSetListener() {
        listener = AlchemistScreenListener({ backToInventoryScreen() },
                                           { doAction() },
                                           { toggleTooltip() })
        stage.addListener(listener)
    }

    private fun doAction() {
        alchemistUI.stopTablesScrolling()
        alchemistUI.doAction()
    }

    private fun toggleTooltip() {
        alchemistUI.toggleTooltip()
    }

    private fun backToInventoryScreen() {
        alchemistUI.stopTablesScrolling()
        val screenShot = stage.actors[0] as Image
        val parchment = stage.actors[1] as Image
        InventoryScreen.loadFromAlchemist(screenShot, parchment)
    }

}
