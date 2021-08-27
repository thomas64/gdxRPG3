package nl.t64.cot.screens.inventory.equipslot

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener


const val SIDE_STEP = 10

class EquipSlotsTableListener(
    private val trySelectNewSlot: (Int) -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.UP -> trySelectNewSlot.invoke(-1)
            Input.Keys.DOWN -> trySelectNewSlot.invoke(1)
            Input.Keys.LEFT -> trySelectNewSlot.invoke(-SIDE_STEP)
            Input.Keys.RIGHT -> trySelectNewSlot.invoke(SIDE_STEP)
        }
        return true
    }

}
