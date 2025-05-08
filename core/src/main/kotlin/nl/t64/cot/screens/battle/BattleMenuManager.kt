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
import com.badlogic.gdx.scenes.scene2d.ui.List as GdxList


class BattleMenuManager(
    private val stage: Stage,
    private val screenBuilder: BattleScreenBuilder,
    private val listeners: BattleListeners,
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

    init {
        listeners.setMenuListeners(
            ::selectPrePreviewAttack, ::selectPreviewAttack, ::selectAttack, ::selectPotion, ::selectWeapon,
            ::attackInPrePreviewIsSelected, ::attackInPreviewIsSelected, ::attackIsSelected,
            ::selectReposition, ::selectMove,
            ::returnToPreBattle, ::returnToHeroForReposition, ::returnToHeroForPrePreview,
            ::returnToPrePreviewAttack, ::returnToPreviewAttack, ::returnToAttack, ::returnToAction
        )

        listeners.setBattleFieldListeners(
            battleField::repositionHeroLeft, battleField::repositionHeroRight,
            battleField::moveHeroLeft, battleField::moveHeroRight
        )
    }

    fun possibleSetupActionTable() {
        val isNoButtonTableVisible = stage.actors.items.none { it in allButtonTables }
        if (isNoButtonTableVisible) {
            setupActionTable()
        }
    }


    fun selectReposition() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTablePreBattle.children.last() as GdxList<*>).selectedIndex
        buttonTablePreBattle.remove()
        setupHeroTableForReposition()
    }

    fun selectPrePreviewAttack() {
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

    fun attackInPrePreviewIsSelected(attack: BattleAbilityItem) {
        screenBuilder.buttonTableSelectAttackIndex = (buttonTableAttack.children.last() as GdxList<*>).selectedIndex
        buttonTableAttack.remove()
        setupPrePreviewTargetTable(attack)
    }

    fun selectMove() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupMoveTable()
    }

    fun selectPreviewAttack() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupPreviewAttackTable()
    }

    fun attackInPreviewIsSelected(attack: BattleAbilityItem) {
        screenBuilder.buttonTableSelectAttackIndex = (buttonTableAttack.children.last() as GdxList<*>).selectedIndex
        buttonTableAttack.remove()
        setupPreviewTargetTable(attack)
    }

    fun selectAttack() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupAttackTable()
    }

    fun attackIsSelected(attack: BattleAbilityItem) {
        screenBuilder.buttonTableSelectAttackIndex = (buttonTableAttack.children.last() as GdxList<*>).selectedIndex
        buttonTableAttack.remove()
        setupTargetTable(attack)
    }

    fun selectPotion() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupPotionTable()
    }

    fun selectWeapon() {
        screenBuilder.buttonTableMainMenuIndex = (buttonTableAction.children.last() as GdxList<*>).selectedIndex
        buttonTableAction.remove()
        setupWeaponTable()
    }

    fun returnToPreBattle() {
        buttonTableHero.remove()
        setupPreBattleTable()
    }

    fun returnToHeroForReposition() {
        buttonTableReposition.remove()
        setupHeroTableForReposition()
    }

    fun returnToHeroForPrePreview() {
        buttonTableAttack.remove()
        setupHeroTableForPrePreview()
    }

    fun returnToPrePreviewAttack() {
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

    fun returnToPreviewAttack() {
        buttonTableTarget.remove()
        setupPreviewAttackTable()
    }

    fun returnToAttack() {
        buttonTableTarget.remove()
        setupAttackTable()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun setupPreBattleTable() {
        buttonTablePreBattle = screenBuilder.createButtonTablePreBattle()
        setupTable(buttonTablePreBattle, listeners.preBattle)
    }

    private fun setupHeroTableForReposition() {
        buttonTableHero = screenBuilder.createButtonTableHero(turnManager.getOnlyHeroes())
        setupTable(buttonTableHero, listeners.heroForReposition)
    }

    private fun setupRepositionTable() {
        buttonTableReposition = screenBuilder.createButtonTableMove()
        setupTable(buttonTableReposition, listeners.reposition)
    }

    private fun setupHeroTableForPrePreview() {
        buttonTableHero = screenBuilder.createButtonTableHero(turnManager.getOnlyHeroes())
        setupTable(buttonTableHero, listeners.heroForPrePreview)
    }

    private fun setupPrePreviewAttackTable() {
        buttonTableAttack = screenBuilder.createButtonTablePreviewAttack(currentParticipant.invoke())
        setupTable(buttonTableAttack, listeners.prePreviewAttack)
    }

    private fun setupPrePreviewTargetTable(selectedAttack: BattleAbilityItem) {
        buttonTableTarget = screenBuilder.createButtonTableTarget(turnManager.getOnlyEnemies())
        listeners.prePreviewTarget.setSelectedAttack(selectedAttack)
        setupTable(buttonTableTarget, listeners.prePreviewTarget)
    }

    private fun setupActionTable() {
        battleField.cancelMovement()
        battleField.resetStartingSpace()
        val areEnemiesInRange: Boolean = battleField.getTargetableEnemiesForActingHero().isNotEmpty()
        buttonTableAction = screenBuilder.createButtonTableAction(currentParticipant.invoke(), areEnemiesInRange)
        setupTable(buttonTableAction, listeners.action)
    }

    private fun setupMoveTable() {
        battleField.setStartingSpace()
        buttonTableMove = screenBuilder.createButtonTableMove()
        setupTable(buttonTableMove, listeners.move)
    }

    private fun setupPreviewAttackTable() {
        buttonTableAttack = screenBuilder.createButtonTablePreviewAttack(currentParticipant.invoke())
        setupTable(buttonTableAttack, listeners.previewAttack)
    }

    private fun setupAttackTable() {
        buttonTableAttack = screenBuilder.createButtonTableAttack(currentParticipant.invoke())
        setupTable(buttonTableAttack, listeners.attack)
    }

    private fun setupPreviewTargetTable(selectedAttack: BattleAbilityItem) {
        buttonTableTarget = screenBuilder.createButtonTableTarget(turnManager.getOnlyEnemies())
        listeners.previewTarget.setSelectedAttack(selectedAttack)
        setupTable(buttonTableTarget, listeners.previewTarget)
    }

    private fun setupTargetTable(selectedAttack: BattleAbilityItem) {
        val targetableEnemies: List<Participant> = battleField.getTargetableEnemiesForActingHero()
        buttonTableTarget = screenBuilder.createButtonTableTarget(targetableEnemies)
        listeners.target.setSelectedAttack(selectedAttack)
        setupTable(buttonTableTarget, listeners.target)
    }

    private fun setupPotionTable() {
        val battlePotions: List<BattlePotionItem> = gameData.inventory.getAllOf(InventoryGroup.POTION)
            .filter { it.name.contains(" Potion") }
            .map { BattlePotionItem(it) }
        buttonTablePotion = screenBuilder.createButtonTablePotion(battlePotions)
        setupTable(buttonTablePotion, listeners.potion)
    }

    private fun setupWeaponTable() {
        val battleEquipment: List<BattleWeaponItem> =
            (gameData.inventory.getAllOf(InventoryGroup.WEAPON) + gameData.inventory.getAllOf(InventoryGroup.SHIELD))
                .map { BattleWeaponItem(it) }
        val currentWeapon: InventoryItem? = currentParticipant.invoke().character.getInventoryItem(InventoryGroup.WEAPON)
        val currentShield: InventoryItem? = currentParticipant.invoke().character.getInventoryItem(InventoryGroup.SHIELD)
        buttonTableWeapon = screenBuilder.createButtonTableWeapon(battleEquipment, currentWeapon, currentShield)
        setupTable(buttonTableWeapon, listeners.weapon)
    }

    private fun setupTable(table: Table, listener: InputListener) {
        stage.addActor(table)
        table.addListener(listener)
        stage.keyboardFocus = table.children.last()
    }

}
