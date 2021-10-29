package nl.t64.cot.screens.inventory.messagedialog

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import nl.t64.cot.constants.Constant


class MessageDialogListener(private val closeDialogFunction: () -> Unit) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Constant.KEYCODE_BOTTOM,
            Constant.KEYCODE_RIGHT,
            Input.Keys.ENTER,
            Input.Keys.SPACE,
            Input.Keys.ESCAPE,
            Input.Keys.A -> closeDialogFunction.invoke()
        }
        return true
    }

}
