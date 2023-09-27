package nl.t64.cot.screens.help

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import nl.t64.cot.constants.Constant


internal class HelpScreenListener(
    private val closeScreenFunction: () -> Unit,
    private val scrollUpFunction: () -> Unit,
    private val scrollDownFunction: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Constant.KEYCODE_RIGHT, Input.Keys.H, Input.Keys.ESCAPE -> closeScreenFunction.invoke()
            Input.Keys.UP -> scrollUpFunction.invoke()
            Input.Keys.DOWN -> scrollDownFunction.invoke()
        }
        return true
    }

}
