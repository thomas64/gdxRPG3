package nl.t64.cot.screens.inventory.inventoryslot

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener


class InventorySlotsTableListener(
    private val selectNewSlot: (Int) -> Unit,
    private val slotsInRow: Int
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.UP -> selectNewSlot.invoke(-slotsInRow)
            Input.Keys.DOWN -> selectNewSlot.invoke(slotsInRow)
            Input.Keys.LEFT -> selectNewSlot.invoke(-1)
            Input.Keys.RIGHT -> selectNewSlot.invoke(1)
        }
        return true
    }

}
