package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import nl.t64.cot.constants.Constant


internal class CutSceneListener(
    private val closeCutscene: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.ESCAPE, Constant.KEYCODE_START -> closeCutscene.invoke()
        }
        return true
    }

}
