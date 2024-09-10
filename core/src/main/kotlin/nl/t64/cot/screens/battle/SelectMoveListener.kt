package nl.t64.cot.screens.battle

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import nl.t64.cot.constants.Constant


class SelectMoveListener(
    private val moveLeft: () -> Unit,
    private val moveRight: () -> Unit,
    private val confirm: () -> Unit,
    private val back: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.stage.actors.items.any { it is Dialog }) return true

        when (keycode) {
            Input.Keys.LEFT -> moveLeft.invoke()
            Input.Keys.RIGHT -> moveRight.invoke()
            Constant.KEYCODE_BOTTOM, Input.Keys.ENTER, Input.Keys.A -> confirm.invoke()
            Constant.KEYCODE_RIGHT, Input.Keys.ESCAPE -> handleEscape(back)
        }
        return true
    }

}
