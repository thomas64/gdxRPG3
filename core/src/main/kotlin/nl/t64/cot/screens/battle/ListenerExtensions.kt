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

fun <T> InputEvent.selectPreviousNonGrayOption() {
    var selected: T? = this.getSelected<T>()
    while (selected != null && selected.toString().startsWith("[GRAY]")) {
        selected = this.getPreviousOption()
    }
    playSe(AudioEvent.SE_MENU_CURSOR)
}

fun <T> InputEvent.selectNextNonGrayOption() {
    var selected: T? = this.getSelected<T>()
    while (selected != null && selected.toString().startsWith("[GRAY]")) {
        selected = this.getNextOption()
    }
    playSe(AudioEvent.SE_MENU_CURSOR)
}

private fun <T> InputEvent.getPreviousOption(): T? {
    val list: List<T> = this.target as? List<T> ?: return null
    val currentIndex: Int = list.selectedIndex
    val previousIndex: Int = if (currentIndex > 0) currentIndex - 1 else list.items.size - 1
    list.selectedIndex = previousIndex
    return list.selected
}

private fun <T> InputEvent.getNextOption(): T? {
    val list: List<T> = this.target as? List<T> ?: return null
    val currentIndex: Int = list.selectedIndex
    val nextIndex: Int = if (currentIndex < list.items.size - 1) currentIndex + 1 else 0
    list.selectedIndex = nextIndex
    return list.selected
}

fun InputEvent.dontLoseFocusAfterEsc() {
    this.stage.keyboardFocus = this.getButtonTable<String>()
}

fun <T> InputEvent.getSelected(): T? {
    return this.getButtonTable<T>().selected
}

private fun <T> InputEvent.getButtonTable(): List<T> {
    return (this.listenerActor as Table).children.last() as List<T>
}
