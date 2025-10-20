package nl.t64.cot.screens.alchemist

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import nl.t64.cot.constants.Constant


internal class AlchemistScreenListener(
    private val closeScreenFunction: () -> Unit,
    private val actionFunction: () -> Unit,
    private val toggleTooltipFunction: () -> Unit,
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.stage.actors.items.any { it is Dialog }) return true

        when (keycode) {
            Constant.KEYCODE_RIGHT, Input.Keys.ESCAPE -> closeScreenFunction.invoke()
            Constant.KEYCODE_BOTTOM, Input.Keys.A -> actionFunction.invoke()
            Constant.KEYCODE_L3, Input.Keys.T -> toggleTooltipFunction.invoke()
        }
        return true
    }

}
