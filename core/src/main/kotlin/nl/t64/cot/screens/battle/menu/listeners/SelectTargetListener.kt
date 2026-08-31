package nl.t64.cot.screens.battle.menu.listeners

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.constants.Constant


class SelectTargetListener(
    private val enemy: (BattleAbilityItem, String) -> Unit,
    private val back: () -> Unit
) : InputListener() {
    private lateinit var selectedAttack: BattleAbilityItem

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.isDialogOpen()) {
            event.dontLoseFocusAfterEsc()
            return true
        }

        when (keycode) {
            Input.Keys.UP -> playSe(AudioEvent.SE_MENU_CURSOR)
            Input.Keys.DOWN -> playSe(AudioEvent.SE_MENU_CURSOR)
            Constant.KEYCODE_BOTTOM, Input.Keys.ENTER, Input.Keys.A -> event.handleEnter()
            Constant.KEYCODE_RIGHT, Input.Keys.ESCAPE -> handleEscape(back)
        }
        return true
    }

    fun setSelectedAttack(attack: BattleAbilityItem) {
        selectedAttack = attack
    }

    private fun InputEvent.handleEnter() {
        val selectedTarget: String = getSelected() ?: return
        when (selectedTarget) {
            "Back" -> handleEscape(back)
            else -> enemy.invoke(selectedAttack, selectedTarget)
        }
    }

}
