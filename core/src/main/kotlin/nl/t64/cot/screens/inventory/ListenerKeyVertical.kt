package nl.t64.cot.screens.inventory

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.utils.Timer


private const val INITIAL_REPEAT_DELAY: Float = 0.4f
private const val REPEAT_INTERVAL: Float = 0.08f

internal class ListenerKeyVertical(
    private val updateIndexFunction: (Int) -> Unit
) : InputListener() {

    private var repeatTask: Timer.Task? = null

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (repeatTask != null) return true

        val deltaIndex: Int = getDeltaForKey(keycode) ?: return true
        updateIndexFunction.invoke(deltaIndex)
        startRepeatTask(deltaIndex)
        return true
    }

    override fun keyUp(event: InputEvent, keycode: Int): Boolean {
        stopRepeatTask()
        return true
    }

    fun cleanup() {
        stopRepeatTask()
    }

    private fun getDeltaForKey(keycode: Int): Int? {
        return when (keycode) {
            Input.Keys.UP -> -1
            Input.Keys.DOWN -> 1
            else -> null
        }
    }

    private fun startRepeatTask(deltaIndex: Int) {
        repeatTask = object : Timer.Task() {
            override fun run() {
                updateIndexFunction.invoke(deltaIndex)
            }
        }
        Timer.schedule(repeatTask, INITIAL_REPEAT_DELAY, REPEAT_INTERVAL)
    }

    private fun stopRepeatTask() {
        repeatTask?.cancel()
        repeatTask = null
    }

}
