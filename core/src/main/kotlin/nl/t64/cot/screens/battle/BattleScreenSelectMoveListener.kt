package nl.t64.cot.screens.battle

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe


class BattleScreenSelectMoveListener(
    private val move: (Int) -> Unit,
    private val back: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.stage.actors.items.any { it is Dialog }) return true

        when (keycode) {
            Input.Keys.UP -> event.handleUp()
            Input.Keys.DOWN -> event.handleDown()
            Input.Keys.LEFT -> event.handleLeft()
            Input.Keys.RIGHT -> event.handleRight()
            Input.Keys.ENTER -> event.handleEnter()
            Input.Keys.ESCAPE -> handleEscape(back)
        }
        return true
    }

    private fun InputEvent.handleUp() {
        playSe(AudioEvent.SE_MENU_CURSOR)

        val moveTable = this.listenerActor as Table
        val rightColumn = moveTable.children.last() as List<String>

        if (rightColumn.hasKeyboardFocus()) {
            if (rightColumn.selectedIndex == rightColumn.items.size - 1) {
                rightColumn.selectedIndex = rightColumn.items.size - 2
            }
        }
    }

    private fun InputEvent.handleDown() {
        playSe(AudioEvent.SE_MENU_CURSOR)

        val moveTable = this.listenerActor as Table
        val rightColumn = moveTable.children.last() as List<String>

        if (rightColumn.hasKeyboardFocus()) {
            if (rightColumn.selectedIndex == rightColumn.items.size - 1) {
                rightColumn.selectedIndex = 0
            }
        }
    }

    private fun InputEvent.handleLeft() {
        val moveTable = this.listenerActor as Table
        val secondToLast = moveTable.children.size - 2
        val leftColumn = moveTable.children[secondToLast] as List<String>
        val rightColumn = moveTable.children.last() as List<String>

        if (rightColumn.hasKeyboardFocus()) {
            playSe(AudioEvent.SE_MENU_CURSOR)
            this.stage.keyboardFocus = leftColumn
            leftColumn.selectedIndex = rightColumn.selectedIndex
            rightColumn.selectedIndex = -1
        }
    }

    private fun InputEvent.handleRight() {
        val moveTable = this.listenerActor as Table
        val secondToLast = moveTable.children.size - 2
        val leftColumn = moveTable.children[secondToLast] as List<String>
        val rightColumn = moveTable.children.last() as List<String>

        if (leftColumn.hasKeyboardFocus() && leftColumn.selected != "Back") {
            playSe(AudioEvent.SE_MENU_CURSOR)
            this.stage.keyboardFocus = rightColumn
            rightColumn.selectedIndex = leftColumn.selectedIndex
            leftColumn.selectedIndex = -1
        }
    }

    private fun InputEvent.handleEnter() {
        val moveTable = this.listenerActor as Table
        val secondToLast = moveTable.children.size - 2
        val leftColumn = moveTable.children[secondToLast] as List<String>
        val rightColumn = moveTable.children.last() as List<String>

        val selected = (if (leftColumn.hasKeyboardFocus()) leftColumn.selected else rightColumn.selected) ?: return
        when (selected) {
            "Back" -> handleEscape(back)
            else -> move.invoke(selected.toInt() - 1)
        }
    }

}
