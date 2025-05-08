package nl.t64.cot.screens.battle

import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.components.party.inventory.BattlePotionItem
import nl.t64.cot.components.party.inventory.BattleWeaponItem

class BattleListeners {

    private lateinit var winBattle: () -> Unit
    private lateinit var openPauseMenu: () -> Unit
    private lateinit var startBattle: () -> Unit
    private lateinit var endTurn: () -> Unit
    private lateinit var heroIsSelectedForPrePreview: (String) -> Unit
    private lateinit var heroIsSelectedForReposition: (String) -> Unit
    private lateinit var showInventoryScreenPreBattle: () -> Unit
    private lateinit var showInventoryScreen: () -> Unit
    private lateinit var showDelayTurnDialog: () -> Unit
    private lateinit var showFleeDialog: () -> Unit
    private lateinit var showPreviewDialog: (BattleAbilityItem, String) -> Unit
    private lateinit var showConfirmRestDialog: () -> Unit
    private lateinit var showConfirmMoveDialog: () -> Unit
    private lateinit var showConfirmAttackDialog: (BattleAbilityItem, String) -> Unit
    private lateinit var showConfirmPotionDialog: (BattlePotionItem) -> Unit
    private lateinit var showConfirmWeaponDialog: (BattleWeaponItem) -> Unit

    private lateinit var selectPrePreviewAttack: () -> Unit
    private lateinit var selectPreviewAttack: () -> Unit
    private lateinit var selectAttack: () -> Unit
    private lateinit var selectPotion: () -> Unit
    private lateinit var selectWeapon: () -> Unit
    private lateinit var attackInPrePreviewIsSelected: (BattleAbilityItem) -> Unit
    private lateinit var attackInPreviewIsSelected: (BattleAbilityItem) -> Unit
    private lateinit var attackIsSelected: (BattleAbilityItem) -> Unit
    private lateinit var selectReposition: () -> Unit
    private lateinit var selectMove: () -> Unit
    private lateinit var returnToPreBattle: () -> Unit
    private lateinit var returnToHeroForReposition: () -> Unit
    private lateinit var returnToHeroForPrePreview: () -> Unit
    private lateinit var returnToPrePreviewAttack: () -> Unit
    private lateinit var returnToPreviewAttack: () -> Unit
    private lateinit var returnToAttack: () -> Unit
    private lateinit var returnToAction: () -> Unit

    private lateinit var repositionLeft: () -> Unit
    private lateinit var repositionRight: () -> Unit
    private lateinit var moveLeft: () -> Unit
    private lateinit var moveRight: () -> Unit

    fun setScreenListeners(
        winBattle: () -> Unit,
        openPauseMenu: () -> Unit,
        startBattle: () -> Unit,
        endTurn: () -> Unit,
        heroIsSelectedForPrePreview: (String) -> Unit,
        heroIsSelectedForReposition: (String) -> Unit,
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
    ) {
        this.winBattle = winBattle
        this.openPauseMenu = openPauseMenu
        this.startBattle = startBattle
        this.endTurn = endTurn
        this.heroIsSelectedForPrePreview = heroIsSelectedForPrePreview
        this.heroIsSelectedForReposition = heroIsSelectedForReposition
        this.showInventoryScreenPreBattle = showInventoryScreenPreBattle
        this.showInventoryScreen = showInventoryScreen
        this.showDelayTurnDialog = showDelayTurnDialog
        this.showFleeDialog = showFleeDialog
        this.showPreviewDialog = showPreviewDialog
        this.showConfirmRestDialog = showConfirmRestDialog
        this.showConfirmMoveDialog = showConfirmMoveDialog
        this.showConfirmAttackDialog = showConfirmAttackDialog
        this.showConfirmPotionDialog = showConfirmPotionDialog
        this.showConfirmWeaponDialog = showConfirmWeaponDialog
    }

    fun setMenuListeners(
        selectPrePreviewAttack: () -> Unit,
        selectPreviewAttack: () -> Unit,
        selectAttack: () -> Unit,
        selectPotion: () -> Unit,
        selectWeapon: () -> Unit,
        attackInPrePreviewIsSelected: (BattleAbilityItem) -> Unit,
        attackInPreviewIsSelected: (BattleAbilityItem) -> Unit,
        attackIsSelected: (BattleAbilityItem) -> Unit,
        selectReposition: () -> Unit,
        selectMove: () -> Unit,
        returnToPreBattle: () -> Unit,
        returnToHeroForReposition: () -> Unit,
        returnToHeroForPrePreview: () -> Unit,
        returnToPrePreviewAttack: () -> Unit,
        returnToPreviewAttack: () -> Unit,
        returnToAttack: () -> Unit,
        returnToAction: () -> Unit
    ) {
        this.selectPrePreviewAttack = selectPrePreviewAttack
        this.selectPreviewAttack = selectPreviewAttack
        this.selectAttack = selectAttack
        this.selectPotion = selectPotion
        this.selectWeapon = selectWeapon
        this.attackInPrePreviewIsSelected = attackInPrePreviewIsSelected
        this.attackInPreviewIsSelected = attackInPreviewIsSelected
        this.attackIsSelected = attackIsSelected
        this.selectReposition = selectReposition
        this.selectMove = selectMove
        this.returnToPreBattle = returnToPreBattle
        this.returnToHeroForReposition = returnToHeroForReposition
        this.returnToHeroForPrePreview = returnToHeroForPrePreview
        this.returnToPrePreviewAttack = returnToPrePreviewAttack
        this.returnToPreviewAttack = returnToPreviewAttack
        this.returnToAttack = returnToAttack
        this.returnToAction = returnToAction
    }

    fun setBattleFieldListeners(
        repositionLeft: () -> Unit,
        repositionRight: () -> Unit,
        moveLeft: () -> Unit,
        moveRight: () -> Unit
    ) {
        this.repositionLeft = repositionLeft
        this.repositionRight = repositionRight
        this.moveLeft = moveLeft
        this.moveRight = moveRight
    }


    val preBattle by lazy {
        SelectPreBattleListener(winBattle,
                                openPauseMenu,
                                selectReposition,
                                showInventoryScreenPreBattle,
                                selectPrePreviewAttack,
                                startBattle)
    }

    val heroForReposition by lazy {
        SelectHeroListener(heroIsSelectedForReposition,
                           returnToPreBattle)
    }

    val reposition by lazy {
        SelectRepositionListener(repositionLeft,
                                 repositionRight,
                                 returnToHeroForReposition,
                                 returnToHeroForReposition)
    }

    val heroForPrePreview by lazy {
        SelectHeroListener(heroIsSelectedForPrePreview,
                           returnToPreBattle)
    }


    val prePreviewAttack by lazy {
        SelectAttackListener(attackInPrePreviewIsSelected,
                             returnToHeroForPrePreview)
    }

    val prePreviewTarget by lazy {
        SelectTargetListener(showPreviewDialog,
                             returnToPrePreviewAttack)
    }

    val action by lazy {
        SelectActionListener(winBattle,
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
    }

    val move by lazy {
        SelectMoveListener(moveLeft,
                           moveRight,
                           showConfirmMoveDialog,
                           returnToAction)
    }

    val previewAttack by lazy {
        SelectAttackListener(attackInPreviewIsSelected,
                             returnToAction)
    }

    val attack by lazy {
        SelectAttackListener(attackIsSelected,
                             returnToAction)
    }

    val previewTarget by lazy {
        SelectTargetListener(showPreviewDialog,
                             returnToPreviewAttack)
    }

    val target by lazy {
        SelectTargetListener(showConfirmAttackDialog,
                             returnToAttack)
    }

    val potion by lazy {
        SelectPotionListener(showConfirmPotionDialog,
                             returnToAction)
    }

    val weapon by lazy {
        SelectWeaponListener(showConfirmWeaponDialog,
                             returnToAction)
    }

}
