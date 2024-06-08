package nl.t64.cot.screens.battle

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe


fun <T> InputEvent.getSelected(): T? {
    return getButtonTable<T>().selected
}

fun <T> InputEvent.getButtonTable(): List<T> {
    return (this.listenerActor as Table).children.last() as List<T>
}

fun handleEscape(backFunction: () -> Unit) {
    playSe(AudioEvent.SE_MENU_BACK)
    backFunction.invoke()
}
