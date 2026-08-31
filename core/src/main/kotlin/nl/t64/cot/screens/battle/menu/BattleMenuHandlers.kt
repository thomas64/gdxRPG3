package nl.t64.cot.screens.battle.menu

import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.components.party.inventory.BattlePotionItem
import nl.t64.cot.components.party.inventory.BattleWeaponItem


class BattleMenuHandlers(
    val winBattle: () -> Unit,
    val openPauseMenu: () -> Unit,

    val showInventoryScreenPreBattle: () -> Unit,
    val startBattle: () -> Unit,
    val heroIsSelectedForPreEquipment: (String) -> Unit,
    val heroIsSelectedForPrePotion: (String) -> Unit,
    val heroIsSelectedForPrePreview: (String) -> Unit,
    val showConfirmWeaponDialogPreBattle: (BattleWeaponItem) -> Unit,
    val showConfirmPotionDialogPreBattle: (BattlePotionItem) -> Unit,

    val showInventoryScreen: () -> Unit,
    val showFleeDialog: () -> Unit,
    val showDelayTurnDialog: () -> Unit,
    val showPushOnDialog: () -> Unit,
    val showConfirmRestDialog: () -> Unit,
    val endTurn: () -> Unit,
    val showConfirmAttackDialog: (BattleAbilityItem, String) -> Unit,
    val showConfirmSpecialDialog: (BattleAbilityItem, String) -> Unit,
    val showConfirmWeaponDialog: (BattleWeaponItem) -> Unit,
    val showConfirmPotionDialog: (BattlePotionItem) -> Unit,
    val showPreviewDialog: (BattleAbilityItem, String) -> Unit,

    val confirmMovement: () -> Unit,
    val confirmStealthMovement: () -> Unit
)
