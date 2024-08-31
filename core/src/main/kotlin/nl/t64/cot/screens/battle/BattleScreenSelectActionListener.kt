package nl.t64.cot.screens.battle

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe


class BattleScreenSelectActionListener(
    private val winBattle: () -> Unit,
    private val selectCalculate: () -> Unit,
    private val selectMove: () -> Unit,
    private val selectAttack: () -> Unit,
    private val selectPotion: () -> Unit,
    private val selectWeapon: () -> Unit,
    private val rest: () -> Unit,
    private val endTurn: () -> Unit,
    private val fleeBattle: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.stage.actors.items.any { it is Dialog }) return true

        when (keycode) {
            Input.Keys.UP -> playSe(AudioEvent.SE_MENU_CURSOR)
            Input.Keys.DOWN -> playSe(AudioEvent.SE_MENU_CURSOR)
            Input.Keys.ESCAPE -> event.dontLoseFocusAfterEsc()
            Input.Keys.ENTER -> event.handleEnter()
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
                "Calculate" in selected -> selectCalculate()
                "Move" in selected -> selectMove()
                "Attack" in selected -> selectAttack()
                "Potion" in selected -> selectPotion()
                "Switch" in selected -> selectWeapon()
                "Rest" in selected -> rest()
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
