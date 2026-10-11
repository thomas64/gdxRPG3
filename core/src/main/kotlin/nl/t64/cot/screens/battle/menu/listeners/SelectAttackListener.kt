package nl.t64.cot.screens.battle.menu.listeners

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.constants.Constant


class SelectAttackListener(
    private val attack: (BattleAbilityItem) -> Unit,
    private val back: () -> Unit
) : BattleMenuListener() {

    override fun keyDownInMenu(event: InputEvent, keycode: Int) {
        when (keycode) {
            Input.Keys.UP -> event.selectPreviousNonGrayOption<BattleAbilityItem>()
            Input.Keys.DOWN -> event.selectNextNonGrayOption<BattleAbilityItem>()
            Constant.KEYCODE_BOTTOM, Input.Keys.ENTER, Input.Keys.A -> event.handleEnter()
            Constant.KEYCODE_RIGHT, Input.Keys.ESCAPE -> handleEscape(back)
        }
    }

    override fun keyDownWhileDialogOpen(event: InputEvent, keycode: Int) {
        event.possibleSelectNonGrayOption<BattleAbilityItem>(keycode)
    }

    private fun InputEvent.handleEnter() {
        val selected: BattleAbilityItem = getSelected() ?: return
        when {
            selected.name == "Back" -> handleEscape(back)
            selected.isUsable() -> attack.invoke(selected)
        }
    }

}
