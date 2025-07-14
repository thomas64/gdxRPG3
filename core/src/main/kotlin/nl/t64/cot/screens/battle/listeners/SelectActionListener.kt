package nl.t64.cot.screens.battle.listeners

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.Constant


class SelectActionListener(
    private val winBattle: () -> Unit,
    private val pauseMenu: () -> Unit,
    private val selectAttack: () -> Unit,
    private val selectSpecial: () -> Unit,
    private val selectMove: () -> Unit,
    private val selectPotion: () -> Unit,
    private val selectEquipment: () -> Unit,
    private val selectPreview: () -> Unit,
    private val selectParty: () -> Unit,
    private val selectFlee: () -> Unit,
    private val selectDelayTurn: () -> Unit,
    private val selectRest: () -> Unit,
    private val selectEndTurn: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.isDialogOpen()) {
            event.dontLoseFocusAfterEsc()
            return true
        }

        when (keycode) {
            Constant.KEYCODE_START, Input.Keys.ESCAPE -> event.handlePause(pauseMenu)
            Input.Keys.UP -> event.selectPreviousNonGrayOption<String>()
            Input.Keys.DOWN -> event.selectNextNonGrayOption<String>()
            Constant.KEYCODE_RIGHT -> event.dontLoseFocusAfterEsc()
            Constant.KEYCODE_BOTTOM, Input.Keys.ENTER, Input.Keys.A -> event.handleEnter()
            Input.Keys.W -> handleWin(winBattle)
        }
        return true
    }

    private fun InputEvent.handleEnter() {
        getSelected<String>()?.let { selected ->
            if ("Attack" in selected
                || "Special" in selected
                || "Move" in selected
                || "Equipment" in selected
                || "Preview" in selected
                || "Potion" in selected
                || "End" in selected
            ) {
                playSe(AudioEvent.SE_MENU_CONFIRM)
            }
            when {
                "Attack" in selected -> selectAttack.invoke()
                "Special" in selected -> selectSpecial.invoke()
                "Move" in selected -> selectMove.invoke()
                "Equipment" in selected -> selectEquipment.invoke()
                "Preview" in selected -> selectPreview.invoke()
                "Potion" in selected -> selectPotion.invoke()
                "Party" in selected -> selectParty.invoke()
                "Flee" in selected -> selectFlee.invoke()
                "Delay" in selected -> selectDelayTurn.invoke()
                "Rest" in selected -> selectRest.invoke()
                "End" in selected -> selectEndTurn.invoke()
            }
        }
    }

}
