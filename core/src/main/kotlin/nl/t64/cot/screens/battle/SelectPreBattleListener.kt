package nl.t64.cot.screens.battle

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.Constant

class SelectPreBattleListener(
    private val winBattle: () -> Unit,
    private val pauseMenu: () -> Unit,
    private val inventoryScreen: () -> Unit,
    private val startBattle: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.stage.actors.items.any { it is Dialog }) {
            event.dontLoseFocusAfterEsc()
            return true
        }

        when (keycode) {
            Constant.KEYCODE_START, Input.Keys.ESCAPE -> event.handlePause()
            Input.Keys.UP -> playSe(AudioEvent.SE_MENU_CURSOR)
            Input.Keys.DOWN -> playSe(AudioEvent.SE_MENU_CURSOR)
            Constant.KEYCODE_RIGHT -> event.dontLoseFocusAfterEsc()
            Constant.KEYCODE_BOTTOM, Input.Keys.ENTER, Input.Keys.A -> event.handleEnter()
            Input.Keys.W -> handleWin()
        }
        return true
    }

    private fun InputEvent.handlePause() {
        this.dontLoseFocusAfterEsc()
        pauseMenu.invoke()
    }

    private fun handleWin() {
        playSe(AudioEvent.SE_MENU_ERROR)
        winBattle.invoke()
    }

    private fun InputEvent.handleEnter() {
        getSelected<String>()?.let { selected ->
            when {
                "Select" in selected -> inventoryScreen()
                "Start" in selected -> {
                    playSe(AudioEvent.SE_MENU_CONFIRM)
                    startBattle()
                }
            }
        }
    }

}
