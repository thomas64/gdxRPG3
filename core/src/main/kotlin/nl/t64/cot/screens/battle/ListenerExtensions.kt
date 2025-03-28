package nl.t64.cot.screens.battle

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe


fun InputEvent.isDialogOpen(): Boolean {
    return this.stage.actors.items.any { it is Dialog }
}

fun InputEvent.dontLoseFocusAfterEsc() {
    this.stage.keyboardFocus = getButtonTable<String>()
}

fun <T> InputEvent.getSelected(): T? {
    return getButtonTable<T>().selected
}

fun handleEnter(confirmFunction: () -> Unit) {
    playSe(AudioEvent.SE_MENU_CONFIRM)
    confirmFunction.invoke()
}

fun handleEscape(backFunction: () -> Unit) {
    playSe(AudioEvent.SE_MENU_BACK)
    backFunction.invoke()
}

fun InputEvent.handlePause(pauseMenu: () -> Unit) {
    this.dontLoseFocusAfterEsc()
    pauseMenu.invoke()
}

fun handleWin(winBattle: () -> Unit) {
    playSe(AudioEvent.SE_MENU_ERROR)
    winBattle.invoke()
}

private fun <T> InputEvent.getButtonTable(): List<T> {
    return (this.listenerActor as Table).children.last() as List<T>
}
