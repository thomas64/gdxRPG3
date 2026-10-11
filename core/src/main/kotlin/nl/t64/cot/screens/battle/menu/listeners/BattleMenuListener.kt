package nl.t64.cot.screens.battle.menu.listeners

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener


abstract class BattleMenuListener : InputListener() {

    final override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.isDialogOpen()) {
            event.dontLoseFocusAfterEsc()
            keyDownWhileDialogOpen(event, keycode)
        } else {
            keyDownInMenu(event, keycode)
        }
        return true
    }

    protected abstract fun keyDownInMenu(event: InputEvent, keycode: Int)

    protected open fun keyDownWhileDialogOpen(event: InputEvent, keycode: Int) {}

}
