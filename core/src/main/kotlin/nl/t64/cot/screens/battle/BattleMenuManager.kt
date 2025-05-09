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
    private var buttonTableReposition: Table = Table()
    private var buttonTableMove: Table = Table()
    private var buttonTableAttack: Table = Table()
    var buttonTableTarget: Table = Table()
    var buttonTablePotion: Table = Table()
    var buttonTableWeapon: Table = Table()

    private val allButtonTables: List<Table>
        get() = listOf(buttonTablePreBattle,
                       buttonTableHero,
                       buttonTableReposition,
                       buttonTableAction,
                       buttonTableMove,
                       buttonTableAttack,
                       buttonTableTarget,
                       buttonTablePotion,
                       buttonTableWeapon)

    private lateinit var preBattle: SelectPreBattleListener
    private lateinit var heroForReposition: SelectHeroListener
    private lateinit var reposition: SelectRepositionListener
    private lateinit var heroForPrePreview: SelectHeroListener
    private lateinit var prePreviewAttack: SelectAttackListener
    private lateinit var prePreviewTarget: SelectTargetListener
    private lateinit var action: SelectActionListener
    private lateinit var move: SelectMoveListener
    private lateinit var previewAttack: SelectAttackListener
    private lateinit var attack: SelectAttackListener
    private lateinit var previewTarget: SelectTargetListener
    private lateinit var target: SelectTargetListener
    private lateinit var potion: SelectPotionListener
    private lateinit var weapon: SelectWeaponListener

    fun setListeners(
        winBattle: () -> Unit,
        openPauseMenu: () -> Unit,
        showInventoryScreenPreBattle: () -> Unit,
        startBattle: () -> Unit,
        heroIsSelectedForReposition: (String) -> Unit,
        heroIsSelectedForPrePreview: (String) -> Unit,
        showPreviewDialog: (BattleAbilityItem, String) -> Unit,
        showInventoryScreen: () -> Unit,
        showFleeDialog: () -> Unit,
        showDelayTurnDialog: () -> Unit,
        showConfirmRestDialog: () -> Unit,
        endTurn: () -> Unit,
        showConfirmMoveDialog: () -> Unit,
        showConfirmAttackDialog: (BattleAbilityItem, String) -> Unit,
        showConfirmPotionDialog: (BattlePotionItem) -> Unit,
        showConfirmWeaponDialog: (BattleWeaponItem) -> Unit
    ) {
        // @formatter:off
        preBattle = SelectPreBattleListener(winBattle, openPauseMenu, ::selectReposition, showInventoryScreenPreBattle, ::selectPrePreviewAttack, startBattle)
        heroForReposition = SelectHeroListener(heroIsSelectedForReposition, ::returnToPreBattle)
        reposition = SelectRepositionListener(battleField::repositionHeroLeft, battleField::repositionHeroRight, ::returnToHeroForReposition, ::returnToHeroForReposition)
        heroForPrePreview = SelectHeroListener(heroIsSelectedForPrePreview, ::returnToPreBattle)
        prePreviewAttack = SelectAttackListener(::attackInPrePreviewIsSelected, ::returnToHeroForPrePreview)
        prePreviewTarget = SelectTargetListener(showPreviewDialog, ::returnToPrePreviewAttack)
        action = SelectActionListener(winBattle, openPauseMenu, ::selectAttack, ::selectMove, ::selectPotion, ::selectWeapon, ::selectPreviewAttack, showInventoryScreen, showFleeDialog, showDelayTurnDialog, showConfirmRestDialog, endTurn)
        move = SelectMoveListener(battleField::moveHeroLeft, battleField::moveHeroRight, showConfirmMoveDialog, ::returnToAction)
        previewAttack = SelectAttackListener(::attackInPreviewIsSelected, ::returnToAction)
        attack = SelectAttackListener(::attackIsSelected, ::returnToAction)
        previewTarget = SelectTargetListener(showPreviewDialog, ::returnToPreviewAttack)
        target = SelectTargetListener(showConfirmAttackDialog, ::returnToAttack)
        potion = SelectPotionListener(showConfirmPotionDialog, ::returnToAction)
        weapon = SelectWeaponListener(showConfirmWeaponDialog, ::returnToAction)
        // @formatter:on
    }


    fun possibleSetupActionTable() {
        val isNoButtonTableVisible = stage.actors.items.none { it in allButtonTables }
        if (isNoButtonTableVisible) {
            screenBuilder.buttonTableMainMenuIndex = 0
            setupActionTable()
        }
    }


    private fun selectReposition() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTablePreBattle.children.last() as GdxList<*>).selectedIndex
        buttonTablePreBattle.remove()
        setupHeroTableForReposition()
    }

    private fun selectPrePreviewAttack() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTablePreBattle.children.last() as GdxList<*>).selectedIndex
        buttonTablePreBattle.remove()
        setupHeroTableForPrePreview()
    }

    fun heroIsSelectedForReposition() {
        screenBuilder.buttonTableSelectHeroIndex = (buttonTableHero.children.last() as GdxList<*>).selectedIndex
        buttonTableHero.remove()
        setupRepositionTable()
    }

    fun heroIsSelectedForPrePreview() {
        screenBuilder.buttonTableSelectHeroIndex = (buttonTableHero.children.last() as GdxList<*>).selectedIndex
        buttonTableHero.remove()
        setupPrePreviewAttackTable()
    }

    private fun attackInPrePreviewIsSelected(attack: BattleAbilityItem) {
        screenBuilder.buttonTableSelectAttackIndex = (buttonTableAttack.children.last() as GdxList<*>).selectedIndex
        buttonTableAttack.remove()
        setupPrePreviewTargetTable(attack)
    }

    private fun selectMove() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupMoveTable()
    }

    private fun selectPreviewAttack() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupPreviewAttackTable()
    }

    private fun attackInPreviewIsSelected(attack: BattleAbilityItem) {
        screenBuilder.buttonTableSelectAttackIndex = (buttonTableAttack.children.last() as GdxList<*>).selectedIndex
        buttonTableAttack.remove()
        setupPreviewTargetTable(attack)
    }

    private fun selectAttack() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupAttackTable()
    }

    private fun attackIsSelected(attack: BattleAbilityItem) {
        screenBuilder.buttonTableSelectAttackIndex = (buttonTableAttack.children.last() as GdxList<*>).selectedIndex
        buttonTableAttack.remove()
        setupTargetTable(attack)
    }

    private fun selectPotion() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupPotionTable()
    }

    private fun selectWeapon() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupWeaponTable()
    }

    private fun returnToPreBattle() {
        buttonTableHero.remove()
        setupPreBattleTable()
    }

    private fun returnToHeroForReposition() {
        buttonTableReposition.remove()
        setupHeroTableForReposition()
    }

    private fun returnToHeroForPrePreview() {
        buttonTableAttack.remove()
        setupHeroTableForPrePreview()
    }

    private fun returnToPrePreviewAttack() {
        buttonTableTarget.remove()
        setupPrePreviewAttackTable()
    }

    fun returnToAction() {
        buttonTableMove.remove()
        buttonTableAttack.remove()
        buttonTablePotion.remove()
        buttonTableWeapon.remove()
        setupActionTable()
    }

    private fun returnToPreviewAttack() {
        buttonTableTarget.remove()
        setupPreviewAttackTable()
    }

    private fun returnToAttack() {
        buttonTableTarget.remove()
        setupAttackTable()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun setupPreBattleTable() {
        buttonTablePreBattle = screenBuilder.createButtonTablePreBattle()
        setupTable(buttonTablePreBattle, preBattle)
    }

    private fun setupHeroTableForReposition() {
        buttonTableHero = screenBuilder.createButtonTableHero(turnManager.getOnlyHeroes())
        setupTable(buttonTableHero, heroForReposition)
    }

    private fun setupRepositionTable() {
        buttonTableReposition = screenBuilder.createButtonTableMove()
        setupTable(buttonTableReposition, reposition)
    }

    private fun setupHeroTableForPrePreview() {
        buttonTableHero = screenBuilder.createButtonTableHero(turnManager.getOnlyHeroes())
        setupTable(buttonTableHero, heroForPrePreview)
    }

    private fun setupPrePreviewAttackTable() {
        buttonTableAttack = screenBuilder.createButtonTablePreviewAttack(currentParticipant.invoke())
        setupTable(buttonTableAttack, prePreviewAttack)
    }

    private fun setupPrePreviewTargetTable(selectedAttack: BattleAbilityItem) {
        buttonTableTarget = screenBuilder.createButtonTableTarget(turnManager.getOnlyEnemies())
        prePreviewTarget.setSelectedAttack(selectedAttack)
        setupTable(buttonTableTarget, prePreviewTarget)
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
        setupTable(buttonTableMove, move)
    }

    private fun setupPreviewAttackTable() {
        buttonTableAttack = screenBuilder.createButtonTablePreviewAttack(currentParticipant.invoke())
        setupTable(buttonTableAttack, previewAttack)
    }

    private fun setupAttackTable() {
        buttonTableAttack = screenBuilder.createButtonTableAttack(currentParticipant.invoke())
        setupTable(buttonTableAttack, attack)
    }

    private fun setupPreviewTargetTable(selectedAttack: BattleAbilityItem) {
        buttonTableTarget = screenBuilder.createButtonTableTarget(turnManager.getOnlyEnemies())
        previewTarget.setSelectedAttack(selectedAttack)
        setupTable(buttonTableTarget, previewTarget)
    }

    private fun setupTargetTable(selectedAttack: BattleAbilityItem) {
        val targetableEnemies: List<Participant> = battleField.getTargetableEnemiesForActingHero()
        buttonTableTarget = screenBuilder.createButtonTableTarget(targetableEnemies)
        target.setSelectedAttack(selectedAttack)
        setupTable(buttonTableTarget, target)
    }

    private fun setupPotionTable() {
        val battlePotions: List<BattlePotionItem> = gameData.inventory.getAllOf(InventoryGroup.POTION)
            .filter { it.name.contains(" Potion") }
            .map { BattlePotionItem(it) }
        buttonTablePotion = screenBuilder.createButtonTablePotion(battlePotions)
        setupTable(buttonTablePotion, potion)
    }

    private fun setupWeaponTable() {
        val battleEquipment: List<BattleWeaponItem> =
            (gameData.inventory.getAllOf(InventoryGroup.WEAPON) + gameData.inventory.getAllOf(InventoryGroup.SHIELD))
                .map { BattleWeaponItem(it) }
        val currentWeapon: InventoryItem? = currentParticipant.invoke().character.getInventoryItem(InventoryGroup.WEAPON)
        val currentShield: InventoryItem? = currentParticipant.invoke().character.getInventoryItem(InventoryGroup.SHIELD)
        buttonTableWeapon = screenBuilder.createButtonTableWeapon(battleEquipment, currentWeapon, currentShield)
        setupTable(buttonTableWeapon, weapon)
    }

    private fun setupTable(table: Table, listener: InputListener) {
        stage.addActor(table)
        table.addListener(listener)
        stage.keyboardFocus = table.children.last()
    }

}
