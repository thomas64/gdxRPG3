package nl.t64.cot.screens.battle.menu.listeners

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.Constant


class SelectHeroListener(
    private val hero: (String) -> Unit,
    private val back: () -> Unit
) : BattleMenuListener() {

    override fun keyDownInMenu(event: InputEvent, keycode: Int) {
        when (keycode) {
            Input.Keys.UP -> playSe(AudioEvent.SE_MENU_CURSOR)
            Input.Keys.DOWN -> playSe(AudioEvent.SE_MENU_CURSOR)
            Constant.KEYCODE_BOTTOM, Input.Keys.ENTER, Input.Keys.A -> event.handleEnter()
            Constant.KEYCODE_RIGHT, Input.Keys.ESCAPE -> handleEscape(back)
        }
    }

    private fun InputEvent.handleEnter() {
        val selectedHero: String = getSelected() ?: return
        when (selectedHero) {
            "Back" -> handleEscape(back)
            else -> hero.invoke(selectedHero)
        }
    }

}
