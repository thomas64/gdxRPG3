package nl.t64.cot.screens.inventory.inventoryslot

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.utils.Timer


private const val INITIAL_REPEAT_DELAY: Float = 0.4f
private const val REPEAT_INTERVAL: Float = 0.08f

class InventorySlotsTableListener(
    private val selectNewSlot: (Int) -> Unit,
    private val slotsPerRow: Int
) : InputListener() {

    private var repeatTask: Timer.Task? = null

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (repeatTask != null) return true

        val deltaSlotIndex: Int = getDeltaForKey(keycode) ?: return true
        selectNewSlot.invoke(deltaSlotIndex)
        startRepeatTask(deltaSlotIndex)
        return true
    }

    override fun keyUp(event: InputEvent, keycode: Int): Boolean {
        stopRepeatTask()
        return true
    }

    private fun getDeltaForKey(keycode: Int): Int? {
        return when (keycode) {
            Input.Keys.UP -> -slotsPerRow
            Input.Keys.DOWN -> slotsPerRow
            Input.Keys.LEFT -> -1
            Input.Keys.RIGHT -> 1
            else -> null
        }
    }

    private fun startRepeatTask(deltaSlotIndex: Int) {
        repeatTask = object : Timer.Task() {
            override fun run() {
                selectNewSlot.invoke(deltaSlotIndex)
            }
        }
        Timer.schedule(repeatTask, INITIAL_REPEAT_DELAY, REPEAT_INTERVAL)
    }

    private fun stopRepeatTask() {
        repeatTask?.cancel()
        repeatTask = null
    }

}
