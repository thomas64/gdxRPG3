package nl.t64.cot.screens.inventory

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Window
import nl.t64.cot.Utils.createDefaultWindow
import nl.t64.cot.screens.ScreenUI
import nl.t64.cot.screens.inventory.equipslot.EquipSlotsTables
import nl.t64.cot.screens.inventory.inventoryslot.InventorySlotsTable
import nl.t64.cot.screens.inventory.tooltip.ItemSlotTooltip
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val SPELLS_WINDOW_POSITION_X = 1483f
private const val SPELLS_WINDOW_POSITION_Y = 50f
private const val INVENTORY_WINDOW_POSITION_X = 1062f
private const val INVENTORY_WINDOW_POSITION_Y = 50f
private const val EQUIP_WINDOW_POSITION_X = 736f
private const val EQUIP_WINDOW_POSITION_Y = 50f
private const val SKILLS_WINDOW_POSITION_X = 395f
private const val SKILLS_WINDOW_POSITION_Y = 50f
private const val STATS_WINDOW_POSITION_X = 63f
private const val STATS_WINDOW_POSITION_Y = 454f
private const val CALCS_WINDOW_POSITION_X = 63f
private const val CALCS_WINDOW_POSITION_Y = 50f
private const val HEROES_WINDOW_POSITION_X = 63f
private const val HEROES_WINDOW_POSITION_Y = 834f

private const val TITLE_GLOBAL = "   Inventory"
private const val TITLE_PERSONAL = "   Equipment"
private const val TITLE_SPELLS = "   Spells"
private const val TITLE_SKILLS = "   Skills"
private const val TITLE_STATS = "   Stats"
private const val TITLE_CALCS = "   Calculations"
private const val TITLE_HEROES = "   Heroes"

internal class InventoryUI(
    stage: Stage,

    private val itemSlotTooltip: ItemSlotTooltip = ItemSlotTooltip(),
    private val personalityTooltip: PersonalityTooltip = PersonalityTooltip(),

    private val spellsTable: SpellsTable = SpellsTable(personalityTooltip),
    private val spellsWindow: Window = createDefaultWindow(TITLE_SPELLS, spellsTable.container),

    private val inventorySlotsTable: InventorySlotsTable = InventorySlotsTable(itemSlotTooltip),
    private val inventoryWindow: Window = createDefaultWindow(TITLE_GLOBAL, inventorySlotsTable.container),

    private val equipSlotsTables: EquipSlotsTables = EquipSlotsTables(itemSlotTooltip),
    private val equipWindow: Window = createDefaultWindow(TITLE_PERSONAL, equipSlotsTables.getCurrentEquipTable()),

    private val skillsTable: SkillsTable = SkillsTable(personalityTooltip),
    private val skillsWindow: Window = createDefaultWindow(TITLE_SKILLS, skillsTable.container),

    private val statsTable: StatsTable = StatsTable(personalityTooltip),
    private val statsWindow: Window = createDefaultWindow(TITLE_STATS, statsTable.container),

    private val calcsTable: CalcsTable = CalcsTable(personalityTooltip),
    private val calcsWindow: Window = createDefaultWindow(TITLE_CALCS, calcsTable.container),

    heroesTable: HeroesTable = HeroesTable(),
    private val heroesWindow: Window = createDefaultWindow(TITLE_HEROES, heroesTable.heroes),

    tableList: List<WindowSelector> = listOf(calcsTable, statsTable, skillsTable,
                                             equipSlotsTables, inventorySlotsTable, spellsTable),
    selectedTableIndex: Int = 4

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
        skillsTable.update()
        statsTable.update()
        calcsTable.update()
        heroesTable.update()
        spellsWindow.pack()
        skillsWindow.pack()
        statsWindow.pack()
        calcsWindow.pack()
        heroesWindow.pack()
    }

    override fun setWindowPositions() {
        spellsWindow.setPosition(SPELLS_WINDOW_POSITION_X, SPELLS_WINDOW_POSITION_Y)
        inventoryWindow.setPosition(INVENTORY_WINDOW_POSITION_X, INVENTORY_WINDOW_POSITION_Y)
        equipWindow.setPosition(EQUIP_WINDOW_POSITION_X, EQUIP_WINDOW_POSITION_Y)
        skillsWindow.setPosition(SKILLS_WINDOW_POSITION_X, SKILLS_WINDOW_POSITION_Y)
        statsWindow.setPosition(STATS_WINDOW_POSITION_X, STATS_WINDOW_POSITION_Y)
        calcsWindow.setPosition(CALCS_WINDOW_POSITION_X, CALCS_WINDOW_POSITION_Y)
        heroesWindow.setPosition(HEROES_WINDOW_POSITION_X, HEROES_WINDOW_POSITION_Y)
    }

    override fun addToStage() {
        itemSlotTooltip.addToStage(stage)
        personalityTooltip.addToStage(stage)
        stage.addActor(spellsWindow)
        stage.addActor(inventoryWindow)
        stage.addActor(equipWindow)
        stage.addActor(skillsWindow)
        stage.addActor(statsWindow)
        stage.addActor(calcsWindow)
        stage.addActor(heroesWindow)
    }

}
