package nl.t64.cot.screens.battle

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
    private val selectMove: () -> Unit,
    private val selectPotion: () -> Unit,
    private val selectWeapon: () -> Unit,
    private val selectPreview: () -> Unit,
    private val inventoryScreen: () -> Unit,
    private val fleeBattle: () -> Unit,
    private val delayTurn: () -> Unit,
    private val rest: () -> Unit,
    private val endTurn: () -> Unit
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
                || "Move" in selected
                || "Potion" in selected
                || "Switch" in selected
                || "Preview" in selected
                || "End" in selected
            ) {
                playSe(AudioEvent.SE_MENU_CONFIRM)
            }
            when {
                "Attack" in selected -> selectAttack.invoke()
                "Move" in selected -> selectMove.invoke()
                "Potion" in selected -> selectPotion.invoke()
                "Switch" in selected -> selectWeapon.invoke()
                "Preview" in selected -> selectPreview.invoke()
                "Inventory" in selected -> inventoryScreen.invoke()
                "Flee" in selected -> fleeBattle.invoke()
                "Delay" in selected -> delayTurn.invoke()
                "Rest" in selected -> rest.invoke()
                "End" in selected -> endTurn.invoke()
            }
        }
    }

}
