package nl.t64.cot.screens.menu

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import nl.t64.cot.constants.Constant


internal class ListenerKeyCancel : InputListener {

    private val listenerInput: ListenerInput

    constructor(selectItemFunction: () -> Unit) {
        listenerInput = ListenerInput(selectItemFunction)
    }

    constructor(updateIndexFunction: (Int) -> Unit, selectItemFunction: () -> Unit, exitIndex: Int) {
        listenerInput = ListenerInput(updateIndexFunction, selectItemFunction, exitIndex)
    }

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Constant.KEYCODE_RIGHT, Input.Keys.ESCAPE -> listenerInput.inputCancel()
        }
        return true
    }

}
