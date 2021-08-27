package nl.t64.cot.screens.inventory

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener


internal class ListenerKeyVertical(
    private val updateIndexFunction: (Int) -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.UP -> updateIndexFunction.invoke(-1)
            Input.Keys.DOWN -> updateIndexFunction.invoke(1)
        }
        return true
    }

}
