package nl.t64.cot.screens.menu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.ScreenUtils
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.profileManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.audio.stopAllBgm
import nl.t64.cot.audio.stopSe
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.gamestate.SaveFile


abstract class MenuLoad : MenuScreen() {

    private var isBgmFading = false

    protected abstract fun updateSelectedIndices()

    override fun render(dt: Float) {
        ScreenUtils.clear(Color.BLACK)
        stage.act(dt)
        updateSelectedIndices()
        if (isBgmFading) {
            audioManager.certainFadeBgmBgs()
        }
        stage.draw()
    }

    protected fun fadeBeforeOpenWorldScreen(saveFile: SaveFile) {
        Gdx.input.inputProcessor = null
        Utils.setGamepadInputProcessor(null)
        stage.addAction(Actions.sequence(Actions.run { isBgmFading = true },
                                         Actions.fadeOut(Constant.FADE_DURATION),
                                         Actions.run { isBgmFading = false },
                                         Actions.run { stopAllBgm() },
                                         Actions.run { openWorldScreen(saveFile) }))
    }

    protected fun errorSound() {
        stopSe(AudioEvent.SE_MENU_CONFIRM)
        playSe(AudioEvent.SE_MENU_ERROR)
    }

    private fun openWorldScreen(saveFile: SaveFile) {
        mapManager.disposeOldMaps()
        screenManager.getScreen(ScreenType.WORLD) // just load the constructor.
        profileManager.loadProfile(saveFile)
        if (gameData.cutscenes.isEverPlayedBefore(ScreenType.SCENE_INTRO)) {
            screenManager.setScreen(ScreenType.WORLD)
        } else {
            gameData.cutscenes.setPlayedEver(ScreenType.SCENE_INTRO)
            screenManager.setScreen(ScreenType.SCENE_INTRO)
        }
    }

}
