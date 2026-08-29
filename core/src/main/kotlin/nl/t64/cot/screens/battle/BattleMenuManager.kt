package nl.t64.cot.screens.battle

import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.battle.BattleField
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.components.battle.TurnManager
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.components.party.abilities.Target
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

    private val currentHero: HeroItem get() = currentParticipant.invoke().character as HeroItem

    private val menuStack: ArrayDeque<() -> Unit> = ArrayDeque()
    private var currentMenu: () -> Unit = {}
    private var currentTable: Table = Table()

    private lateinit var preBattleMainMenuListener: SelectPreBattleListener
    private lateinit var actionMainMenuListener: SelectActionListener
    private lateinit var preBattleEquipmentListener: SelectWeaponListener
    private lateinit var actionEquipmentListener: SelectWeaponListener
    private lateinit var preBattleHeroForEquipmentListener: SelectHeroListener
    private lateinit var preBattleHeroForPotionListener: SelectHeroListener
    private lateinit var preBattleHeroForPreviewListener: SelectHeroListener
    private lateinit var previewAttacksListener: SelectAttackListener
    private lateinit var actionAttackListener: SelectAttackListener
    private lateinit var actionSpecialListener: SelectAttackListener
    private lateinit var previewTargetListener: SelectTargetListener
    private lateinit var actionAttackTargetListener: SelectTargetListener
    private lateinit var actionSpecialTargetListener: SelectTargetListener
    private lateinit var actionMoveListener: SelectMoveListener
    private lateinit var stealthMovementListener: SelectMoveListener
    private lateinit var preBattlePotionListener: SelectPotionListener
    private lateinit var actionPotionListener: SelectPotionListener

    fun setListeners(
        winBattle: () -> Unit,
        openPauseMenu: () -> Unit,
        showInventoryScreenPreBattle: () -> Unit,
        showInventoryScreen: () -> Unit,
        startBattle: () -> Unit,
        heroIsSelectedForPreEquipment: (String) -> Unit,
        heroIsSelectedForPrePotion: (String) -> Unit,
        heroIsSelectedForPrePreview: (String) -> Unit,
        showPreviewDialog: (BattleAbilityItem, String) -> Unit,
        showFleeDialog: () -> Unit,
        showDelayTurnDialog: () -> Unit,
        showPushOnDialog: () -> Unit,
        showConfirmRestDialog: () -> Unit,
        endTurn: () -> Unit,
        confirmMovement: () -> Unit,
        confirmStealthMovement: () -> Unit,
        showConfirmAttackDialog: (BattleAbilityItem, String) -> Unit,
        showConfirmSpecialDialog: (BattleAbilityItem, String) -> Unit,
        showConfirmPotionDialogPreBattle: (BattlePotionItem) -> Unit,
        showConfirmPotionDialog: (BattlePotionItem) -> Unit,
        showConfirmWeaponDialogPreBattle: (BattleWeaponItem) -> Unit,
        showConfirmWeaponDialog: (BattleWeaponItem) -> Unit
    ) {
        preBattleMainMenuListener = SelectPreBattleListener(winBattle, openPauseMenu, showInventoryScreenPreBattle, ::equipmentIsSelectedInPreBattle, ::potionIsSelectedInPreBattle, ::previewIsSelectedInPreBattle, startBattle)
        actionMainMenuListener = SelectActionListener(winBattle, openPauseMenu, ::attackIsSelectedInAction, ::specialIsSelectedInAction, ::moveIsSelectedInAction, ::potionIsSelectedInAction, ::equipmentIsSelectedInAction, ::previewIsSelectedInAction, showInventoryScreen, showFleeDialog, showDelayTurnDialog, showPushOnDialog, showConfirmRestDialog, endTurn)
        preBattleEquipmentListener = SelectWeaponListener(showConfirmWeaponDialogPreBattle, ::goBack)
        actionEquipmentListener = SelectWeaponListener(showConfirmWeaponDialog, ::goBack)
        preBattleHeroForEquipmentListener = SelectHeroListener(heroIsSelectedForPreEquipment, ::goBack)
        preBattleHeroForPotionListener = SelectHeroListener(heroIsSelectedForPrePotion, ::goBack)
        preBattleHeroForPreviewListener = SelectHeroListener(heroIsSelectedForPrePreview, ::goBack)
        previewAttacksListener = SelectAttackListener(::anAttackIsSelectedInPreview, ::goBack)
        actionAttackListener = SelectAttackListener(::anAttackIsSelectedInAttackList, ::goBack)
        actionSpecialListener = SelectAttackListener(::aSpecialIsSelectedInSpecialList, ::goBack)
        previewTargetListener = SelectTargetListener(showPreviewDialog, ::goBack)
        actionAttackTargetListener = SelectTargetListener(showConfirmAttackDialog, ::goBack)
        actionSpecialTargetListener = SelectTargetListener(showConfirmSpecialDialog, ::goBack)
        actionMoveListener = SelectMoveListener(battleField::moveHeroLeft, battleField::moveHeroRight, confirmMovement, ::goBack)
        stealthMovementListener = SelectMoveListener(battleField::moveHeroLeftWithStealth, battleField::moveHeroRightWithStealth, confirmStealthMovement, battleField::cancelMovement)
        preBattlePotionListener = SelectPotionListener(showConfirmPotionDialogPreBattle, ::goBack)
        actionPotionListener = SelectPotionListener(showConfirmPotionDialog, ::goBack)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun openPreBattleMenu() {
        screenBuilder.buttonTableMainMenuIndex = 0
        openRootMenu(::showPreBattleTable)
    }

    fun openStealthMovementMenu() {
        openRootMenu(::showStealthMovementTable)
    }

    fun possibleOpenActionMenu() {
        if (currentTable.hasParent()) return
        screenBuilder.buttonTableMainMenuIndex = 0
        openRootMenu(::showActionTable)
    }

    fun openWeaponMenuForSelectedHero() {
        rememberSelectHeroIndex()
        openSubMenu(::showPreBattleWeaponTable)
    }

    fun openPotionMenuForSelectedHero() {
        rememberSelectHeroIndex()
        openSubMenu { showPotionTable(preBattlePotionListener) }
    }

    fun openPreviewAttackMenuForSelectedHero() {
        rememberSelectHeroIndex()
        openSubMenu(::showPreviewAttackTable)
    }

    fun goBack() {
        openMenu(menuStack.removeLast())
    }

    fun reopenCurrentMenu() {
        openMenu(currentMenu)
    }

    fun closeMenu() {
        currentTable.remove()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun equipmentIsSelectedInPreBattle() {
        rememberMainMenuIndex()
        openSubMenu { showHeroTable(preBattleHeroForEquipmentListener) }
    }

    private fun potionIsSelectedInPreBattle() {
        rememberMainMenuIndex()
        openSubMenu { showHeroTable(preBattleHeroForPotionListener) }
    }

    private fun previewIsSelectedInPreBattle() {
        rememberMainMenuIndex()
        openSubMenu { showHeroTable(preBattleHeroForPreviewListener) }
    }

    private fun attackIsSelectedInAction() {
        rememberMainMenuIndex()
        openSubMenu(::showAttackTable)
    }

    private fun specialIsSelectedInAction() {
        rememberMainMenuIndex()
        openSubMenu(::showSpecialTable)
    }

    private fun moveIsSelectedInAction() {
        rememberMainMenuIndex()
        openSubMenu(::showMoveTable)
    }

    private fun potionIsSelectedInAction() {
        rememberMainMenuIndex()
        openSubMenu { showPotionTable(actionPotionListener) }
    }

    private fun equipmentIsSelectedInAction() {
        rememberMainMenuIndex()
        openSubMenu(::showActionWeaponTable)
    }

    private fun previewIsSelectedInAction() {
        rememberMainMenuIndex()
        openSubMenu(::showPreviewAttackTable)
    }

    private fun anAttackIsSelectedInPreview(attack: BattleAbilityItem) {
        rememberSelectAttackIndex()
        openSubMenu { showPreviewTargetTable(attack) }
    }

    private fun anAttackIsSelectedInAttackList(attack: BattleAbilityItem) {
        rememberSelectAttackIndex()
        openSubMenu { showTargetTable(attack, actionAttackTargetListener) }
    }

    private fun aSpecialIsSelectedInSpecialList(special: BattleAbilityItem) {
        rememberSelectAttackIndex()
        openSubMenu { showTargetTable(special, actionSpecialTargetListener) }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun showPreBattleTable() {
        showTable(screenBuilder.createButtonTablePreBattle(), preBattleMainMenuListener)
    }

    private fun showStealthMovementTable() {
        battleField.setStartingSpace()
        val stealthMovementTable: Table =
            screenBuilder.createButtonTableStealthMovement(currentParticipant.invoke().character.name,
                                                           battleField.getFreeStealthStepsForHero())
        showTable(stealthMovementTable, stealthMovementListener)
    }

    private fun showHeroTable(selectHeroListener: SelectHeroListener) {
        showTable(screenBuilder.createButtonTableHero(turnManager.getOnlyHeroes()), selectHeroListener)
    }

    private fun showActionTable() {
        battleField.cancelMovement()
        battleField.resetStartingSpace()
        val areEnemiesInRange: Boolean = battleField.getTargetableEnemiesForActingHero().isNotEmpty()
        val isAbleToMove: Boolean = battleField.getModifiedApForHero() > 0
        val actionTable: Table =
            screenBuilder.createButtonTableAction(currentParticipant.invoke(), areEnemiesInRange, isAbleToMove)
        showTable(actionTable, actionMainMenuListener)
    }

    private fun showMoveTable() {
        battleField.setStartingSpace()
        showTable(screenBuilder.createButtonTableMove(), actionMoveListener)
    }

    private fun showPreviewAttackTable() {
        showTable(screenBuilder.createButtonTablePreviewAttack(currentParticipant.invoke()), previewAttacksListener)
    }

    private fun showAttackTable() {
        showTable(screenBuilder.createButtonTableAttack(currentParticipant.invoke()), actionAttackListener)
    }

    private fun showSpecialTable() {
        showTable(screenBuilder.createButtonTableSpecial(currentParticipant.invoke()), actionSpecialListener)
    }

    private fun showPreviewTargetTable(selectedAttack: BattleAbilityItem) {
        val allTargetNames: List<String> = turnManager.getOnlyEnemies().map { it.character.name }
        previewTargetListener.setSelectedAttack(selectedAttack)
        showTable(screenBuilder.createButtonTableTarget(allTargetNames), previewTargetListener)
    }

    private fun showTargetTable(selectedAttack: BattleAbilityItem, targetListener: SelectTargetListener) {
        val availableTargets: List<String> =
            when (selectedAttack.abilityItem.target) {
                Target.SELF -> TODO()
                Target.SELF_OR_ALLY -> turnManager.getOnlyHeroes().map { it.character.name }
                Target.ALLY_ANYWHERE -> turnManager.getOnlyAllies().map { it.character.name }
                Target.ALLY_RANGE_3 -> battleField.getTargetableAlliesInRangeOfActingHero(3).map { it.character.name }
                Target.ALLY_NEAR -> battleField.getTargetableAlliesNextToActingHero().map { it.character.name }
                Target.ALL_ALLIES -> listOf("All allies")
                Target.ENEMY_ANYWHERE -> turnManager.getOnlyEnemies().map { it.character.name }
                Target.ENEMY_RANGE_3 -> battleField.getTargetableEnemiesInRangeOfActingHero(3).map { it.character.name }
                Target.ENEMY -> battleField.getTargetableEnemiesForActingHero().map { it.character.name }
                Target.ALL_ENEMIES -> listOf("All enemies")
                Target.AREA -> TODO()
            }
        targetListener.setSelectedAttack(selectedAttack)
        showTable(screenBuilder.createButtonTableTarget(availableTargets), targetListener)
    }

    private fun showPotionTable(potionListener: SelectPotionListener) {
        val potions: List<InventoryItem> = gameData.inventory.getAllOf(InventoryGroup.POTION)
            .filter { it.name.contains(" Potion") }
        showTable(screenBuilder.createButtonTablePotion(potions), potionListener)
    }

    private fun showPreBattleWeaponTable() {
        val weaponsAndShield: List<InventoryItem> = currentHero.getAllWeaponsAbleToEquip() + currentHero.getAllShieldsAbleToEquip()
        showWeaponTable(weaponsAndShield, preBattleEquipmentListener)
    }

    private fun showActionWeaponTable() {
        val weapons: List<InventoryItem> = currentHero.getAllWeaponsAbleToEquip()
        showWeaponTable(weapons, actionEquipmentListener)
    }

    private fun showWeaponTable(equipment: List<InventoryItem>, weaponListener: SelectWeaponListener) {
        val currentWeapon: InventoryItem? = currentHero.getInventoryItem(InventoryGroup.WEAPON)
        val currentShield: InventoryItem? = currentHero.getInventoryItem(InventoryGroup.SHIELD)
        val weaponTable: Table = screenBuilder.createButtonTableWeapon(equipment, currentWeapon, currentShield)
        showTable(weaponTable, weaponListener)
    }

    private fun HeroItem.getAllWeaponsAbleToEquip(): List<InventoryItem> {
        return gameData.inventory.getAllOf(InventoryGroup.WEAPON)
            .filter { this.createMessageIfHeroHasNotEnoughFor(it) == null }
    }

    private fun HeroItem.getAllShieldsAbleToEquip(): List<InventoryItem> {
        return gameData.inventory.getAllOf(InventoryGroup.SHIELD)
            .filter { this.createMessageIfHeroHasNotEnoughFor(it) == null }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun openRootMenu(menu: () -> Unit) {
        menuStack.clear()
        openMenu(menu)
    }

    private fun openSubMenu(menu: () -> Unit) {
        menuStack.addLast(currentMenu)
        openMenu(menu)
    }

    private fun openMenu(menu: () -> Unit) {
        currentTable.remove()
        currentMenu = menu
        menu.invoke()
    }

    private fun showTable(table: Table, listener: InputListener) {
        currentTable = table
        stage.addActor(table)
        table.addListener(listener)
        stage.keyboardFocus = table.children.last()
    }

    private fun rememberMainMenuIndex() {
        screenBuilder.buttonTableMainMenuIndex = currentSelectedIndex
    }

    private fun rememberSelectHeroIndex() {
        screenBuilder.buttonTableSelectHeroIndex = currentSelectedIndex
    }

    private fun rememberSelectAttackIndex() {
        screenBuilder.buttonTableSelectAttackIndex = currentSelectedIndex
    }

    private val currentSelectedIndex: Int
        get() = (currentTable.children.last() as GdxList<*>).selectedIndex

}
