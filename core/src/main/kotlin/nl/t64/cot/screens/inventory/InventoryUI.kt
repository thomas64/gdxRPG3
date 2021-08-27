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
private const val STATS_WINDOW_POSITION_Y = 429f
private const val CALCS_WINDOW_POSITION_X = 63f
private const val CALCS_WINDOW_POSITION_Y = 50f
private const val HEROES_WINDOW_POSITION_X = 63f
private const val HEROES_WINDOW_POSITION_Y = 834f

private const val TITLE_GLOBAL = "   Inventory"
private const val TITLE_PERSONAL = "   Equipment"
private const val TITLE_SPELLS = "   Spells"
private const val TITLE_SKILLS = "   Skills"
private const val TITLE_STATS = "   Stats"
private const val TITLE_CALCS = "   Calcs"
private const val TITLE_HEROES = "   Heroes"

internal class InventoryUI private constructor(
    stage: Stage,
    private val itemSlotTooltip: ItemSlotTooltip,
    private val personalityTooltip: PersonalityTooltip,
    private val spellsWindow: Window,
    private val inventoryWindow: Window,
    equipWindow: Window,
    private val skillsWindow: Window,
    private val statsWindow: Window,
    private val calcsWindow: Window,
    private val heroesWindow: Window,
    private val spellsTable: SpellsTable,
    inventorySlotsTable: InventorySlotsTable,
    equipSlotsTables: EquipSlotsTables,
    private val skillsTable: SkillsTable,
    private val statsTable: StatsTable,
    private val calcsTable: CalcsTable,
    heroesTable: HeroesTable,
    tableList: List<WindowSelector>,
    selectedTableIndex: Int
) : ScreenUI(equipWindow, equipSlotsTables, inventorySlotsTable, heroesTable, tableList, selectedTableIndex) {

    companion object {
        fun create(stage: Stage): InventoryUI {

            val itemSlotTooltip = ItemSlotTooltip()
            val personalityTooltip = PersonalityTooltip()

            val spellsTable = SpellsTable(personalityTooltip)
            val spellsWindow = createDefaultWindow(TITLE_SPELLS, spellsTable.container)

            val inventorySlotsTable = InventorySlotsTable(itemSlotTooltip)
            val inventoryWindow = createDefaultWindow(TITLE_GLOBAL, inventorySlotsTable.container)

            val equipSlotsTables = EquipSlotsTables(itemSlotTooltip)
            val equipWindow = createDefaultWindow(TITLE_PERSONAL, equipSlotsTables.getCurrentEquipTable())

            val skillsTable = SkillsTable(personalityTooltip)
            val skillsWindow = createDefaultWindow(TITLE_SKILLS, skillsTable.container)

            val statsTable = StatsTable(personalityTooltip)
            val statsWindow = createDefaultWindow(TITLE_STATS, statsTable.container)

            val calcsTable = CalcsTable(personalityTooltip)
            val calcsWindow = createDefaultWindow(TITLE_CALCS, calcsTable.container)

            val heroesTable = HeroesTable()
            val heroesWindow = createDefaultWindow(TITLE_HEROES, heroesTable.heroes)

            val tableList = listOf(calcsTable, statsTable, skillsTable,
                                   equipSlotsTables, inventorySlotsTable, spellsTable)
            val selectedTableIndex = 4

            return InventoryUI(stage, itemSlotTooltip, personalityTooltip,
                               spellsWindow, inventoryWindow, equipWindow, skillsWindow,
                               statsWindow, calcsWindow, heroesWindow,
                               spellsTable, inventorySlotsTable, equipSlotsTables, skillsTable,
                               statsTable, calcsTable, heroesTable,
                               tableList, selectedTableIndex)
        }
    }

    init {
        setWindowPositions()
        addToStage(stage)
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

    private fun setWindowPositions() {
        spellsWindow.setPosition(SPELLS_WINDOW_POSITION_X, SPELLS_WINDOW_POSITION_Y)
        inventoryWindow.setPosition(INVENTORY_WINDOW_POSITION_X, INVENTORY_WINDOW_POSITION_Y)
        equipWindow.setPosition(EQUIP_WINDOW_POSITION_X, EQUIP_WINDOW_POSITION_Y)
        skillsWindow.setPosition(SKILLS_WINDOW_POSITION_X, SKILLS_WINDOW_POSITION_Y)
        statsWindow.setPosition(STATS_WINDOW_POSITION_X, STATS_WINDOW_POSITION_Y)
        calcsWindow.setPosition(CALCS_WINDOW_POSITION_X, CALCS_WINDOW_POSITION_Y)
        heroesWindow.setPosition(HEROES_WINDOW_POSITION_X, HEROES_WINDOW_POSITION_Y)
    }

    private fun addToStage(stage: Stage) {
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
