package nl.t64.cot.screens.help

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import nl.t64.cot.constants.Constant


internal class HelpScreenListener(
    private val closeScreenFunction: () -> Unit,
    private val startScrollUpFunction: () -> Unit,
    private val startScrollDownFunction: () -> Unit,
    private val stopScrollUpFunction: () -> Unit,
    private val stopScrollDownFunction: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Constant.KEYCODE_RIGHT, Input.Keys.T, Input.Keys.ESCAPE -> closeScreenFunction.invoke()
            Input.Keys.UP -> startScrollUpFunction.invoke()
            Input.Keys.DOWN -> startScrollDownFunction.invoke()
        }
        return true
    }

    override fun keyUp(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.UP -> stopScrollUpFunction.invoke()
            Input.Keys.DOWN -> stopScrollDownFunction.invoke()
        }
        return true
    }

}
