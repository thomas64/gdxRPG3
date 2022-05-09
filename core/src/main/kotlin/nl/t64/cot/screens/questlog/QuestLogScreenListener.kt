package nl.t64.cot.screens.questlog

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import nl.t64.cot.constants.Constant


internal class QuestLogScreenListener(
    private val closeScreenFunction: () -> Unit,
    private val showLegendFunction: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.stage.actors.items.any { it is Dialog }) {
            return true
        }
        when (keycode) {
            Constant.KEYCODE_RIGHT,
            Input.Keys.L,
            Input.Keys.ESCAPE -> closeScreenFunction.invoke()
            Constant.KEYCODE_SELECT,
            Input.Keys.H -> showLegendFunction.invoke()
        }
        return true
    }

}
