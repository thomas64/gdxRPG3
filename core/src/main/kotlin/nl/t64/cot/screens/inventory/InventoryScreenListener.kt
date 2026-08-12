package nl.t64.cot.screens.inventory

import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import nl.t64.cot.Utils
import nl.t64.cot.constants.Constant
import nl.t64.cot.input.AnalogTriggerAdapter


internal class InventoryScreenListener(
    stage: Stage,
    private val closeScreenFunction: () -> Unit,
    private val questLogScreenFunction: () -> Unit,
    private val actionFunction: () -> Unit,
    private val previousHeroFunction: () -> Unit,
    private val nextHeroFunction: () -> Unit,
    private val moveHeroLeftFunction: () -> Unit,
    private val moveHeroRightFunction: () -> Unit,
    private val previousTableFunction: () -> Unit,
    private val nextTableFunction: () -> Unit,
    private val dropItemFunction: () -> Unit,
    private val dismissHeroFunction: () -> Unit,
    private val sortInventoryFunction: () -> Unit,
    private val toggleTooltipFunction: () -> Unit,
    private val toggleCompareFunction: () -> Unit,
    private val cheatAddGoldFunction: () -> Unit,
    private val cheatRemoveGoldFunction: () -> Unit
) : InputListener() {

    private val triggerAdapter = AnalogTriggerAdapter(stage, previousTableFunction, nextTableFunction)
    private var isModifierDown: Boolean = false
    private var isModifierUsedForCombo: Boolean = false

    init {
        Utils.runWithDelay(Constant.FADE_DURATION) {
            Controllers.addListener(triggerAdapter)
        }
    }

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (hasOpenDialog(event)) return true

        when (keycode) {
            Constant.KEYCODE_RIGHT, Input.Keys.I, Input.Keys.ESCAPE -> closeScreenFunction.invoke()
            Constant.KEYCODE_LEFT, Input.Keys.L -> questLogScreenFunction.invoke()
            Constant.KEYCODE_BOTTOM, Input.Keys.A, Input.Keys.ENTER -> actionFunction.invoke()
            Constant.KEYCODE_L1, Input.Keys.Q -> selectPreviousOrMoveHeroLeft()
            Constant.KEYCODE_R1, Input.Keys.W -> selectNextOrMoveHeroRight()
            Input.Keys.Z -> previousTableFunction.invoke()
            Input.Keys.X -> nextTableFunction.invoke()
            Constant.KEYCODE_TOP, Input.Keys.D -> dropItemFunction.invoke()
            Constant.KEYCODE_SELECT, Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT -> holdModifier()
            Input.Keys.F -> dismissHeroFunction.invoke()
            Constant.KEYCODE_START, Input.Keys.SPACE -> sortInventoryFunction.invoke()
            Constant.KEYCODE_L3, Input.Keys.T -> toggleTooltipFunction.invoke()
            Constant.KEYCODE_R3, Input.Keys.C -> toggleCompareFunction.invoke()
            Input.Keys.NUM_0 -> cheatAddGoldFunction.invoke()
            Input.Keys.NUM_9 -> cheatRemoveGoldFunction.invoke()
        }
        return true
    }

    override fun keyUp(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Constant.KEYCODE_SELECT -> releaseModifierAndPossibleDismissHero(event)
            Input.Keys.SHIFT_LEFT, Input.Keys.SHIFT_RIGHT -> releaseModifier()
        }
        return true
    }

    private fun hasOpenDialog(event: InputEvent): Boolean {
        return event.stage.actors.items.any { it is Dialog }
    }

    private fun selectPreviousOrMoveHeroLeft() {
        if (isModifierDown) {
            isModifierUsedForCombo = true
            moveHeroLeftFunction.invoke()
        } else {
            previousHeroFunction.invoke()
        }
    }

    private fun selectNextOrMoveHeroRight() {
        if (isModifierDown) {
            isModifierUsedForCombo = true
            moveHeroRightFunction.invoke()
        } else {
            nextHeroFunction.invoke()
        }
    }

    private fun holdModifier() {
        isModifierDown = true
    }

    private fun releaseModifierAndPossibleDismissHero(event: InputEvent) {
        val wasUsedForCombo: Boolean = isModifierUsedForCombo
        releaseModifier()
        if (!wasUsedForCombo && !hasOpenDialog(event)) {
            dismissHeroFunction.invoke()
        }
    }

    private fun releaseModifier() {
        isModifierDown = false
        isModifierUsedForCombo = false
    }

    fun removeTriggers() {
        Controllers.removeListener(triggerAdapter)
    }

}
