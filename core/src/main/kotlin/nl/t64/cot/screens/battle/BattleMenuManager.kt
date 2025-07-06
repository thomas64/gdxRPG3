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

    private lateinit var preBattleMainMenuListener: SelectPreBattleListener
    private lateinit var actionMainMenuListener: SelectActionListener
    private lateinit var preBattleEquipmentListener: SelectWeaponListener
    private lateinit var actionEquipmentListener: SelectWeaponListener
    private lateinit var preBattleHeroForEquipmentListener: SelectHeroListener
    private lateinit var preBattleHeroForPreviewListener: SelectHeroListener
    private lateinit var preBattlePreviewAttacksListener: SelectAttackListener
    private lateinit var actionPreviewAttacksListener: SelectAttackListener
    private lateinit var actionAttackListener: SelectAttackListener
    private lateinit var preBattlePreviewTargetListener: SelectTargetListener
    private lateinit var actionPreviewTargetListener: SelectTargetListener
    private lateinit var actionAttackTargetListener: SelectTargetListener
    private lateinit var actionMoveListener: SelectMoveListener
    private lateinit var actionPotionListener: SelectPotionListener

    fun setListeners(
        winBattle: () -> Unit,
        openPauseMenu: () -> Unit,
        showInventoryScreenPreBattle: () -> Unit,
        showInventoryScreen: () -> Unit,
        startBattle: () -> Unit,
        heroIsSelectedForPreEquipment: (String) -> Unit,
        heroIsSelectedForPrePreview: (String) -> Unit,
        showPreviewDialog: (BattleAbilityItem, String) -> Unit,
        showFleeDialog: () -> Unit,
        showDelayTurnDialog: () -> Unit,
        showConfirmRestDialog: () -> Unit,
        endTurn: () -> Unit,
        confirmMovement: () -> Unit,
        showConfirmAttackDialog: (BattleAbilityItem, String) -> Unit,
        showConfirmPotionDialog: (BattlePotionItem) -> Unit,
        showConfirmWeaponDialogPreBattle: (BattleWeaponItem) -> Unit,
        showConfirmWeaponDialog: (BattleWeaponItem) -> Unit
    ) {
        preBattleMainMenuListener = SelectPreBattleListener(winBattle, openPauseMenu, showInventoryScreenPreBattle, ::selectEquipmentIsSelectedInPreBattle, ::previewAttacksIsSelectedInPreBattle, startBattle)
        actionMainMenuListener = SelectActionListener(winBattle, openPauseMenu, ::attackIsSelectedInAction, ::moveIsSelectedInAction, ::potionIsSelectedInAction, ::equipmentIsSelectedInAction, ::previewIsSelectedInAction, showInventoryScreen, showFleeDialog, showDelayTurnDialog, showConfirmRestDialog, endTurn)
        preBattleEquipmentListener = SelectWeaponListener(showConfirmWeaponDialogPreBattle, ::returnToSelectHeroInEquipmentInPreBattle)
        actionEquipmentListener = SelectWeaponListener(showConfirmWeaponDialog, ::returnToActionMainMenu)
        preBattleHeroForEquipmentListener = SelectHeroListener(heroIsSelectedForPreEquipment, ::returnToPreBattleMainMenu)
        preBattleHeroForPreviewListener = SelectHeroListener(heroIsSelectedForPrePreview, ::returnToPreBattleMainMenu)
        preBattlePreviewAttacksListener = SelectAttackListener({ anAttackIsSelectedInAttackListInPreview(it, preBattlePreviewTargetListener) }, ::returnToSelectHeroInPreviewAttacksInPreBattle)
        actionPreviewAttacksListener = SelectAttackListener({ anAttackIsSelectedInAttackListInPreview(it, actionPreviewTargetListener) }, ::returnToActionMainMenu)
        actionAttackListener = SelectAttackListener(::anAttackIsSelectedInAttackList, ::returnToActionMainMenu)
        preBattlePreviewTargetListener = SelectTargetListener(showPreviewDialog, ::returnToSelectAttackInPreviewInPreBattle)
        actionPreviewTargetListener = SelectTargetListener(showPreviewDialog, ::returnToSelectAttackInPreview)
        actionAttackTargetListener = SelectTargetListener(showConfirmAttackDialog, ::returnToSelectAttack)
        actionMoveListener = SelectMoveListener(battleField::moveHeroLeft, battleField::moveHeroRight, confirmMovement, ::returnToActionMainMenu)
        actionPotionListener = SelectPotionListener(showConfirmPotionDialog, ::returnToActionMainMenu)
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
        setupHeroTable(preBattleHeroForPreviewListener)
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

    fun aHeroIsSelectedInPreviewEquipmentInPreBattle() {
        screenBuilder.buttonTableSelectHeroIndex = (buttonTableHero.children.last() as GdxList<*>).selectedIndex
        buttonTableHero.remove()
        setupWeaponTable(preBattleEquipmentListener)
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
        setupHeroTable(preBattleHeroForEquipmentListener)
    }

    private fun equipmentIsSelectedInAction() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupWeaponTable(actionEquipmentListener)
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

    private fun returnToSelectHeroInEquipmentInPreBattle() {
        buttonTableWeapon.remove()
        setupHeroTable(preBattleHeroForEquipmentListener)
    }

    private fun returnToSelectHeroInPreviewAttacksInPreBattle() {
        buttonTableAttack.remove()
        setupHeroTable(preBattleHeroForPreviewListener)
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
        setupTable(buttonTablePreBattle, preBattleMainMenuListener)
    }

    private fun setupHeroTable(selectHeroListener: SelectHeroListener) {
        buttonTableHero = screenBuilder.createButtonTableHero(turnManager.getOnlyHeroes())
        setupTable(buttonTableHero, selectHeroListener)
    }

    private fun setupPrePreviewAttackTable() {
        buttonTableAttack = screenBuilder.createButtonTablePreviewAttack(currentParticipant.invoke())
        setupTable(buttonTableAttack, preBattlePreviewAttacksListener)
    }

    private fun setupActionTable() {
        battleField.cancelMovement()
        battleField.resetStartingSpace()
        val areEnemiesInRange: Boolean = battleField.getTargetableEnemiesForActingHero().isNotEmpty()
        buttonTableAction = screenBuilder.createButtonTableAction(currentParticipant.invoke(), areEnemiesInRange)
        setupTable(buttonTableAction, actionMainMenuListener)
    }

    private fun setupMoveTable() {
        battleField.setStartingSpace()
        buttonTableMove = screenBuilder.createButtonTableMove()
        setupTable(buttonTableMove, actionMoveListener)
    }

    private fun setupPreviewAttackTable() {
        buttonTableAttack = screenBuilder.createButtonTablePreviewAttack(currentParticipant.invoke())
        setupTable(buttonTableAttack, actionPreviewAttacksListener)
    }

    private fun setupAttackTable() {
        buttonTableAttack = screenBuilder.createButtonTableAttack(currentParticipant.invoke())
        setupTable(buttonTableAttack, actionAttackListener)
    }

    private fun setupPreviewTargetTable(selectedAttack: BattleAbilityItem, targetListener: SelectTargetListener) {
        buttonTableTarget = screenBuilder.createButtonTableTarget(turnManager.getOnlyEnemies())
        targetListener.setSelectedAttack(selectedAttack)
        setupTable(buttonTableTarget, targetListener)
    }

    private fun setupTargetTable(selectedAttack: BattleAbilityItem) {
        val targetableEnemies: List<Participant> = battleField.getTargetableEnemiesForActingHero()
        buttonTableTarget = screenBuilder.createButtonTableTarget(targetableEnemies)
        actionAttackTargetListener.setSelectedAttack(selectedAttack)
        setupTable(buttonTableTarget, actionAttackTargetListener)
    }

    private fun setupPotionTable() {
        val battlePotions: List<BattlePotionItem> = gameData.inventory.getAllOf(InventoryGroup.POTION)
            .filter { it.name.contains(" Potion") }
            .map { BattlePotionItem(it) }
        buttonTablePotion = screenBuilder.createButtonTablePotion(battlePotions)
        setupTable(buttonTablePotion, actionPotionListener)
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
