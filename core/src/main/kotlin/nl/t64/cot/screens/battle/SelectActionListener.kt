package nl.t64.cot.screens.battle

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.List
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.Constant
import com.badlogic.gdx.scenes.scene2d.ui.List as GdxList


class SelectActionListener(
    private val winBattle: () -> Unit,
    private val pauseMenu: () -> Unit,
    private val selectAttack: () -> Unit,
    private val selectMove: () -> Unit,
    private val selectPotion: () -> Unit,
    private val selectWeapon: () -> Unit,
    private val rest: () -> Unit,
    private val selectPreview: () -> Unit,
    private val inventoryScreen: () -> Unit,
    private val endTurn: () -> Unit,
    private val fleeBattle: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.stage.actors.items.any { it is Dialog }) {
            event.dontLoseFocusAfterEsc()
            return true
        }

        when (keycode) {
            Constant.KEYCODE_START, Input.Keys.ESCAPE -> event.handlePause()
            Input.Keys.UP -> event.selectPreviousNonGrayOption()
            Input.Keys.DOWN -> event.selectNextNonGrayOption()
            Constant.KEYCODE_RIGHT -> event.dontLoseFocusAfterEsc()
            Constant.KEYCODE_BOTTOM, Input.Keys.ENTER, Input.Keys.A -> event.handleEnter()
            Input.Keys.W -> handleWin()
        }
        return true
    }

    private fun InputEvent.handlePause() {
        this.dontLoseFocusAfterEsc()
        pauseMenu.invoke()
    }

    private fun InputEvent.selectPreviousNonGrayOption() {
        var selected: String? = this.getSelected<String>()
        while (selected != null && selected.startsWith("[GRAY]")) {
            selected = getPreviousOption(this)
        }
        playSe(AudioEvent.SE_MENU_CURSOR)
    }

    private fun InputEvent.selectNextNonGrayOption() {
        var selected: String? = this.getSelected<String>()
        while (selected != null && selected.startsWith("[GRAY]")) {
            selected = getNextOption(this)
        }
        playSe(AudioEvent.SE_MENU_CURSOR)
    }

    private fun getPreviousOption(event: InputEvent): String? {
        val list: List<String> = event.target as? GdxList<String> ?: return null
        val currentIndex: Int = list.selectedIndex
        val previousIndex: Int = if (currentIndex > 0) currentIndex - 1 else list.items.size - 1
        list.selectedIndex = previousIndex
        return list.selected
    }

    private fun getNextOption(event: InputEvent): String? {
        val list: List<String> = event.target as? GdxList<String> ?: return null
        val currentIndex: Int = list.selectedIndex
        val nextIndex: Int = if (currentIndex < list.items.size - 1) currentIndex + 1 else 0
        list.selectedIndex = nextIndex
        return list.selected
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
                "Attack" in selected -> selectAttack()
                "Move" in selected -> selectMove()
                "Potion" in selected -> selectPotion()
                "Switch" in selected -> selectWeapon()
                "Preview" in selected -> selectPreview()
                "Inventory" in selected -> inventoryScreen()
                "Flee" in selected -> fleeBattle()
                "Rest" in selected -> rest()
                "End" in selected -> endTurn()
            }
        }
    }

    private fun handleWin() {
        playSe(AudioEvent.SE_MENU_ERROR)
        winBattle.invoke()
    }

}
