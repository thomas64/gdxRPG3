package nl.t64.cot.screens.battle

import com.badlogic.gdx.scenes.scene2d.Stage
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.battle.*
import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.components.party.inventory.BattlePotionItem
import nl.t64.cot.components.party.inventory.BattleWeaponItem
import nl.t64.cot.screens.dialog.MessageDialog
import nl.t64.cot.screens.dialog.QuestionDialog
import nl.t64.cot.screens.dialog.TwoColumnsQuestionDialog


class BattleDialogManager(
    private val stage: Stage,
    private val currentParticipant: () -> Participant
) {

    fun showConfirmMoveDialog(
        battleField: BattleField,
        onConfirmed: (MoveAction) -> Unit,
        onCancelled: () -> Unit
    ) {
        val moveAction = MoveAction(battleField, currentParticipant.invoke())
        if (moveAction.didCharacterRemainOnTheSameSpace()) {
            onCancelled()
            return
        }
        val message = moveAction.createConfirmationMessage()
        val dialog = QuestionDialog(message) { onConfirmed(moveAction) }
        dialog.show(stage, 0, 0.5f)
    }

    fun showPreviewDialog(
        selectedAttack: BattleAbilityItem,
        selectedTarget: Participant
    ) {
        val attackAction = AttackAction(currentParticipant.invoke(), selectedTarget, selectedAttack)
        val message = attackAction.createPreviewMessage()
        val dialog = MessageDialog(message)
        dialog.setLeftAlignment()
        dialog.setWidthToMinimum()
        dialog.show(stage, AudioEvent.SE_MENU_CONFIRM)
    }

    fun showConfirmAttackDialog(
        selectedAttack: BattleAbilityItem,
        selectedTarget: Participant,
        onConfirmed: (AttackAction) -> Unit
    ) {
        val attackAction = AttackAction(currentParticipant.invoke(), selectedTarget, selectedAttack)

        val notEnoughApSpMessage: String? = attackAction.isCostingTooMuchApSp()
        if (notEnoughApSpMessage != null) {
            showSmallLeftAlignMessageDialog(notEnoughApSpMessage)
            return
        }
        val unableMessage: String? = attackAction.isUnableWithCurrentWeapon()
        if (unableMessage != null) {
            showSmallLeftAlignMessageDialog(unableMessage)
            return
        }

        val message = attackAction.createConfirmationMessage()
        val dialog = QuestionDialog(message) { onConfirmed(attackAction) }
        dialog.setLeftAlignment()
        dialog.show(stage, AudioEvent.SE_MENU_CONFIRM, 0, 1f)
    }

    private fun showSmallLeftAlignMessageDialog(message: String) {
        val dialog = MessageDialog(message)
        dialog.setLeftAlignment()
        dialog.setWidthToMinimum()
        dialog.show(stage, AudioEvent.SE_MENU_ERROR)
    }

    fun showConfirmPotionDialog(
        selectedPotion: BattlePotionItem,
        onConfirmed: (PotionAction) -> Unit
    ) {
        val potionAction = PotionAction(currentParticipant.invoke(), selectedPotion)
        val (isAble, message) = potionAction.isAble()
        if (!isAble) {
            val dialog = MessageDialog(message)
            dialog.show(stage, AudioEvent.SE_MENU_ERROR)
        } else {
            val dialog = QuestionDialog(message) { onConfirmed(potionAction) }
            dialog.show(stage, AudioEvent.SE_MENU_CONFIRM, 0, 0.5f)
        }
    }

    fun showConfirmWeaponDialog(
        selectedWeapon: BattleWeaponItem,
        enemies: List<Participant>,
        onConfirmed: (WeaponAction) -> Unit
    ) {
        val weaponAction = WeaponAction(currentParticipant.invoke(), selectedWeapon, enemies)

        val unableToEquipMessage: String? = weaponAction.isUnableToEquip()
        if (unableToEquipMessage != null) {
            val dialog = MessageDialog(unableToEquipMessage)
            dialog.show(stage, AudioEvent.SE_MENU_ERROR)
            return
        }

        val message = weaponAction.createConfirmationMessage()
        val dialog = if (message.second.isBlank() && message.third.isBlank()) {
            QuestionDialog(message.first) { onConfirmed(weaponAction) }
                .apply { setLeftAlignment() }
        } else {
            TwoColumnsQuestionDialog(message) { onConfirmed(weaponAction) }
        }
        dialog.show(stage, AudioEvent.SE_MENU_CONFIRM, 0, 0.5f)
    }

    fun showFleeDialog(
        battleId: String,
        onConfirmed: (FleeAction) -> Unit
    ) {
        val fleeAction = FleeAction(currentParticipant.invoke(), battleId)
        val (isAble, message) = fleeAction.isAble()
        if (!isAble) {
            val dialog = MessageDialog(message)
            dialog.show(stage, AudioEvent.SE_MENU_ERROR)
        } else {
            val dialog = QuestionDialog(message) { onConfirmed(fleeAction) }
            dialog.show(stage, AudioEvent.SE_MENU_CONFIRM, 0)
        }
    }

    fun showDelayTurnDialog(
        turnManager: TurnManager,
        onConfirmed: (DelayTurnAction) -> Unit
    ) {
        val delayTurnAction = DelayTurnAction(turnManager, currentParticipant.invoke())
        val (isAble, message) = delayTurnAction.isAble()
        if (!isAble) {
            val dialog = MessageDialog(message)
            dialog.show(stage, AudioEvent.SE_MENU_ERROR)
        } else {
            val dialog = QuestionDialog(message) { onConfirmed(delayTurnAction) }
            dialog.show(stage, AudioEvent.SE_MENU_CONFIRM, 0)
        }
    }

    fun showConfirmRestDialog(
        onConfirmed: (RestAction) -> Unit
    ) {
        val restAction = RestAction(currentParticipant.invoke())
        val (isAble, message) = restAction.isAble()
        if (!isAble) {
            val dialog = MessageDialog(message)
            dialog.show(stage, AudioEvent.SE_MENU_ERROR)
        } else {
            val dialog = QuestionDialog(message) { onConfirmed(restAction) }
            dialog.show(stage, AudioEvent.SE_MENU_CONFIRM, 0)
        }
    }

}
