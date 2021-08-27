package nl.t64.cot.screens

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Window
import nl.t64.cot.screens.inventory.HeroesTable
import nl.t64.cot.screens.inventory.WindowSelector
import nl.t64.cot.screens.inventory.equipslot.EquipSlotsTables
import nl.t64.cot.screens.inventory.inventoryslot.InventorySlotsTable
import nl.t64.cot.screens.shop.ShopSlotsTable


abstract class ScreenUI(
    protected val equipWindow: Window,
    val equipSlotsTables: EquipSlotsTables,
    val inventorySlotsTable: InventorySlotsTable,
    protected val heroesTable: HeroesTable,
    private val tableList: List<WindowSelector>,
    private var selectedTableIndex: Int
) {
    private val stage: Stage get() = equipWindow.stage

    open fun getShopSlotsTable(): ShopSlotsTable {
        throw IllegalStateException("ShopSlotsTable not implemented here.")
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
        val currentSlot = getSelectedTable().getCurrentSlot()
        val currentTooltip = getSelectedTable().getCurrentTooltip()
        currentTooltip.toggle(currentSlot)
    }

    fun toggleCompare() {
        val currentSlot = getSelectedTable().getCurrentSlot()
        val currentTooltip = getSelectedTable().getCurrentTooltip()
        currentTooltip.toggleCompare(currentSlot)
    }

    fun unloadAssets() {
        heroesTable.disposePixmapTextures()
    }

    protected fun setFocusOnSelectedTable() {
        getSelectedTable().setKeyboardFocus(stage)
        stage.draw()
    }

    protected fun getSelectedTable(): WindowSelector {
        return tableList[selectedTableIndex]
    }

}
