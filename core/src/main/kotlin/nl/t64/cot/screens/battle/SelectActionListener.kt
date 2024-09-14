package nl.t64.cot.screens.battle

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.Constant


class SelectActionListener(
    private val winBattle: () -> Unit,
    private val selectAttack: () -> Unit,
    private val selectMove: () -> Unit,
    private val selectPotion: () -> Unit,
    private val selectWeapon: () -> Unit,
    private val rest: () -> Unit,
    private val selectPreview: () -> Unit,
    private val inventoryScreen: () -> Unit,
    private val endTurn: () -> Unit,
    private val fleeBattle: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.stage.actors.items.any { it is Dialog }) return true

        when (keycode) {
            Input.Keys.UP -> playSe(AudioEvent.SE_MENU_CURSOR)
            Input.Keys.DOWN -> playSe(AudioEvent.SE_MENU_CURSOR)
            Constant.KEYCODE_RIGHT, Input.Keys.ESCAPE -> event.dontLoseFocusAfterEsc()
            Constant.KEYCODE_BOTTOM, Input.Keys.ENTER, Input.Keys.A -> event.handleEnter()
            Input.Keys.W -> handleWin()
        }
        return true
    }

    private fun InputEvent.dontLoseFocusAfterEsc() {
        playSe(AudioEvent.SE_MENU_ERROR)
        this.stage.keyboardFocus = getButtonTable<String>()
    }

    private fun InputEvent.handleEnter() {
        getSelected<String>()?.let { selected ->
            playSe(AudioEvent.SE_MENU_CONFIRM)
            when {
                "Attack" in selected -> selectAttack()
                "Move" in selected -> selectMove()
                "Potion" in selected -> selectPotion()
                "Switch" in selected -> selectWeapon()
                "Rest" in selected -> rest()
                "Preview" in selected -> selectPreview()
                "Inventory" in selected -> inventoryScreen()
                "End" in selected -> endTurn()
                "Flee" in selected -> fleeBattle()
            }
        }
    }

    private fun handleWin() {
        playSe(AudioEvent.SE_MENU_ERROR)
        winBattle.invoke()
    }

}
