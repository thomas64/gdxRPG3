package nl.t64.cot.screens.battle

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.Table


fun InputEvent.getSelected(): String? {
    return getButtonTable().selected
}

fun InputEvent.getButtonTable(): List<String> {
    return (this.listenerActor as Table).children.last() as List<String>
}
