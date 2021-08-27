package nl.t64.cot.screens.menu

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import nl.t64.cot.constants.Constant


internal class ListenerKeyStart(updateIndexFunction: (Int) -> Unit,
                                selectItemFunction: () -> Unit,
                                exitIndex: Int
) : InputListener() {

    private val listenerInput: ListenerInput = ListenerInput(updateIndexFunction, selectItemFunction, exitIndex)

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Constant.KEYCODE_START -> listenerInput.inputConfirmDefinedIndex()
        }
        return true
    }

}
