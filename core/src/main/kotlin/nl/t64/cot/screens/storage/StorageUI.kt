package nl.t64.cot.screens.storage

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Window
import nl.t64.cot.Utils
import nl.t64.cot.screens.ScreenUI
import nl.t64.cot.screens.inventory.CounterpartSlotsTable
import nl.t64.cot.screens.inventory.HeroesTable
import nl.t64.cot.screens.inventory.WindowSelector
import nl.t64.cot.screens.inventory.equipslot.EquipSlotsTables
import nl.t64.cot.screens.inventory.inventoryslot.InventorySlotsTable
import nl.t64.cot.screens.inventory.tooltip.ItemSlotTooltip


private const val EQUIP_WINDOW_POSITION_X = 1566f
private const val EQUIP_WINDOW_POSITION_Y = 50f
private const val INVENTORY_WINDOW_POSITION_X = 1145f
private const val INVENTORY_WINDOW_POSITION_Y = 50f
private const val STORAGE_WINDOW_POSITION_X = 63f
private const val STORAGE_WINDOW_POSITION_Y = 50f
private const val HEROES_WINDOW_POSITION_X = 63f
private const val HEROES_WINDOW_POSITION_Y = 834f

private const val TITLE_PERSONAL = "   Equipment"
private const val TITLE_GLOBAL = "   Inventory"
private const val TITLE_STORAGE = "   Storage"
private const val TITLE_HEROES = "   Heroes"

internal class StorageUI(
    stage: Stage,

    private val itemSlotTooltip: ItemSlotTooltip = ItemSlotTooltip(),

    private val equipSlotsTables: EquipSlotsTables = EquipSlotsTables(itemSlotTooltip),
    private val equipWindow: Window = Utils.createDefaultWindow(TITLE_PERSONAL, equipSlotsTables.getCurrentEquipTable()),

    private val inventorySlotsTable: InventorySlotsTable = InventorySlotsTable(itemSlotTooltip),
    private val inventoryWindow: Window = Utils.createDefaultWindow(TITLE_GLOBAL, inventorySlotsTable.container),

    private val storageSlotsTable: StorageSlotsTable = StorageSlotsTable(itemSlotTooltip),
    private val storageWindow: Window = Utils.createDefaultWindow(TITLE_STORAGE, storageSlotsTable.container),

    heroesTable: HeroesTable = HeroesTable(),
    private val heroesWindow: Window = Utils.createDefaultWindow(TITLE_HEROES, heroesTable.heroes),

    tableList: List<WindowSelector> = listOf(storageSlotsTable, inventorySlotsTable, equipSlotsTables),
    selectedTableIndex: Int = 0

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

    override fun getStorageSlotsTable(): StorageSlotsTable {
        return storageSlotsTable
    }

    override fun getCounterpartSlotsTable(): CounterpartSlotsTable {
        return storageSlotsTable;
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

    fun takeOne() {
        getSelectedTable().takeOne()
    }

    fun takeHalf() {
        getSelectedTable().takeHalf()
    }

    fun takeFull() {
        getSelectedTable().takeFull()
    }

    fun equip() {
        getSelectedTable().doAction()
    }

    fun reloadInventory() {
        storageSlotsTable.clearAndFill()
    }

    fun update() {
        heroesTable.update()
        heroesWindow.pack()
    }

    override fun setWindowPositions() {
        equipWindow.setPosition(EQUIP_WINDOW_POSITION_X, EQUIP_WINDOW_POSITION_Y)
        inventoryWindow.setPosition(INVENTORY_WINDOW_POSITION_X, INVENTORY_WINDOW_POSITION_Y)
        storageWindow.setPosition(STORAGE_WINDOW_POSITION_X, STORAGE_WINDOW_POSITION_Y)
        heroesWindow.setPosition(HEROES_WINDOW_POSITION_X, HEROES_WINDOW_POSITION_Y)
    }

    override fun addToStage() {
        itemSlotTooltip.addToStage(stage)
        stage.addActor(equipWindow)
        stage.addActor(inventoryWindow)
        stage.addActor(storageWindow)
        stage.addActor(heroesWindow)
    }

}
