package nl.t64.cot.input

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerAdapter
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog


class AnalogTriggerAdapter(
    private val stage: Stage,
    private val leftTriggerFunction: () -> Unit,
    private val rightTriggerFunction: () -> Unit
) : ControllerAdapter() {

    private var isLeftTriggerPressed = false
    private var isRightTriggerPressed = false

    override fun axisMoved(controller: Controller?, axisIndex: Int, value: Float): Boolean {
        if (stage.actors.items.any { it is Dialog }) return true

        if (value >= .5f) {
            if (axisIndex == 4 && !isLeftTriggerPressed) {
                leftTriggerFunction.invoke()
                isLeftTriggerPressed = true
            } else if (axisIndex == 5 && !isRightTriggerPressed) {
                rightTriggerFunction.invoke()
                isRightTriggerPressed = true
            }
        } else {
            if (axisIndex == 4) {
                isLeftTriggerPressed = false
            } else if (axisIndex == 5) {
                isRightTriggerPressed = false
            }
        }
        return true
    }

}
