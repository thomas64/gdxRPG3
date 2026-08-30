package nl.t64.cot.screens.battle

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.Utils
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.SpecialAction
import nl.t64.cot.components.party.abilities.AbilityItemId


private const val DEFAULT_FLOATING_NUMBER_DELAY = 1.2f

class SpecialOutcomeManager(
    private val battleFieldTable: () -> Table,
    private val battleState: BattleState
) {

    fun specialConfirmed(specialAction: SpecialAction) {
        val specialData: AttackData = specialAction.handle().single()
        battleState.isDelayingTurn = true
        Utils.runWithDelay(0.5f) {
            when {
                specialData.isHeal -> {
                    FloatingNumberEffect(battleFieldTable.invoke(), specialData.target, specialData.castMessage, Color.GREEN).floatDown()
                    playSe(AudioEvent.SE_POTION)
                }
                specialData.perform == AbilityItemId.PERFORM_BEAUTY -> {
                    FloatingNumberEffect(battleFieldTable.invoke(), specialData.attacker, "~~+~~", Color.VIOLET).floatUp()
                    playSe(AudioEvent.SE_PERFORM_BUFF)
                }
                specialData.perform == AbilityItemId.PERFORM_CHAOS -> {
                    FloatingNumberEffect(battleFieldTable.invoke(), specialData.attacker, "~~-~~", Color.VIOLET).floatUp()
                    playSe(AudioEvent.SE_PERFORM_DEBUFF)
                }
                specialData.castMessage.startsWith('+') -> {
                    FloatingNumberEffect(battleFieldTable.invoke(), specialData.target, specialData.castMessage, Color.YELLOW).floatDown()
                    BlinkEffect(battleFieldTable.invoke(), specialData.target, Color.CYAN).start()
                    playSe(AudioEvent.SE_CAST_BUFF)
                }
                specialData.castMessage.startsWith('-') -> {
                    FloatingNumberEffect(battleFieldTable.invoke(), specialData.target, specialData.castMessage, Color.ORANGE).floatUp()
                    BlinkEffect(battleFieldTable.invoke(), specialData.target, Color.ORANGE).start()
                    playSe(AudioEvent.SE_CAST_DEBUFF)
                }
                else -> throw IllegalArgumentException("SpecialOutcomeManager does not support this special action.")
            }
            Utils.runWithDelay(DEFAULT_FLOATING_NUMBER_DELAY) {
                battleState.isDelayingTurn = false
            }
        }
    }

}
