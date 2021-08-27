package nl.t64.cot.screens.loot

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import nl.t64.cot.constants.Constant


internal class LootSlotsTableListener(
    private val closeScreenFunction: () -> Unit,
    private val toggleTooltip: () -> Unit,
    private val takeItemFunction: () -> Unit,
    private val selectNewSlot: (Int) -> Unit,
    private val slotsInRow: Int
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Constant.KEYCODE_RIGHT, Input.Keys.ESCAPE -> closeScreenFunction.invoke()
            Constant.KEYCODE_SELECT, Input.Keys.T -> toggleTooltip.invoke()
            Constant.KEYCODE_BOTTOM, Input.Keys.ENTER, Input.Keys.A -> takeItemFunction.invoke()
            Input.Keys.UP -> selectNewSlot.invoke(-slotsInRow)
            Input.Keys.DOWN -> selectNewSlot.invoke(slotsInRow)
            Input.Keys.LEFT -> selectNewSlot.invoke(-1)
            Input.Keys.RIGHT -> selectNewSlot.invoke(1)
        }
        return true
    }

}
