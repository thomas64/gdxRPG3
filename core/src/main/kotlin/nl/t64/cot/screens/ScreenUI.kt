package nl.t64.cot.screens

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.screens.inventory.CounterpartSlotsTable
import nl.t64.cot.screens.inventory.HeroesTable
import nl.t64.cot.screens.inventory.WindowSelector
import nl.t64.cot.screens.inventory.equipslot.EquipSlotsTables
import nl.t64.cot.screens.inventory.inventoryslot.InventorySlotsTable
import nl.t64.cot.screens.shop.ShopSlotsTable
import nl.t64.cot.screens.storage.StorageSlotsTable


abstract class ScreenUI(
    val stage: Stage,
    val heroesTable: HeroesTable,
    private val tableList: List<WindowSelector>,
    private var selectedTableIndex: Int
) {

    fun init() {
        setWindowPositions()
        addToStage()
        setFocusOnSelectedTable()
        stage.addAction(Actions.sequence(
            Actions.delay(0.1f),
            Actions.run { getSelectedTable().selectCurrentSlot() }
        ))
    }

    abstract fun setWindowPositions()

    abstract fun addToStage()

    open fun getEquipSlotsTables(): EquipSlotsTables {
        throw IllegalStateException("EquipSlotsTables not implemented here.")
    }

    open fun getInventorySlotsTable(): InventorySlotsTable {
        throw IllegalStateException("InventorySlotsTable not implemented here.")
    }

    open fun getShopSlotsTable(): ShopSlotsTable {
        throw IllegalStateException("ShopSlotsTable not implemented here.")
    }

    open fun getStorageSlotsTable(): StorageSlotsTable {
        throw IllegalStateException("StorageSlotsTable not implemented here.")
    }

    open fun getCounterpartSlotsTable(): CounterpartSlotsTable {
        throw IllegalStateException("CounterpartSlotsTable not implemented here.")
    }

    fun selectPreviousTable() {
        getSelectedTable().hideTooltip()
        getSelectedTable().deselectCurrentSlot()
        selectedTableIndex--
        if (selectedTableIndex < 0) {
            selectedTableIndex = tableList.size - 1
        }
        setFocusOnSelectedTable()
        getSelectedTable().selectCurrentSlot()
    }

    fun selectNextTable() {
        getSelectedTable().hideTooltip()
        getSelectedTable().deselectCurrentSlot()
        selectedTableIndex++
        if (selectedTableIndex >= tableList.size) {
            selectedTableIndex = 0
        }
        setFocusOnSelectedTable()
        getSelectedTable().selectCurrentSlot()
    }

    fun toggleTooltip() {
        getSelectedTable().toggleTooltip()
    }

    fun toggleCompare() {
        getSelectedTable().toggleCompare()
    }

    fun unloadAssets() {
        heroesTable.disposePixmapTextures()
    }

    fun setFocusOnSelectedTable() {
        getSelectedTable().setKeyboardFocus(stage)
        stage.draw()
    }

    fun getSelectedTable(): WindowSelector {
        return tableList[selectedTableIndex]
    }

}
