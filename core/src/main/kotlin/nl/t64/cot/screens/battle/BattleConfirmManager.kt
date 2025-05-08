package nl.t64.cot.screens.battle

import com.badlogic.gdx.scenes.scene2d.Stage
import nl.t64.cot.Utils
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.battle.*
import nl.t64.cot.screens.dialog.MessageDialog


class BattleConfirmManager(
    private val stage: Stage,
    private val setDelayingTurn: (Boolean) -> Unit
) {

    fun potionConfirmed(potionAction: PotionAction) {
        val (message, audioEvent) = potionAction.handle()
        val messageDialog = MessageDialog(message)
        messageDialog.setActionAfterHide {
            setDelayingTurn.invoke(false)
        }
        setDelayingTurn.invoke(true)
        Utils.runWithDelay(0.5f) {
            messageDialog.show(stage, audioEvent)
        }
    }

    fun weaponConfirmed(weaponAction: WeaponAction) {
        val message = weaponAction.handle()
        val messageDialog = MessageDialog(message)
        messageDialog.setActionAfterHide {
            setDelayingTurn.invoke(false)
        }
        setDelayingTurn.invoke(true)
        Utils.runWithDelay(0.5f) {
            messageDialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT)
        }
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

    fun delayTurnConfirmed(delayTurnAction: DelayTurnAction, turnManager: TurnManager) {
        val message = delayTurnAction.handle()
        val messageDialog = MessageDialog(message)
        messageDialog.setActionAfterHide {
            turnManager.delayTurn()
            setDelayingTurn.invoke(false)
        }
        setDelayingTurn.invoke(true)
        Utils.runWithDelay(0.5f) {
            messageDialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT)
        }
    }

    fun restConfirmed(restAction: RestAction, turnManager: TurnManager) {
        val (message, audioEvent) = restAction.handle()
        val messageDialog = MessageDialog(message)
        messageDialog.setActionAfterHide {
            turnManager.setNextTurn()
            setDelayingTurn.invoke(false)
        }
        setDelayingTurn.invoke(true)
        Utils.runWithDelay(0.5f) {
            messageDialog.show(stage, audioEvent, 0.25f)
        }
    }

    fun endTurn(currentParticipant: Participant, turnManager: TurnManager) {
        val message = EndTurnAction(currentParticipant).handle()
        val messageDialog = MessageDialog(message)
        messageDialog.setActionAfterHide {
            turnManager.setNextTurn()
            setDelayingTurn.invoke(false)
        }
        setDelayingTurn.invoke(true)
        Utils.runWithDelay(0.5f) {
            messageDialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT)
        }
    }

}
