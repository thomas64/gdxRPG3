package nl.t64.cot.screens.battle

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.Constant

class SelectPreBattleListener(
    private val winBattle: () -> Unit,
    private val pauseMenu: () -> Unit,
    private val reposition: () -> Unit,
    private val inventoryScreen: () -> Unit,
    private val startBattle: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.isDialogOpen()) {
            event.dontLoseFocusAfterEsc()
            return true
        }

        when (keycode) {
            Constant.KEYCODE_START, Input.Keys.ESCAPE -> event.handlePause(pauseMenu)
            Input.Keys.UP -> playSe(AudioEvent.SE_MENU_CURSOR)
            Input.Keys.DOWN -> playSe(AudioEvent.SE_MENU_CURSOR)
            Constant.KEYCODE_RIGHT -> event.dontLoseFocusAfterEsc()
            Constant.KEYCODE_BOTTOM, Input.Keys.ENTER, Input.Keys.A -> event.handleEnter()
            Input.Keys.W -> handleWin(winBattle)
        }
        return true
    }

    private fun InputEvent.handleEnter() {
        getSelected<String>()?.let { selected ->
            when {
                "Reposition" in selected -> {
                    playSe(AudioEvent.SE_MENU_CONFIRM)
                    reposition.invoke()
                }
                "Select" in selected -> inventoryScreen.invoke()
                "Start" in selected -> {
                    playSe(AudioEvent.SE_MENU_CONFIRM)
                    startBattle.invoke()
                }
            }
        }
    }

}
