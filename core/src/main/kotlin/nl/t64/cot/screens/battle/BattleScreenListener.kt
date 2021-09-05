package nl.t64.cot.screens.battle

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Dialog


internal class BattleScreenListener(
    private val winBattle: () -> Unit,
    private val fleeBattle: () -> Unit,
    private val damageHero: (Int) -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.stage.actors.items.any { it is Dialog }) return true

        when (keycode) {
            Input.Keys.W -> winBattle.invoke()
            Input.Keys.F -> fleeBattle.invoke()
            Input.Keys.NUM_1 -> damageHero.invoke(1)
            Input.Keys.NUM_2 -> damageHero.invoke(2)
            Input.Keys.NUM_3 -> damageHero.invoke(3)
            Input.Keys.NUM_4 -> damageHero.invoke(4)
            Input.Keys.NUM_5 -> damageHero.invoke(5)
            Input.Keys.NUM_6 -> damageHero.invoke(6)
        }
        return true
    }

}
