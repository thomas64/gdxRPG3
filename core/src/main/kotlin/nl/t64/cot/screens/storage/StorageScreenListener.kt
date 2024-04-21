package nl.t64.cot.screens.storage

import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import nl.t64.cot.Utils
import nl.t64.cot.constants.Constant
import nl.t64.cot.input.AnalogTriggerAdapter


internal class StorageScreenListener(
    stage: Stage,
    private val closeScreenFunction: () -> Unit,
    private val takeOneFunction: () -> Unit,
    private val takeHalfFunction: () -> Unit,
    private val takeFullFunction: () -> Unit,
    private val equipFunction: () -> Unit,
    private val previousHeroFunction: () -> Unit,
    private val nextHeroFunction: () -> Unit,
    private val previousTableFunction: () -> Unit,
    private val nextTableFunction: () -> Unit,
    private val sortStorageFunction: () -> Unit,
    private val toggleTooltipFunction: () -> Unit,
    private val toggleCompareFunction: () -> Unit
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
            Constant.KEYCODE_BOTTOM, Input.Keys.A -> takeOneFunction.invoke()
            Constant.KEYCODE_LEFT, Input.Keys.S -> takeHalfFunction.invoke()
            Constant.KEYCODE_TOP, Input.Keys.D -> takeFullFunction.invoke()
            Constant.KEYCODE_START, Input.Keys.E -> equipFunction.invoke()
            Constant.KEYCODE_L1, Input.Keys.Q -> previousHeroFunction.invoke()
            Constant.KEYCODE_R1, Input.Keys.W -> nextHeroFunction.invoke()
            Input.Keys.Z -> previousTableFunction.invoke()
            Input.Keys.X -> nextTableFunction.invoke()
            Constant.KEYCODE_R3, Input.Keys.SPACE -> sortStorageFunction.invoke()
            Constant.KEYCODE_SELECT, Input.Keys.T -> toggleTooltipFunction.invoke()
            Constant.KEYCODE_L3, Input.Keys.C -> toggleCompareFunction.invoke()
        }
        return true
    }

    fun removeTriggers() {
        Controllers.removeListener(triggerAdapter)
    }

}
