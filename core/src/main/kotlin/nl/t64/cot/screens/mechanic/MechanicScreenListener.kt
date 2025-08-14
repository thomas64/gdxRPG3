package nl.t64.cot.screens.mechanic

import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import nl.t64.cot.Utils
import nl.t64.cot.constants.Constant
import nl.t64.cot.input.AnalogTriggerAdapter


internal class MechanicScreenListener(
    stage: Stage,
    private val closeScreenFunction: () -> Unit,
    private val previousTableFunction: () -> Unit,
    private val nextTableFunction: () -> Unit,
    private val toggleTooltipFunction: () -> Unit,
) : InputListener() {

    private val triggerAdapter = AnalogTriggerAdapter(stage, previousTableFunction, nextTableFunction)

    init {
        Utils.runWithDelay(Constant.FADE_DURATION) {
            Controllers.addListener(triggerAdapter)
        }
    }

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.stage.actors.items.any { it is Dialog }) return true

        when (keycode) {
            Constant.KEYCODE_RIGHT, Input.Keys.ESCAPE -> closeScreenFunction.invoke()
            Input.Keys.Z -> previousTableFunction.invoke()
            Input.Keys.X -> nextTableFunction.invoke()
            Constant.KEYCODE_L3, Input.Keys.T -> toggleTooltipFunction.invoke()
        }
        return true
    }

    fun removeTriggers() {
        Controllers.removeListener(triggerAdapter)
    }

}
