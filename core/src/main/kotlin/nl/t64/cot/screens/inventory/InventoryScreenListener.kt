package nl.t64.cot.screens.inventory

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import nl.t64.cot.constants.Constant


internal class InventoryScreenListener(
    private val closeScreenFunction: () -> Unit,
    private val actionFunction: () -> Unit,
    private val previousHeroFunction: () -> Unit,
    private val nextHeroFunction: () -> Unit,
    private val previousTableFunction: () -> Unit,
    private val nextTableFunction: () -> Unit,
    private val dismissHeroFunction: () -> Unit,
    private val sortInventoryFunction: () -> Unit,
    private val toggleTooltipFunction: () -> Unit,
    private val toggleCompareFunction: () -> Unit,
    private val cheatAddGoldFunction: () -> Unit,
    private val cheatRemoveGoldFunction: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.stage.actors.items.any { it is Dialog }) return true

        when (keycode) {
            Constant.KEYCODE_RIGHT, Input.Keys.I, Input.Keys.ESCAPE -> closeScreenFunction.invoke()
            Constant.KEYCODE_BOTTOM, Input.Keys.A, Input.Keys.ENTER -> actionFunction.invoke()
            Constant.KEYCODE_L1, Input.Keys.Q -> previousHeroFunction.invoke()
            Constant.KEYCODE_R1, Input.Keys.W -> nextHeroFunction.invoke()
            Constant.KEYCODE_R3_L, Input.Keys.Z -> previousTableFunction.invoke()
            Constant.KEYCODE_R3_R, Input.Keys.X -> nextTableFunction.invoke()
            Constant.KEYCODE_TOP, Input.Keys.D -> dismissHeroFunction.invoke()
            Constant.KEYCODE_START, Input.Keys.SPACE -> sortInventoryFunction.invoke()
            Constant.KEYCODE_SELECT, Input.Keys.T -> toggleTooltipFunction.invoke()
            Constant.KEYCODE_L3, Input.Keys.C -> toggleCompareFunction.invoke()
            Input.Keys.NUM_0 -> cheatAddGoldFunction.invoke()
            Input.Keys.NUM_9 -> cheatRemoveGoldFunction.invoke()
        }
        return true
    }

}
