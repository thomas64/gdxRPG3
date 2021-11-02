package nl.t64.cot.screens.menu

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import nl.t64.cot.constants.Constant


internal class ListenerKeyConfirmDialog(selectItemFunction: () -> Unit) : InputListener() {

    private val listenerInput: ListenerInput = ListenerInput(selectItemFunction)

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Constant.KEYCODE_BOTTOM,
            Input.Keys.A,
            Input.Keys.SPACE,
            Input.Keys.ENTER -> listenerInput.inputConfirm()
        }
        return true
    }

}
