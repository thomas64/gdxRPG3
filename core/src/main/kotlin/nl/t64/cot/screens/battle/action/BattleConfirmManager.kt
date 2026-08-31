package nl.t64.cot.screens.battle.action

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.Utils
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.audio.stopSe
import nl.t64.cot.components.battle.*
import nl.t64.cot.screens.battle.BattleResultManager
import nl.t64.cot.screens.battle.BattleState
import nl.t64.cot.screens.battle.effects.FloatingNumberEffect
import nl.t64.cot.screens.dialog.MessageDialog


private const val DEFAULT_FLOATING_NUMBER_DELAY = 1.2f

class BattleConfirmManager(
    private val stage: Stage,
    private val turnManager: TurnManager,
    private val battleFieldTable: () -> Table,
    private val currentParticipant: () -> Participant,
    private val battleState: BattleState
) {

    fun potionConfirmed(potionAction: PotionAction, onFinished: () -> Unit = {}) {
        val (message, color) = potionAction.handle()
        battleState.isDelayingTurn = true
        Utils.runWithDelay(0.5f) {
            FloatingNumberEffect(battleFieldTable.invoke(),
                                 currentParticipant.invoke().character.name,
                                 message,
                                 color)
                .floatDown()
            playSe(AudioEvent.SE_POTION)
            Utils.runWithDelay(DEFAULT_FLOATING_NUMBER_DELAY) {
                battleState.isDelayingTurn = false
                onFinished.invoke()
            }
        }
    }

    fun weaponConfirmed(weaponAction: WeaponAction) {
        weaponAction.handle()
        stopSe(AudioEvent.SE_MENU_CONFIRM)
        playSe(AudioEvent.SE_EQUIP)
    }

    fun fleeConfirmed(fleeAction: FleeAction, resultManager: BattleResultManager) {
        val (isSuccess, message) = fleeAction.handle()
        val messageDialog = MessageDialog(message)
        messageDialog.setActionAfterHide {
            if (isSuccess) {
                resultManager.battleFledExitScreen()
            }
            battleState.isDelayingTurn = false
        }
        battleState.isDelayingTurn = true
        Utils.runWithDelay(0.5f) {
            messageDialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT)
        }
    }

    fun delayTurnConfirmed(delayTurnAction: DelayTurnAction) {
        delayTurnAction.handle()
        battleState.isDelayingTurn = true
        Utils.runWithDelay(0.75f) {
            turnManager.delayTurn()
            battleState.isDelayingTurn = false
        }
    }

    fun pushOnConfirmed(pushOnAction: PushOnAction) {
        val message: String = pushOnAction.handle()
        battleState.isDelayingTurn = true
        Utils.runWithDelay(0.5f) {
            FloatingNumberEffect(battleFieldTable.invoke(),
                                 currentParticipant.invoke().character.name,
                                 message,
                                 Color.YELLOW)
                .floatDown()
            playSe(AudioEvent.SE_CAST_BUFF)
            Utils.runWithDelay(DEFAULT_FLOATING_NUMBER_DELAY) {
                battleState.isDelayingTurn = false
            }
        }
    }

    fun restConfirmed(restAction: RestAction) {
        val message: String = restAction.handle()
        battleState.isDelayingTurn = true
        Utils.runWithDelay(0.5f) {
            FloatingNumberEffect(battleFieldTable.invoke(),
                                 currentParticipant.invoke().character.name,
                                 message,
                                 Color.GREEN)
                .floatDown()
            playSe(AudioEvent.SE_POTION)
            Utils.runWithDelay(DEFAULT_FLOATING_NUMBER_DELAY) {
                turnManager.setNextTurn()
                battleState.isDelayingTurn = false
            }
        }
    }

    fun endTurn() {
        val message: String = EndTurnAction(currentParticipant.invoke()).handle()
        battleState.isDelayingTurn = true
        if (message.isEmpty()) {
            Utils.runWithDelay(0.5f) {
                turnManager.setNextTurn()
                battleState.isDelayingTurn = false
            }
        } else {
            Utils.runWithDelay(0.5f) {
                FloatingNumberEffect(battleFieldTable.invoke(),
                                     currentParticipant.invoke().character.name,
                                     message,
                                     Color.WHITE)
                    .floatDown()
                Utils.runWithDelay(DEFAULT_FLOATING_NUMBER_DELAY) {
                    turnManager.setNextTurn()
                    battleState.isDelayingTurn = false
                }
            }
        }
    }

}
