package nl.t64.cot.screens

import com.badlogic.gdx.Screen
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType


abstract class ParchmentScreen : Screen {

    val stage: Stage = Stage()

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

    fun fadeParchment() {
        val screenshot = stage.actors[0] as Image
        val parchment = stage.actors[1] as Image
        stage.clear()
        setBackground(screenshot, parchment)
        parchment.addAction(Actions.sequence(Actions.fadeOut(Constant.FADE_DURATION),
                                             Actions.run { screenManager.setScreen(ScreenType.WORLD) }))
    }

    fun setBackground(screenshot: Image, parchment: Image) {
        stage.addActor(screenshot)
        stage.addActor(parchment)
    }

}
