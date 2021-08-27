package nl.t64.cot.screens.menu

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener


internal class ListenerKeyDelete(updateIndexFunction: (Int) -> Unit,
                                 selectItemFunction: () -> Unit,
                                 deleteIndex: Int
) : InputListener() {

    private val listenerInput: ListenerInput = ListenerInput(updateIndexFunction, selectItemFunction, deleteIndex)

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.FORWARD_DEL -> listenerInput.inputConfirmDefinedIndex()
        }
        return true
    }

}
