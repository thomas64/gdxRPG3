package nl.t64.cot.screens.world

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import nl.t64.cot.constants.Constant


internal class MiniMapListener(
    private val closeMiniMap: () -> Unit
) : InputAdapter() {

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Constant.KEYCODE_SELECT,
            Constant.KEYCODE_RIGHT,
            Input.Keys.ESCAPE,
            Input.Keys.M -> closeMiniMap.invoke()
        }
        return false
    }

}
