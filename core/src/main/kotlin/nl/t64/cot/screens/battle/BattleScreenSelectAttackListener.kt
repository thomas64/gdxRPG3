package nl.t64.cot.screens.battle

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe


class BattleScreenSelectAttackListener(
    private val attack: (String) -> Unit,
    private val back: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.stage.actors.items.any { it is Dialog }) return true

        when (keycode) {
            Input.Keys.UP -> playSe(AudioEvent.SE_MENU_CURSOR)
            Input.Keys.DOWN -> playSe(AudioEvent.SE_MENU_CURSOR)
            Input.Keys.ENTER -> event.handleEnter()
            Input.Keys.ESCAPE -> handleEscape(back)
        }
        return true
    }

    private fun InputEvent.handleEnter() {
        getSelected<String>()?.let {
            when {
                "Strike" in it -> {
                    playSe(AudioEvent.SE_MENU_CONFIRM)
                    attack("Strike")
                }
                "Back" in it -> handleEscape(back)
            }
        }
    }

}
