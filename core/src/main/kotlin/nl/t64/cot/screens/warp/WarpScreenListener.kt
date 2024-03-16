package nl.t64.cot.screens.warp

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import nl.t64.cot.constants.Constant


class WarpScreenListener(
    private val warpFunction: () -> Unit,
    private val cheatActivateAllPortalsFunction: () -> Unit,
    private val closeScreenFunction: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Constant.KEYCODE_BOTTOM, Input.Keys.A, Input.Keys.SPACE, Input.Keys.ENTER -> warpFunction.invoke()
            Input.Keys.NUM_0 -> cheatActivateAllPortalsFunction.invoke()
            Constant.KEYCODE_RIGHT, Input.Keys.ESCAPE -> closeScreenFunction.invoke()
        }
        return true
    }

}
