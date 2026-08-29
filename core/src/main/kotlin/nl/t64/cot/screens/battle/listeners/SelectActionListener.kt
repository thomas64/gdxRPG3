package nl.t64.cot.screens.battle.listeners

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.battle.BattleMenuOption


class SelectActionListener(
    private val winBattle: () -> Unit,
    private val pauseMenu: () -> Unit,
    private val selectParty: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.isDialogOpen()) {
            event.dontLoseFocusAfterEsc()
            return true
        }

        when (keycode) {
            Constant.KEYCODE_START, Input.Keys.ESCAPE -> event.handlePause(pauseMenu)
            Input.Keys.UP -> event.selectPreviousNonGrayOption<BattleMenuOption>()
            Input.Keys.DOWN -> event.selectNextNonGrayOption<BattleMenuOption>()
            Constant.KEYCODE_RIGHT -> event.dontLoseFocusAfterEsc()
            Constant.KEYCODE_BOTTOM, Input.Keys.ENTER, Input.Keys.A -> event.handleEnter()
            Constant.KEYCODE_TOP, Input.Keys.I -> selectParty.invoke()
            Input.Keys.W -> handleWin(winBattle)
        }
        return true
    }

    private fun InputEvent.handleEnter() {
        getSelected<BattleMenuOption>()?.select()
    }

}
