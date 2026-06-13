package nl.t64.cot.screens.inventory.inventoryslot

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.inventory.InventoryScreen
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.warp.ResetTimeScreen
import nl.t64.cot.sfx.TransitionImage
import nl.t64.cot.sfx.TransitionPurpose


object CrystalHandler {

    fun doAction() {
        if (screenManager.getCurrentParchmentScreen() is InventoryScreen) {
            if (mapManager.currentMap.mapTitle == "ylarus_place") {
                resetTimeDemo()
            } else {
                ResetTimeScreen.load()
            }
        }
    }

    private fun resetTimeDemo() {
        val stage = InventoryUtils.getScreenUI().stage
        Gdx.input.inputProcessor = null
        Utils.setGamepadInputProcessor(null)
        val transition = TransitionImage(TransitionPurpose.JUST_FADE, Color.GRAY)
        stage.addActor(transition)
        transition.addAction(Actions.sequence(Actions.alpha(0f),
                                              Actions.run { playSe(AudioEvent.SE_RESET) },
                                              Actions.delay(0.5f),
                                              Actions.fadeIn(Constant.FADE_DURATION),
                                              Actions.delay(1.5f),
                                              Actions.run { screenManager.setScreen(ScreenType.SCENE_CYCLE_4_BEGINS) },
                                              Actions.removeActor()))
    }

}
