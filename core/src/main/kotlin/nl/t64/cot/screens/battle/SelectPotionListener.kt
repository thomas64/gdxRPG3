package nl.t64.cot.screens.battle

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.party.inventory.BattlePotionItem
import nl.t64.cot.constants.Constant


class SelectPotionListener(
    private val potion: (BattlePotionItem) -> Unit,
    private val back: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.stage.actors.items.any { it is Dialog }) return true

        when (keycode) {
            Input.Keys.UP -> playSe(AudioEvent.SE_MENU_CURSOR)
            Input.Keys.DOWN -> playSe(AudioEvent.SE_MENU_CURSOR)
            Constant.KEYCODE_BOTTOM ,Input.Keys.ENTER, Input.Keys.A  -> event.handleEnter()
            Constant.KEYCODE_RIGHT, Input.Keys.ESCAPE -> handleEscape(back)
        }
        return true
    }

    private fun InputEvent.handleEnter() {
        val selected: BattlePotionItem = getSelected() ?: return
        when {
            selected.name == "Back" -> handleEscape(back)
            else -> {
                playSe(AudioEvent.SE_MENU_CONFIRM)
                potion.invoke(selected)
            }
        }
    }

}
