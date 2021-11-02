package nl.t64.cot.screens.inventory

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Window
import nl.t64.cot.Utils.createDefaultWindow
import nl.t64.cot.screens.ScreenUI
import nl.t64.cot.screens.inventory.equipslot.EquipSlotsTables
import nl.t64.cot.screens.inventory.inventoryslot.InventorySlotsTable
import nl.t64.cot.screens.inventory.tooltip.ItemSlotTooltip
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val INVENTORY_WINDOW_POSITION_X = 1472f
private const val INVENTORY_WINDOW_POSITION_Y = 50f
private const val EQUIP_WINDOW_POSITION_X = 1162f
private const val EQUIP_WINDOW_POSITION_Y = 50f
private const val SPELLS_WINDOW_POSITION_X = 770f
private const val SPELLS_WINDOW_POSITION_Y = 50f
private const val WEAPON_SKILLS_WINDOW_POSITION_X = 770f
private const val WEAPON_SKILLS_WINDOW_POSITION_Y = 441f
private const val COMBAT_SKILLS_WINDOW_POSITION_X = 378f
private const val COMBAT_SKILLS_WINDOW_POSITION_Y = 50f
private const val CIVIL_SKILLS_WINDOW_POSITION_X = 378f
private const val CIVIL_SKILLS_WINDOW_POSITION_Y = 441f
private const val CALCS_WINDOW_POSITION_X = 63f
private const val CALCS_WINDOW_POSITION_Y = 50f
private const val STATS_WINDOW_POSITION_X = 63f
private const val STATS_WINDOW_POSITION_Y = 428f
private const val HEROES_WINDOW_POSITION_X = 63f
private const val HEROES_WINDOW_POSITION_Y = 834f

private const val TITLE_GLOBAL = "   Inventory"
private const val TITLE_PERSONAL = "   Equipment"
private const val TITLE_SPELLS = "   Spells"
private const val TITLE_WEAPON_SKILLS = "   Weapon Skills"
private const val TITLE_COMBAT_SKILLS = "   Combat Skills"
private const val TITLE_CIVIL_SKILLS = "   Civil Skills"
private const val TITLE_CALCS = "   Calculations"
private const val TITLE_STATS = "   Stats"
private const val TITLE_HEROES = "   Heroes"

internal class InventoryUI(
    stage: Stage,

    private val itemSlotTooltip: ItemSlotTooltip = ItemSlotTooltip(),
    private val personalityTooltip: PersonalityTooltip = PersonalityTooltip(),

    private val inventorySlotsTable: InventorySlotsTable = InventorySlotsTable(itemSlotTooltip),
    private val inventoryWindow: Window = createDefaultWindow(TITLE_GLOBAL, inventorySlotsTable.container),

    private val equipSlotsTables: EquipSlotsTables = EquipSlotsTables(itemSlotTooltip),
    private val equipWindow: Window = createDefaultWindow(TITLE_PERSONAL, equipSlotsTables.getCurrentEquipTable()),

    private val spellsTable: SpellsTable = SpellsTable(personalityTooltip),
    private val spellsWindow: Window = createDefaultWindow(TITLE_SPELLS, spellsTable.container),

    private val weaponSkillsTable: SkillsTable = SkillsTable({ it.isWeaponSkill() }, personalityTooltip),
    private val weaponSkillsWindow: Window = createDefaultWindow(TITLE_WEAPON_SKILLS, weaponSkillsTable.container),

    private val combatSkillsTable: SkillsTable = SkillsTable({ it.isCombatSkill() }, personalityTooltip),
    private val combatSkillsWindow: Window = createDefaultWindow(TITLE_COMBAT_SKILLS, combatSkillsTable.container),

    private val civilSkillsTable: SkillsTable = SkillsTable({ it.isCivilSkill() }, personalityTooltip),
    private val civilSkillsWindow: Window = createDefaultWindow(TITLE_CIVIL_SKILLS, civilSkillsTable.container),

    private val calcsTable: CalcsTable = CalcsTable(personalityTooltip),
    private val calcsWindow: Window = createDefaultWindow(TITLE_CALCS, calcsTable.container),

    private val statsTable: StatsTable = StatsTable(personalityTooltip),
    private val statsWindow: Window = createDefaultWindow(TITLE_STATS, statsTable.container),

    heroesTable: HeroesTable = HeroesTable(),
    private val heroesWindow: Window = createDefaultWindow(TITLE_HEROES, heroesTable.heroes),

    tableList: List<WindowSelector> = listOf(statsTable, calcsTable, civilSkillsTable, combatSkillsTable,
                                             weaponSkillsTable, spellsTable, equipSlotsTables, inventorySlotsTable),
    selectedTableIndex: Int = 6

) : ScreenUI(stage, heroesTable, tableList, selectedTableIndex) {

    init {
        super.init()
    }

    override fun getEquipSlotsTables(): EquipSlotsTables {
        return equipSlotsTables
    }

    override fun getInventorySlotsTable(): InventorySlotsTable {
        return inventorySlotsTable
    }

    fun updateSelectedHero(updateHero: () -> Unit) {
        getSelectedTable().deselectCurrentSlot()

        val oldCurrentIndex = equipSlotsTables.getIndexOfCurrentSlot()
        equipWindow.getChild(1).remove()
        updateHero.invoke()
        equipWindow.add(equipSlotsTables.getCurrentEquipTable())
        equipSlotsTables.setCurrentByIndex(oldCurrentIndex)

        setFocusOnSelectedTable()
        getSelectedTable().selectCurrentSlot()
    }

    fun doAction() {
        getSelectedTable().doAction()
    }

    fun reloadInventory() {
        inventorySlotsTable.clearAndFill()
    }

    fun update() {
        spellsTable.update()
        weaponSkillsTable.update()
        combatSkillsTable.update()
        civilSkillsTable.update()
        calcsTable.update()
        statsTable.update()
        heroesTable.update()

        spellsWindow.pack()
        weaponSkillsWindow.pack()
        combatSkillsWindow.pack()
        civilSkillsWindow.pack()
        calcsWindow.pack()
        statsWindow.pack()
        heroesWindow.pack()
    }

    override fun setWindowPositions() {
        inventoryWindow.setPosition(INVENTORY_WINDOW_POSITION_X, INVENTORY_WINDOW_POSITION_Y)
        equipWindow.setPosition(EQUIP_WINDOW_POSITION_X, EQUIP_WINDOW_POSITION_Y)
        spellsWindow.setPosition(SPELLS_WINDOW_POSITION_X, SPELLS_WINDOW_POSITION_Y)
        weaponSkillsWindow.setPosition(WEAPON_SKILLS_WINDOW_POSITION_X, WEAPON_SKILLS_WINDOW_POSITION_Y)
        combatSkillsWindow.setPosition(COMBAT_SKILLS_WINDOW_POSITION_X, COMBAT_SKILLS_WINDOW_POSITION_Y)
        civilSkillsWindow.setPosition(CIVIL_SKILLS_WINDOW_POSITION_X, CIVIL_SKILLS_WINDOW_POSITION_Y)
        calcsWindow.setPosition(CALCS_WINDOW_POSITION_X, CALCS_WINDOW_POSITION_Y)
        statsWindow.setPosition(STATS_WINDOW_POSITION_X, STATS_WINDOW_POSITION_Y)
        heroesWindow.setPosition(HEROES_WINDOW_POSITION_X, HEROES_WINDOW_POSITION_Y)
    }

    override fun addToStage() {
        itemSlotTooltip.addToStage(stage)
        personalityTooltip.addToStage(stage)
        stage.addActor(inventoryWindow)
        stage.addActor(equipWindow)
        stage.addActor(spellsWindow)
        stage.addActor(weaponSkillsWindow)
        stage.addActor(combatSkillsWindow)
        stage.addActor(civilSkillsWindow)
        stage.addActor(calcsWindow)
        stage.addActor(statsWindow)
        stage.addActor(heroesWindow)
    }

}
