package nl.t64.cot.screens.inventory.inventoryslot

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.profileManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.inventory.InventoryScreen
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.menu.QuestionDialog
import nl.t64.cot.sfx.TransitionImage
import nl.t64.cot.sfx.TransitionPurpose


class CrystalHandler private constructor() {

    private val stage = InventoryUtils.getScreenUI().stage

    companion object {
        fun doAction() {
            CrystalHandler().possibleHandle()
        }
    }

    private fun possibleHandle() {
        if (screenManager.getCurrentParchmentScreen() is InventoryScreen) {
            val question = """
                Do you want to save your progress,
                reset time and everything that happened,
                and return to your home?""".trimIndent()
            val dialog = QuestionDialog(question) { certainHandle() }
            dialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT, 0)
        }
    }

    private fun certainHandle() {
        if (mapManager.currentMap.mapTitle == "ylarus_place") {
            resetTimeDemo()
        } else {
            (screenManager.getCurrentParchmentScreen() as InventoryScreen)
                .closeScreenAnd { resetTime() }
        }
    }

    private fun resetTimeDemo() {
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

    private fun resetTime() {
        playSe(AudioEvent.SE_RESET)
        val actionAfterFade = {
            gameData.resetCycle()
            mapManager.loadMap(Constant.STARTING_MAP)
            mapManager.currentMap.setPlayerSpawnLocationWithId(Constant.STARTING_MAP)
            profileManager.saveProfile()
            worldScreen.changeMap(mapManager.currentMap)
        }
        worldScreen.fadeOut(transitionColor = Color.GRAY,
                            duration = 1f,
                            transitionPurpose = TransitionPurpose.MAP_CHANGE,
                            actionAfterFade = actionAfterFade)
    }

}
