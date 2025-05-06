package nl.t64.cot.screens.battle

import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.components.party.inventory.BattlePotionItem
import nl.t64.cot.components.party.inventory.BattleWeaponItem

class BattleListeners(
    winBattle: () -> Unit,
    openPauseMenu: () -> Unit,
    startBattle: () -> Unit,
    endTurn: () -> Unit,

    selectPrePreviewAttack: () -> Unit,
    selectPreviewAttack: () -> Unit,
    selectAttack: () -> Unit,
    selectPotion: () -> Unit,
    selectWeapon: () -> Unit,

    heroIsSelectedForPrePreview: (String) -> Unit,
    attackInPrePreviewIsSelected: (BattleAbilityItem) -> Unit,
    attackInPreviewIsSelected: (BattleAbilityItem) -> Unit,
    attackIsSelected: (BattleAbilityItem) -> Unit,

    heroIsSelectedForReposition: (String) -> Unit,
    selectReposition: () -> Unit,
    repositionLeft: () -> Unit,
    repositionRight: () -> Unit,

    selectMove: () -> Unit,
    moveLeft: () -> Unit,
    moveRight: () -> Unit,

    showInventoryScreenPreBattle: () -> Unit,
    showInventoryScreen: () -> Unit,
    showDelayTurnDialog: () -> Unit,
    showFleeDialog: () -> Unit,
    showPreviewDialog: (BattleAbilityItem, String) -> Unit,
    showConfirmRestDialog: () -> Unit,
    showConfirmMoveDialog: () -> Unit,
    showConfirmAttackDialog: (BattleAbilityItem, String) -> Unit,
    showConfirmPotionDialog: (BattlePotionItem) -> Unit,
    showConfirmWeaponDialog: (BattleWeaponItem) -> Unit,

    returnToPreBattle: () -> Unit,
    returnToHeroForReposition: () -> Unit,
    returnToHeroForPrePreview: () -> Unit,
    returnToPrePreviewAttack: () -> Unit,
    returnToPreviewAttack: () -> Unit,
    returnToAttack: () -> Unit,
    returnToAction: () -> Unit
) {

    val preBattle = SelectPreBattleListener(winBattle,
                                            openPauseMenu,
                                            selectReposition,
                                            showInventoryScreenPreBattle,
                                            selectPrePreviewAttack,
                                            startBattle)

    val heroForReposition = SelectHeroListener(heroIsSelectedForReposition,
                                               returnToPreBattle)

    val reposition = SelectRepositionListener(repositionLeft,
                                              repositionRight,
                                              returnToHeroForReposition,
                                              returnToHeroForReposition)

    val heroForPrePreview = SelectHeroListener(heroIsSelectedForPrePreview,
                                               returnToPreBattle)

    val prePreviewAttack = SelectAttackListener(attackInPrePreviewIsSelected,
                                                returnToHeroForPrePreview)

    val prePreviewTarget = SelectTargetListener(showPreviewDialog,
                                                returnToPrePreviewAttack)

    val action = SelectActionListener(winBattle,
                                      openPauseMenu,
                                      selectAttack,
                                      selectMove,
                                      selectPotion,
                                      selectWeapon,
                                      selectPreviewAttack,
                                      showInventoryScreen,
                                      showFleeDialog,
                                      showDelayTurnDialog,
                                      showConfirmRestDialog,
                                      endTurn)

    val move = SelectMoveListener(moveLeft,
                                  moveRight,
                                  showConfirmMoveDialog,
                                  returnToAction)

    val previewAttack = SelectAttackListener(attackInPreviewIsSelected,
                                             returnToAction)

    val attack = SelectAttackListener(attackIsSelected,
                                      returnToAction)

    val previewTarget = SelectTargetListener(showPreviewDialog,
                                             returnToPreviewAttack)

    val target = SelectTargetListener(showConfirmAttackDialog,
                                      returnToAttack)

    val potion = SelectPotionListener(showConfirmPotionDialog,
                                      returnToAction)

    val weapon = SelectWeaponListener(showConfirmWeaponDialog,
                                      returnToAction)

}
