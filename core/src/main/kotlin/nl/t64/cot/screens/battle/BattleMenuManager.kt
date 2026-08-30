package nl.t64.cot.screens.battle

import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.battle.*
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.abilities.AbilityItem
import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.components.party.abilities.Target
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.screens.battle.listeners.*
import com.badlogic.gdx.scenes.scene2d.ui.List as GdxList


private const val AP_UNAVAILABLE: Int = 99
private const val MOVE_AP: Int = 1
private const val REST_AP: Int = 1

private const val ATTACK: String = "Attack"
private const val MOVE: String = "Move"
private const val END_TURN: String = "End turn"
private const val BACK: String = "Back"

class BattleMenuManager(
    private val stage: Stage,
    private val screenBuilder: BattleScreenBuilder,
    private val turnManager: TurnManager,
    private val battleField: BattleField,
    private val currentParticipant: () -> Participant,
    private val menuHandlers: BattleMenuHandlers
) {

    private val currentHero: HeroItem get() = currentParticipant.invoke().character as HeroItem

    private val menuStack: ArrayDeque<() -> Unit> = ArrayDeque()
    private var currentMenu: () -> Unit = {}
    private var currentTable: Table = Table()
    private var mainMenuIndex: Int = 0
    private var heroMenuIndex: Int = 0
    private var attackMenuIndex: Int = 0

    private val preBattleMainMenuListener = SelectPreBattleListener(menuHandlers.winBattle, menuHandlers.openPauseMenu, menuHandlers.showInventoryScreenPreBattle)
    private val actionMainMenuListener = SelectActionListener(menuHandlers.winBattle, menuHandlers.openPauseMenu, menuHandlers.showInventoryScreen)
    private val preBattleEquipmentListener = SelectWeaponListener(menuHandlers.showConfirmWeaponDialogPreBattle, ::goBack)
    private val actionEquipmentListener = SelectWeaponListener(menuHandlers.showConfirmWeaponDialog, ::goBack)
    private val preBattleHeroForEquipmentListener = SelectHeroListener(menuHandlers.heroIsSelectedForPreEquipment, ::goBack)
    private val preBattleHeroForPotionListener = SelectHeroListener(menuHandlers.heroIsSelectedForPrePotion, ::goBack)
    private val preBattleHeroForPreviewListener = SelectHeroListener(menuHandlers.heroIsSelectedForPrePreview, ::goBack)
    private val previewAttacksListener = SelectAttackListener(::anAttackIsSelectedInPreview, ::goBack)
    private val actionAttackListener = SelectAttackListener(::anAttackIsSelectedInAttackList, ::goBack)
    private val actionSpecialListener = SelectAttackListener(::aSpecialIsSelectedInSpecialList, ::goBack)
    private val previewTargetListener = SelectTargetListener(menuHandlers.showPreviewDialog, ::goBack)
    private val actionAttackTargetListener = SelectTargetListener(menuHandlers.showConfirmAttackDialog, ::goBack)
    private val actionSpecialTargetListener = SelectTargetListener(menuHandlers.showConfirmSpecialDialog, ::goBack)
    private val actionMoveListener = SelectMoveListener(battleField::moveHeroLeft, battleField::moveHeroRight, menuHandlers.confirmMovement, ::goBack)
    private val stealthMovementListener = SelectMoveListener(battleField::moveHeroLeftWithStealth, battleField::moveHeroRightWithStealth, menuHandlers.confirmStealthMovement, battleField::cancelMovement)
    private val preBattlePotionListener = SelectPotionListener(menuHandlers.showConfirmPotionDialogPreBattle, ::goBack)
    private val actionPotionListener = SelectPotionListener(menuHandlers.showConfirmPotionDialog, ::goBack)

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun openPreBattleMenu() {
        mainMenuIndex = 0
        openRootMenu(::showPreBattleTable)
    }

    fun openStealthMovementMenu() {
        openRootMenu(::showStealthMovementTable)
    }

    fun possibleOpenActionMenu() {
        if (currentTable.hasParent()) return
        mainMenuIndex = 0
        openRootMenu(::showActionTable)
    }

    fun openWeaponMenuForSelectedHero() {
        openSubMenuFromHeroMenu(::showPreBattleWeaponTable)
    }

    fun openPotionMenuForSelectedHero() {
        openSubMenuFromHeroMenu { showPotionTable(preBattlePotionListener) }
    }

    fun openPreviewAttackMenuForSelectedHero() {
        openSubMenuFromHeroMenu(::showPreviewAttackTable)
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

    private fun anAttackIsSelectedInPreview(attack: BattleAbilityItem) {
        openSubMenuFromAttackMenu { showPreviewTargetTable(attack) }
    }

    private fun anAttackIsSelectedInAttackList(attack: BattleAbilityItem) {
        openSubMenuFromAttackMenu { showTargetTable(attack, actionAttackTargetListener) }
    }

    private fun aSpecialIsSelectedInSpecialList(special: BattleAbilityItem) {
        openSubMenuFromAttackMenu { showTargetTable(special, actionSpecialTargetListener) }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun showPreBattleTable() {
        val options: List<BattleMenuOption> = createPreBattleOptions()
        showTable(screenBuilder.createButtonTablePreBattle(options, mainMenuIndex), preBattleMainMenuListener)
    }

    private fun showStealthMovementTable() {
        battleField.setStartingSpace()
        val stealthMovementTable: Table =
            screenBuilder.createButtonTableStealthMovement(currentParticipant.invoke().character.name,
                                                           battleField.getFreeStealthStepsForHero())
        showTable(stealthMovementTable, stealthMovementListener)
    }

    private fun showHeroTable(selectHeroListener: SelectHeroListener) {
        val heroTable: Table = screenBuilder.createButtonTableHero(turnManager.getOnlyHeroes(), heroMenuIndex)
        showTable(heroTable, selectHeroListener)
    }

    private fun showActionTable() {
        battleField.cancelMovement()
        battleField.resetStartingSpace()
        val areEnemiesInRange: Boolean = battleField.getTargetableEnemiesForActingHero().isNotEmpty()
        val options: List<BattleMenuOption> = createActionOptions(areEnemiesInRange)
        moveCursorToUsableOption(options, areEnemiesInRange)
        showTable(screenBuilder.createButtonTableAction(options, mainMenuIndex), actionMainMenuListener)
    }

    private fun showMoveTable() {
        battleField.setStartingSpace()
        showTable(screenBuilder.createButtonTableMove(), actionMoveListener)
    }

    private fun showPreviewAttackTable() {
        val abilities: List<BattleAbilityItem> = createPreviewAbilities()
        moveCursorToUsableAbility(abilities)
        showTable(screenBuilder.createButtonTableAttack(abilities, attackMenuIndex), previewAttacksListener)
    }

    private fun showAttackTable() {
        val abilities: List<BattleAbilityItem> = createAttackAbilities()
        moveCursorToUsableAbility(abilities)
        showTable(screenBuilder.createButtonTableAttack(abilities, attackMenuIndex), actionAttackListener)
    }

    private fun showSpecialTable() {
        val abilities: List<BattleAbilityItem> = createSpecialAbilities()
        moveCursorToUsableAbility(abilities)
        showTable(screenBuilder.createButtonTableSpecial(abilities, attackMenuIndex), actionSpecialListener)
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

    private fun createPreBattleOptions(): List<BattleMenuOption> {
        return listOf(
            // @formatter:off
            BattleMenuOption("Party preparation", "", true, menuHandlers.showInventoryScreenPreBattle, playsConfirmSound = false),
            BattleMenuOption("Select equipment",  "", true, { openSubMenuFromMainMenu { showHeroTable(preBattleHeroForEquipmentListener) } }),
            BattleMenuOption("Drink potion",      "", true, { openSubMenuFromMainMenu { showHeroTable(preBattleHeroForPotionListener) } }),
            BattleMenuOption("Preview attacks",   "", true, { openSubMenuFromMainMenu { showHeroTable(preBattleHeroForPreviewListener) } }),
            BattleMenuOption("Start battle",      "", true, menuHandlers.startBattle)
            // @formatter:on
        )
    }

    private fun createActionOptions(areEnemiesInRange: Boolean): List<BattleMenuOption> {
        val participant: Participant = currentParticipant.invoke()
        val currentAp: Int = participant.currentAP
        val currentSp: Int = participant.character.currentSp
        val shownAp: Int = maxOf(1, currentAp)
        val fleeAp: Int = maxOf(shownAp, participant.maximumAP)
        val moveApRange: String = if (shownAp <= 1) "1" else "1-$shownAp"

        val canAttack: Boolean = currentAp >= participant.getCheapestUsableAttackAp(areEnemiesInRange)
        val canUseSpecial: Boolean = currentAp >= participant.getCheapestUsableSpecialAp()
        val canMove: Boolean = battleField.getModifiedApForHero() > 0 && currentAp >= MOVE_AP
        val canSwitchEquipment: Boolean = currentAp >= SWITCH_WEAPON_AP
        val canDrinkPotion: Boolean = currentAp >= POTION_AP
        val canFlee: Boolean = currentAp >= fleeAp
        val canDelayTurn: Boolean = currentAp >= DELAY_AP
        val canPushOn: Boolean = currentSp >= PUSH_ON_SP
        val canRest: Boolean = currentAp >= REST_AP

        return listOf(
            // @formatter:off
            BattleMenuOption(ATTACK,       "? AP",                 canAttack,          { openSubMenuFromMainMenu(::showAttackTable) }),
            BattleMenuOption("Special",    "? AP",                 canUseSpecial,      { openSubMenuFromMainMenu(::showSpecialTable) }),
            BattleMenuOption(MOVE,         "$moveApRange AP",      canMove,            { openSubMenuFromMainMenu(::showMoveTable) }),
            BattleMenuOption("Equipment",  "$SWITCH_WEAPON_AP AP", canSwitchEquipment, { openSubMenuFromMainMenu(::showActionWeaponTable) }),
            BattleMenuOption("Potion",     "$POTION_AP AP",        canDrinkPotion,     { openSubMenuFromMainMenu { showPotionTable(actionPotionListener) } }),
            BattleMenuOption("Preview",    "",                     true,               { openSubMenuFromMainMenu(::showPreviewAttackTable) }),
            BattleMenuOption("Party",      "",                     true,               menuHandlers.showInventoryScreen,   playsConfirmSound = false),
            BattleMenuOption("Flee party", "$fleeAp AP",           canFlee,            menuHandlers.showFleeDialog,        playsConfirmSound = false),
            BattleMenuOption("Delay turn", "$DELAY_AP AP",         canDelayTurn,       menuHandlers.showDelayTurnDialog,   playsConfirmSound = false),
            BattleMenuOption("Push on",    "$PUSH_ON_SP SP",       canPushOn,          menuHandlers.showPushOnDialog,      playsConfirmSound = false),
            BattleMenuOption("Rest",       "$shownAp AP",          canRest,            menuHandlers.showConfirmRestDialog, playsConfirmSound = false),
            BattleMenuOption(END_TURN,     "",                     true,               menuHandlers.endTurn)
            // @formatter:on
        )
    }

    private fun createPreviewAbilities(): List<BattleAbilityItem> {
        val participant: Participant = currentParticipant.invoke()
        val abilities: List<BattleAbilityItem> = participant.getBattleAbilities()
            .filterNot { it.abilityItem.isSpecial }
            .map { it.createCopyForPreview() }
        return abilities + createBackButton(participant)
    }

    private fun createAttackAbilities(): List<BattleAbilityItem> {
        val participant: Participant = currentParticipant.invoke()
        val abilities: List<BattleAbilityItem> = participant.getBattleAbilities()
            .filterNot { it.abilityItem.isSpecial }
        return abilities + createBackButton(participant)
    }

    private fun createSpecialAbilities(): List<BattleAbilityItem> {
        val participant: Participant = currentParticipant.invoke()
        val abilities: List<BattleAbilityItem> = participant.getBattleAbilities()
            .filter { it.abilityItem.isSpecial }
        return abilities + createBackButton(participant)
    }

    private fun createBackButton(participant: Participant): BattleAbilityItem {
        return object : BattleAbilityItem(AbilityItem(name = BACK), participant) {
            override fun createCopyForPreview(): BattleAbilityItem = this
            override fun createPreviewMessage(): String = ""
            override fun handleSuccess(attackData: AttackData) {}
        }
    }

    private fun moveCursorToUsableAbility(abilities: List<BattleAbilityItem>) {
        if (attackMenuIndex > abilities.lastIndex || !abilities[attackMenuIndex].isSelectable()) {
            attackMenuIndex = abilities.indexOfFirst { it.isSelectable() }
        }
    }

    private fun BattleAbilityItem.isSelectable(): Boolean {
        return name == BACK || isUsable()
    }

    private fun moveCursorToUsableOption(options: List<BattleMenuOption>, areEnemiesInRange: Boolean) {
        if (!areEnemiesInRange && options[mainMenuIndex].name == ATTACK) {
            mainMenuIndex = options.indexOfFirst { it.name == MOVE }
        }
        if (currentParticipant.invoke().currentAP <= 1 || !options[mainMenuIndex].isEnabled) {
            mainMenuIndex = options.indexOfFirst { it.name == END_TURN }
        }
    }

    private fun Participant.getCheapestUsableAttackAp(areEnemiesInRange: Boolean): Int {
        if (!areEnemiesInRange) return AP_UNAVAILABLE

        return getCheapestUsableAp { !it.abilityItem.isSpecial }
    }

    private fun Participant.getCheapestUsableSpecialAp(): Int {
        return getCheapestUsableAp { it.abilityItem.isSpecial }
    }

    private fun Participant.getCheapestUsableAp(isWantedAbility: (BattleAbilityItem) -> Boolean): Int {
        return getBattleAbilities()
            .filter(isWantedAbility)
            .filter { it.isWeaponAllowed() && it.hasEnoughApSp() }
            .minOfOrNull { it.ap }
            ?: AP_UNAVAILABLE
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun openRootMenu(menu: () -> Unit) {
        menuStack.clear()
        openMenu(menu)
    }

    private fun openSubMenuFromMainMenu(menu: () -> Unit) {
        mainMenuIndex = currentSelectedIndex
        openSubMenu(menu)
    }

    private fun openSubMenuFromHeroMenu(menu: () -> Unit) {
        heroMenuIndex = currentSelectedIndex
        openSubMenu(menu)
    }

    private fun openSubMenuFromAttackMenu(menu: () -> Unit) {
        attackMenuIndex = currentSelectedIndex
        openSubMenu(menu)
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

    private val currentSelectedIndex: Int
        get() = (currentTable.children.last() as GdxList<*>).selectedIndex

}
