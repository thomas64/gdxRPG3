package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import nl.t64.cot.constants.Constant


private const val REQUIRED_PRESS_DURATION = 1f

internal class CutSceneListener(
    private val closeCutscene: () -> Unit
) : InputListener() {

    private var isKeyPressed: Boolean = false
    private var keyDownTime: Float = 0f

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.ESCAPE, Constant.KEYCODE_START -> {
                isKeyPressed = true
            }
        }
        return true
    }

    override fun keyUp(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.ESCAPE, Constant.KEYCODE_START -> {
                isKeyPressed = false
                keyDownTime = 0f
            }
        }
        return true
    }

    fun update(dt: Float) {
        if (isKeyPressed) {
            keyDownTime += dt
            if (keyDownTime > REQUIRED_PRESS_DURATION) {
                isKeyPressed = false
                keyDownTime = 0f
                closeCutscene.invoke()
            }
        }
    }
}
