package nl.t64.cot.screens.battle

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Dialog


internal class BattleScreenListener(
    private val winBattle: () -> Unit,
    private val fleeBattle: () -> Unit,
    private val killHero: (Int) -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.stage.actors.items.any { it is Dialog }) return true

        when (keycode) {
            Input.Keys.W -> winBattle.invoke()
            Input.Keys.F -> fleeBattle.invoke()
            Input.Keys.NUM_1 -> killHero.invoke(1)
            Input.Keys.NUM_2 -> killHero.invoke(2)
            Input.Keys.NUM_3 -> killHero.invoke(3)
            Input.Keys.NUM_4 -> killHero.invoke(4)
            Input.Keys.NUM_5 -> killHero.invoke(5)
            Input.Keys.NUM_6 -> killHero.invoke(6)
        }
        return true
    }

}
