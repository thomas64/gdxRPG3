package nl.t64.cot.screens.inventory.messagedialog

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import nl.t64.cot.constants.Constant


internal class MessageDialogListener(
    private val closeDialogFunction: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Constant.KEYCODE_BOTTOM, Input.Keys.ENTER, Input.Keys.A -> closeDialogFunction.invoke()
        }
        return true
    }

}
