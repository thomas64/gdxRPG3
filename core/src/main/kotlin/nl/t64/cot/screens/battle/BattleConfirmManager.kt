package nl.t64.cot.screens.battle

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.Utils
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.audio.stopSe
import nl.t64.cot.components.battle.*
import nl.t64.cot.screens.dialog.MessageDialog


private const val DEFAULT_FLOATING_NUMBER_DELAY = 1.2f

class BattleConfirmManager(
    private val stage: Stage,
    private val turnManager: TurnManager,
    private val battleFieldTable: () -> Table,
    private val currentParticipant: () -> Participant,
    private val setDelayingTurn: (Boolean) -> Unit
) {

    fun potionConfirmed(potionAction: PotionAction) {
        val (message, color) = potionAction.handle()
        setDelayingTurn.invoke(true)
        Utils.runWithDelay(0.5f) {
            FloatingNumberEffect(battleFieldTable.invoke(),
                                 currentParticipant.invoke().character.name,
                                 message,
                                 color)
                .floatDown()
            playSe(AudioEvent.SE_POTION)
            Utils.runWithDelay(DEFAULT_FLOATING_NUMBER_DELAY) {
                setDelayingTurn.invoke(false)
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
            setDelayingTurn.invoke(false)
        }
        setDelayingTurn.invoke(true)
        Utils.runWithDelay(0.5f) {
            messageDialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT)
        }
    }

    fun delayTurnConfirmed(delayTurnAction: DelayTurnAction) {
        delayTurnAction.handle()
        setDelayingTurn.invoke(true)
        Utils.runWithDelay(0.75f) {
            turnManager.delayTurn()
            setDelayingTurn.invoke(false)
        }
    }

    fun pushOnConfirmed(pushOnAction: PushOnAction) {
        val message: String = pushOnAction.handle()
        setDelayingTurn.invoke(true)
        Utils.runWithDelay(0.5f) {
            FloatingNumberEffect(battleFieldTable.invoke(),
                                 currentParticipant.invoke().character.name,
                                 message,
                                 Color.YELLOW)
                .floatDown()
            playSe(AudioEvent.SE_CAST_BUFF)
            Utils.runWithDelay(DEFAULT_FLOATING_NUMBER_DELAY) {
                setDelayingTurn.invoke(false)
            }
        }
    }

    fun restConfirmed(restAction: RestAction) {
        val message: String = restAction.handle()
        setDelayingTurn.invoke(true)
        Utils.runWithDelay(0.5f) {
            FloatingNumberEffect(battleFieldTable.invoke(),
                                 currentParticipant.invoke().character.name,
                                 message,
                                 Color.GREEN)
                .floatDown()
            playSe(AudioEvent.SE_POTION)
            Utils.runWithDelay(DEFAULT_FLOATING_NUMBER_DELAY) {
                turnManager.setNextTurn()
                setDelayingTurn.invoke(false)
            }
        }
    }

    fun endTurn() {
        val message: String = EndTurnAction(currentParticipant.invoke()).handle()
        setDelayingTurn.invoke(true)
        if (message.isEmpty()) {
            Utils.runWithDelay(0.5f) {
                turnManager.setNextTurn()
                setDelayingTurn.invoke(false)
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
                    setDelayingTurn.invoke(false)
                }
            }
        }
    }

}
