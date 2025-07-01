package nl.t64.cot.screens.battle

import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.battle.BattleField
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.components.battle.TurnManager
import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.components.party.inventory.BattlePotionItem
import nl.t64.cot.components.party.inventory.BattleWeaponItem
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.screens.battle.listeners.*
import com.badlogic.gdx.scenes.scene2d.ui.List as GdxList


class BattleMenuManager(
    private val stage: Stage,
    private val screenBuilder: BattleScreenBuilder,
    private val turnManager: TurnManager,
    private val battleField: BattleField,
    private val currentParticipant: () -> Participant
) {

    var buttonTablePreBattle: Table = Table()
    var buttonTableAction: Table = Table()
    private var buttonTableHero: Table = Table()
    private var buttonTableMove: Table = Table()
    private var buttonTableAttack: Table = Table()
    var buttonTableTarget: Table = Table()
    var buttonTablePotion: Table = Table()
    var buttonTableWeapon: Table = Table()

    private val allButtonTables: List<Table>
        get() = listOf(buttonTablePreBattle,
                       buttonTableHero,
                       buttonTableAction,
                       buttonTableMove,
                       buttonTableAttack,
                       buttonTableTarget,
                       buttonTablePotion,
                       buttonTableWeapon)

    private lateinit var preBattle: SelectPreBattleListener
    private lateinit var action: SelectActionListener
    private lateinit var preBattleWeapon: SelectWeaponListener
    private lateinit var actionWeapon: SelectWeaponListener
    private lateinit var preBattleSelectHeroForPreview: SelectHeroListener
    private lateinit var preBattlePreviewAttacks: SelectAttackListener
    private lateinit var actionPreviewAttacks: SelectAttackListener
    private lateinit var preBattlePreviewTarget: SelectTargetListener
    private lateinit var actionPreviewTarget: SelectTargetListener
    private lateinit var actionMove: SelectMoveListener
    private lateinit var actionAttack: SelectAttackListener
    private lateinit var actionAttackTarget: SelectTargetListener
    private lateinit var actionPotion: SelectPotionListener

    fun setListeners(
        winBattle: () -> Unit,
        openPauseMenu: () -> Unit,
        showInventoryScreenPreBattle: () -> Unit,
        showInventoryScreen: () -> Unit,
        startBattle: () -> Unit,
        heroIsSelectedForPrePreview: (String) -> Unit,
        showPreviewDialog: (BattleAbilityItem, String) -> Unit,
        showFleeDialog: () -> Unit,
        showDelayTurnDialog: () -> Unit,
        showConfirmRestDialog: () -> Unit,
        endTurn: () -> Unit,
        showConfirmMoveDialog: () -> Unit,
        showConfirmAttackDialog: (BattleAbilityItem, String) -> Unit,
        showConfirmPotionDialog: (BattlePotionItem) -> Unit,
        showConfirmWeaponDialogPreBattle: (BattleWeaponItem) -> Unit,
        showConfirmWeaponDialog: (BattleWeaponItem) -> Unit
    ) {
        preBattle = SelectPreBattleListener(winBattle, openPauseMenu, showInventoryScreenPreBattle, ::selectEquipmentIsSelectedInPreBattle, ::previewAttacksIsSelectedInPreBattle, startBattle)
        action = SelectActionListener(winBattle, openPauseMenu, ::attackIsSelectedInAction, ::moveIsSelectedInAction, ::potionIsSelectedInAction, ::equipmentIsSelectedInAction, ::previewIsSelectedInAction, showInventoryScreen, showFleeDialog, showDelayTurnDialog, showConfirmRestDialog, endTurn)
        preBattleWeapon = SelectWeaponListener(showConfirmWeaponDialogPreBattle, ::returnToPreBattleMainMenu)
        actionWeapon = SelectWeaponListener(showConfirmWeaponDialog, ::returnToActionMainMenu)
        preBattleSelectHeroForPreview = SelectHeroListener(heroIsSelectedForPrePreview, ::returnToPreBattleMainMenu)
        preBattlePreviewAttacks = SelectAttackListener({ anAttackIsSelectedInAttackListInPreview(it, preBattlePreviewTarget) }, ::returnToSelectHeroInPreviewAttacksInPreBattle)
        actionPreviewAttacks = SelectAttackListener({ anAttackIsSelectedInAttackListInPreview(it, actionPreviewTarget) }, ::returnToActionMainMenu)
        preBattlePreviewTarget = SelectTargetListener(showPreviewDialog, ::returnToSelectAttackInPreviewInPreBattle)
        actionPreviewTarget = SelectTargetListener(showPreviewDialog, ::returnToSelectAttackInPreview)
        actionMove = SelectMoveListener(battleField::moveHeroLeft, battleField::moveHeroRight, showConfirmMoveDialog, ::returnToActionMainMenu)
        actionAttack = SelectAttackListener(::anAttackIsSelectedInAttackList, ::returnToActionMainMenu)
        actionAttackTarget = SelectTargetListener(showConfirmAttackDialog, ::returnToSelectAttack)
        actionPotion = SelectPotionListener(showConfirmPotionDialog, ::returnToActionMainMenu)
    }


    fun possibleSetupActionTable() {
        val isNoButtonTableVisible = stage.actors.items.none { it in allButtonTables }
        if (isNoButtonTableVisible) {
            screenBuilder.buttonTableMainMenuIndex = 0
            setupActionTable()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun previewAttacksIsSelectedInPreBattle() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTablePreBattle.children.last() as GdxList<*>).selectedIndex
        buttonTablePreBattle.remove()
        setupHeroTableForPrePreview()
    }

    private fun previewIsSelectedInAction() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupPreviewAttackTable()
    }

    private fun attackIsSelectedInAction() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupAttackTable()
    }

    fun aHeroIsSelectedInPreviewAttacksInPreBattle() {
        screenBuilder.buttonTableSelectHeroIndex = (buttonTableHero.children.last() as GdxList<*>).selectedIndex
        buttonTableHero.remove()
        setupPrePreviewAttackTable()
    }

    private fun anAttackIsSelectedInAttackListInPreview(attack: BattleAbilityItem,
                                                        targetListener: SelectTargetListener) {
        screenBuilder.buttonTableSelectAttackIndex = (buttonTableAttack.children.last() as GdxList<*>).selectedIndex
        buttonTableAttack.remove()
        setupPreviewTargetTable(attack, targetListener)
    }

    private fun anAttackIsSelectedInAttackList(attack: BattleAbilityItem) {
        screenBuilder.buttonTableSelectAttackIndex = (buttonTableAttack.children.last() as GdxList<*>).selectedIndex
        buttonTableAttack.remove()
        setupTargetTable(attack)
    }

    private fun selectEquipmentIsSelectedInPreBattle() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTablePreBattle.children.last() as GdxList<*>).selectedIndex
        buttonTablePreBattle.remove()
        setupWeaponTable(preBattleWeapon)
    }

    private fun equipmentIsSelectedInAction() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupWeaponTable(actionWeapon)
    }

    private fun moveIsSelectedInAction() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupMoveTable()
    }

    private fun potionIsSelectedInAction() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupPotionTable()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun returnToPreBattleMainMenu() {
        buttonTableWeapon.remove()
        buttonTableHero.remove()
        setupPreBattleTable()
    }

    fun returnToActionMainMenu() {
        buttonTableMove.remove()
        buttonTableAttack.remove()
        buttonTablePotion.remove()
        buttonTableWeapon.remove()
        setupActionTable()
    }

    private fun returnToSelectHeroInPreviewAttacksInPreBattle() {
        buttonTableAttack.remove()
        setupHeroTableForPrePreview()
    }

    private fun returnToSelectAttackInPreviewInPreBattle() {
        buttonTableTarget.remove()
        setupPrePreviewAttackTable()
    }

    private fun returnToSelectAttackInPreview() {
        buttonTableTarget.remove()
        setupPreviewAttackTable()
    }

    private fun returnToSelectAttack() {
        buttonTableTarget.remove()
        setupAttackTable()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun setupPreBattleTable() {
        buttonTablePreBattle = screenBuilder.createButtonTablePreBattle()
        setupTable(buttonTablePreBattle, preBattle)
    }

    private fun setupHeroTableForPrePreview() {
        buttonTableHero = screenBuilder.createButtonTableHero(turnManager.getOnlyHeroes())
        setupTable(buttonTableHero, preBattleSelectHeroForPreview)
    }

    private fun setupPrePreviewAttackTable() {
        buttonTableAttack = screenBuilder.createButtonTablePreviewAttack(currentParticipant.invoke())
        setupTable(buttonTableAttack, preBattlePreviewAttacks)
    }

    private fun setupActionTable() {
        battleField.cancelMovement()
        battleField.resetStartingSpace()
        val areEnemiesInRange: Boolean = battleField.getTargetableEnemiesForActingHero().isNotEmpty()
        buttonTableAction = screenBuilder.createButtonTableAction(currentParticipant.invoke(), areEnemiesInRange)
        setupTable(buttonTableAction, action)
    }

    private fun setupMoveTable() {
        battleField.setStartingSpace()
        buttonTableMove = screenBuilder.createButtonTableMove()
        setupTable(buttonTableMove, actionMove)
    }

    private fun setupPreviewAttackTable() {
        buttonTableAttack = screenBuilder.createButtonTablePreviewAttack(currentParticipant.invoke())
        setupTable(buttonTableAttack, actionPreviewAttacks)
    }

    private fun setupAttackTable() {
        buttonTableAttack = screenBuilder.createButtonTableAttack(currentParticipant.invoke())
        setupTable(buttonTableAttack, actionAttack)
    }

    private fun setupPreviewTargetTable(selectedAttack: BattleAbilityItem, targetListener: SelectTargetListener) {
        buttonTableTarget = screenBuilder.createButtonTableTarget(turnManager.getOnlyEnemies())
        targetListener.setSelectedAttack(selectedAttack)
        setupTable(buttonTableTarget, targetListener)
    }

    private fun setupTargetTable(selectedAttack: BattleAbilityItem) {
        val targetableEnemies: List<Participant> = battleField.getTargetableEnemiesForActingHero()
        buttonTableTarget = screenBuilder.createButtonTableTarget(targetableEnemies)
        actionAttackTarget.setSelectedAttack(selectedAttack)
        setupTable(buttonTableTarget, actionAttackTarget)
    }

    private fun setupPotionTable() {
        val battlePotions: List<BattlePotionItem> = gameData.inventory.getAllOf(InventoryGroup.POTION)
            .filter { it.name.contains(" Potion") }
            .map { BattlePotionItem(it) }
        buttonTablePotion = screenBuilder.createButtonTablePotion(battlePotions)
        setupTable(buttonTablePotion, actionPotion)
    }

    private fun setupWeaponTable(listener: SelectWeaponListener) {
        val battleEquipment: List<BattleWeaponItem> =
            (gameData.inventory.getAllOf(InventoryGroup.WEAPON) + gameData.inventory.getAllOf(InventoryGroup.SHIELD))
                .map { BattleWeaponItem(it) }
        val currentWeapon: InventoryItem? = currentParticipant.invoke().character.getInventoryItem(InventoryGroup.WEAPON)
        val currentShield: InventoryItem? = currentParticipant.invoke().character.getInventoryItem(InventoryGroup.SHIELD)
        buttonTableWeapon = screenBuilder.createButtonTableWeapon(battleEquipment, currentWeapon, currentShield)
        setupTable(buttonTableWeapon, listener)
    }

    private fun setupTable(table: Table, listener: InputListener) {
        stage.addActor(table)
        table.addListener(listener)
        stage.keyboardFocus = table.children.last()
    }

}
