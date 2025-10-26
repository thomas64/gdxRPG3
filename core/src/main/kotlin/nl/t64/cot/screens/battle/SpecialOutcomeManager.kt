package nl.t64.cot.screens.battle

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.Utils
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.SpecialAction


private const val DEFAULT_FLOATING_NUMBER_DELAY = 1.2f

class SpecialOutcomeManager(
    private val battleFieldTable: () -> Table,
    private val setDelayingTurn: (Boolean) -> Unit
) {

    fun specialConfirmed(specialAction: SpecialAction) {
        val specialData: AttackData = specialAction.handle().single()
        setDelayingTurn.invoke(true)
        Utils.runWithDelay(0.5f) {
            if (specialData.isHeal) {
                FloatingNumberEffect(battleFieldTable.invoke(), specialData.target, specialData.castMessage, Color.GREEN).floatDown()
                playSe(AudioEvent.SE_POTION)
            } else {
                FloatingNumberEffect(battleFieldTable.invoke(), specialData.target, specialData.castMessage, Color.YELLOW).floatDown()
                BlinkEffect(battleFieldTable.invoke(), specialData.target, Color.CYAN).start()
                playSe(AudioEvent.SE_CAST_BUFF)
            }
            Utils.runWithDelay(DEFAULT_FLOATING_NUMBER_DELAY) {
                setDelayingTurn.invoke(false)
            }
        }
    }

}
