package nl.t64.cot.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.ScreenUtils
import nl.t64.cot.Utils
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType


abstract class ParchmentScreen : Screen {

    protected val stage: Stage = Stage()

    open fun getScreenUI(): ScreenUI {
        throw IllegalStateException("ScreenUI not implemented here.")
    }

    override fun resize(width: Int, height: Int) {
        // empty
    }

    override fun pause() {
        // empty
    }

    override fun resume() {
        // empty
    }

    override fun hide() {
        stage.clear()
    }

    override fun dispose() {
        stage.dispose()
    }

    protected fun renderStage(dt: Float) {
        ScreenUtils.clear(Color.BLACK)
        stage.act(dt)
        stage.draw()
    }

    protected fun closeScreen(fadeToScreen: ScreenType = ScreenType.WORLD,
                              audioEvent: AudioEvent = AudioEvent.SE_SCROLL) {
        setInputProcessors(null)
        playSe(audioEvent)
        fadeParchment(fadeToScreen)
        removeTriggersListener()
    }

    protected fun setInputProcessors(inputProcessor: InputProcessor?) {
        Gdx.input.inputProcessor = inputProcessor
        Utils.setGamepadInputProcessor(inputProcessor)
    }

    protected open fun removeTriggersListener() {
        // empty
    }

    private fun fadeParchment(fadeToScreen: ScreenType) {
        val parchment = prepareBackgroundForFade()
        parchment.addAction(Actions.sequence(Actions.fadeOut(Constant.FADE_DURATION),
                                             Actions.run { screenManager.setScreen(fadeToScreen) }))
    }

    protected fun prepareBackgroundForFade(): Image {
        val screenshot = stage.actors[0] as Image
        val parchment = stage.actors[1] as Image
        stage.clear()
        setBackground(screenshot, parchment)
        return parchment
    }

    fun setBackground(screenshot: Image, parchment: Image) {
        stage.addActor(screenshot)
        stage.addActor(parchment)
    }

}
