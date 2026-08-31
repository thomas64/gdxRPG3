package nl.t64.cot.screens.battle.action

import com.badlogic.gdx.scenes.scene2d.Stage
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.battle.*
import nl.t64.cot.components.party.abilities.AbilityItem
import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.components.party.inventory.BattlePotionItem
import nl.t64.cot.components.party.inventory.BattleWeaponItem
import nl.t64.cot.screens.battle.BattleState
import nl.t64.cot.screens.dialog.MessageDialog
import nl.t64.cot.screens.dialog.QuestionDialog
import nl.t64.cot.screens.dialog.TwoColumnsQuestionDialog


class BattleDialogManager(
    private val stage: Stage,
    private val turnManager: TurnManager,
    private val currentParticipant: () -> Participant,
    private val battleState: BattleState,
) {

    fun showPreviewDialog(
        selectedAttack: BattleAbilityItem,
        selectedTarget: Participant
    ) {
        val attackAction = AttackAction(currentParticipant.invoke(), selectedTarget, selectedAttack)

        if (shouldShowErrorMessage(attackAction.isUnableWithCurrentWeapon())) return

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

        if (shouldShowErrorMessage(attackAction.isCostingTooMuchApSp())) return
        if (shouldShowErrorMessage(attackAction.isCostingTooMuchResources())) return
        if (shouldShowErrorMessage(attackAction.isUnableWithCurrentWeapon())) return

        val message = attackAction.createConfirmationMessage()
        val dialog = TwoColumnsQuestionDialog(message) { onConfirmed.invoke(attackAction) }
        dialog.show(stage, AudioEvent.SE_MENU_CONFIRM, 0, 1f)
    }

    fun showConfirmSpecialDialog(
        selectedSpecial: BattleAbilityItem,
        selectedTarget: Participant,
        onConfirmed: (SpecialAction) -> Unit
    ) {
        val specialAction = SpecialAction(currentParticipant.invoke(), selectedTarget, selectedSpecial)

        if (shouldShowErrorMessage(specialAction.isCostingTooMuchApSp())) return
        if (shouldShowErrorMessage(specialAction.isCostingTooMuchResources())) return
        if (shouldShowErrorMessage(specialAction.isAlreadyCast())) return
        if (shouldShowErrorMessage(specialAction.isUnableWithCurrentWeapon())) return

        val message = specialAction.createConfirmationMessage()
        val dialog = TwoColumnsQuestionDialog(message) { onConfirmed.invoke(specialAction) }
        dialog.show(stage, AudioEvent.SE_MENU_CONFIRM, 0, 1f)
    }

    private fun shouldShowErrorMessage(possibleErrorMessage: String?): Boolean {
        if (possibleErrorMessage != null) {
            showSmallLeftAlignMessageDialog(possibleErrorMessage)
            return true
        } else {
            return false
        }
    }

    private fun showSmallLeftAlignMessageDialog(message: String) {
        val dialog = MessageDialog(message)
        dialog.setLeftAlignment()
        dialog.setWidthToMinimum()
        dialog.show(stage, AudioEvent.SE_MENU_ERROR)
    }

    fun showConfirmPotionDialogPreBattle(
        selectedPotion: BattlePotionItem,
        onConfirmed: (PotionAction) -> Unit
    ) {
        val potionAction = PotionAction(currentParticipant.invoke(), selectedPotion, potionAp = 0)
        showConfirmDrinkPotionDialog(potionAction, onConfirmed)
    }

    fun showConfirmPotionDialog(
        selectedPotion: BattlePotionItem,
        onConfirmed: (PotionAction) -> Unit
    ) {
        val potionAction = PotionAction(currentParticipant.invoke(), selectedPotion)
        showConfirmDrinkPotionDialog(potionAction, onConfirmed)
    }

    private fun showConfirmDrinkPotionDialog(
        potionAction: PotionAction,
        onConfirmed: (PotionAction) -> Unit
    ) {
        val (isAble, message) = potionAction.isAble()
        if (!isAble) {
            val dialog = MessageDialog(message)
            dialog.show(stage, AudioEvent.SE_MENU_ERROR)
        } else {
            val dialog = QuestionDialog(message) { onConfirmed.invoke(potionAction) }
            dialog.show(stage, AudioEvent.SE_MENU_CONFIRM, 0, 0.5f)
        }
    }

    fun showConfirmWeaponDialogPreBattle(
        selectedWeapon: BattleWeaponItem,
        enemies: List<Participant>,
        onConfirmed: (WeaponAction) -> Unit
    ) {
        val weaponAction = WeaponAction(currentParticipant.invoke(), selectedWeapon, enemies, switchWeaponAp = 0)
        showConfirmSwitchEquipmentDialog(weaponAction, onConfirmed)
    }

    fun showConfirmWeaponDialog(
        selectedWeapon: BattleWeaponItem,
        enemies: List<Participant>,
        onConfirmed: (WeaponAction) -> Unit
    ) {
        val weaponAction = WeaponAction(currentParticipant.invoke(), selectedWeapon, enemies)
        showConfirmSwitchEquipmentDialog(weaponAction, onConfirmed)
    }

    private fun showConfirmSwitchEquipmentDialog(
        weaponAction: WeaponAction,
        equipWeapon: (WeaponAction) -> Unit
    ) {
        val message = weaponAction.createConfirmationMessage()
        val dialog = TwoColumnsQuestionDialog(message) {
            whenAbleToEquipDoSo_OrShowErrorDialog(weaponAction, equipWeapon)
        }
        dialog.show(stage, AudioEvent.SE_MENU_CONFIRM, 0, 0.5f)
    }

    private fun whenAbleToEquipDoSo_OrShowErrorDialog(
        weaponAction: WeaponAction,
        equipWeapon: (WeaponAction) -> Unit
    ) {
        val unableToEquipMessage: String? = weaponAction.isUnableToEquip()
        if (unableToEquipMessage != null) {
            val dialog = MessageDialog(unableToEquipMessage)
            dialog.show(stage, AudioEvent.SE_MENU_ERROR)
        } else {
            equipWeapon.invoke(weaponAction)
        }
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
            val dialog = QuestionDialog(message) { onConfirmed.invoke(fleeAction) }
            dialog.show(stage, AudioEvent.SE_MENU_CONFIRM, 0)
        }
    }

    fun showDelayTurnDialog(
        onConfirmed: (DelayTurnAction) -> Unit
    ) {
        val delayTurnAction = DelayTurnAction(currentParticipant.invoke())
        val (isAble, message) = delayTurnAction.isAble()
        if (!isAble) {
            val dialog = MessageDialog(message)
            dialog.show(stage, AudioEvent.SE_MENU_ERROR)
        } else {
            val dialog = QuestionDialog(message) { onConfirmed.invoke(delayTurnAction) }
            dialog.show(stage, AudioEvent.SE_MENU_CONFIRM, 0)
        }
    }

    fun showPushOnDialog(
        onConfirmed: (PushOnAction) -> Unit
    ) {
        val pushOnAction = PushOnAction(currentParticipant.invoke())
        val (isAble, message) = pushOnAction.isAble()
        if (!isAble) {
            val dialog = MessageDialog(message)
            dialog.show(stage, AudioEvent.SE_MENU_ERROR)
        } else {
            val dialog = QuestionDialog(message) { onConfirmed.invoke(pushOnAction) }
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
            val dialog = QuestionDialog(message) { onConfirmed.invoke(restAction) }
            dialog.show(stage, AudioEvent.SE_MENU_CONFIRM, 0)
        }
    }

    fun showContinuePerformDialog() {
        battleState.isDelayingTurn = true
        val performer: Participant = currentParticipant.invoke()
        val performingAbility: AbilityItem =
            performer.character.getAllAbilities().first { it.id == performer.performingType }

        if (performer.character.currentSp < performingAbility.sp) {
            val message = "${performer.character.name} does not have enough SP to continue performing."
            val messageDialog = MessageDialog(message)
            messageDialog.setActionAfterHide {
                stopPerforming(performer)
            }
            messageDialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT, 0.5f)

        } else {
            val question = "Should ${performer.character.name} continue with ${performingAbility.name} (${performingAbility.sp} SP)?"
            val dialog = QuestionDialog(question) {
                performer.currentAP = 0
                performer.character.currentSp -= performingAbility.sp
                battleState.hasChosenToContinuePerforming = true
                battleState.isDelayingTurn = false
            }
            dialog.setActionAfterNo {
                stopPerforming(performer)
            }
            dialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT, 0, 0.5f)
        }
    }

    private fun stopPerforming(performer: Participant) {
        performer.stopPerforming()
        turnManager.troubadourEffects.removeFromAllParticipants()
        battleState.isDelayingTurn = false
    }

}
