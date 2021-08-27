package nl.t64.cot.screens.menu

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener


internal class ListenerKeyVertical(updateIndexFunction: (Int) -> Unit,
                                   numberOfItems: Int
) : InputListener() {

    private val listenerInput: ListenerInput = ListenerInput(updateIndexFunction, numberOfItems)

    fun updateSelectedIndex(newSelectedIndex: Int) {
        listenerInput.updateSelectedIndex(newSelectedIndex)
    }

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.UP -> listenerInput.inputPrev()
            Input.Keys.DOWN -> listenerInput.inputNext()
        }
        return true
    }

}
