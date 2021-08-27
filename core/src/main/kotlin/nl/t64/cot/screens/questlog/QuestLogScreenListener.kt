package nl.t64.cot.screens.questlog

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import nl.t64.cot.constants.Constant


internal class QuestLogScreenListener(
    private val closeScreenFunction: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Constant.KEYCODE_RIGHT,
            Input.Keys.L,
            Input.Keys.ESCAPE -> closeScreenFunction.invoke()
        }
        return true
    }

}
