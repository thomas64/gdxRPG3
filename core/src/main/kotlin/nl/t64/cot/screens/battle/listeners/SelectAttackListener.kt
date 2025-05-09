package nl.t64.cot.screens.battle.listeners

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.constants.Constant


class SelectAttackListener(
    private val attack: (BattleAbilityItem) -> Unit,
    private val back: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.isDialogOpen()) return true

        when (keycode) {
            Input.Keys.UP -> event.selectPreviousNonGrayOption<BattleAbilityItem>()
            Input.Keys.DOWN -> event.selectNextNonGrayOption<BattleAbilityItem>()
            Constant.KEYCODE_BOTTOM, Input.Keys.ENTER, Input.Keys.A -> event.handleEnter()
            Constant.KEYCODE_RIGHT, Input.Keys.ESCAPE -> handleEscape(back)
        }
        return true
    }

    private fun InputEvent.handleEnter() {
        val selected: BattleAbilityItem = getSelected() ?: return
        when {
            selected.name == "Back" -> handleEscape(back)
            else -> {
                playSe(AudioEvent.SE_MENU_CONFIRM)
                attack.invoke(selected)
            }
        }
    }

}
