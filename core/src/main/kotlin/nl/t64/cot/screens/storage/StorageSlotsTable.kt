package nl.t64.cot.screens.storage

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.screens.inventory.CounterpartSlotsTable
import nl.t64.cot.screens.inventory.inventoryslot.InventorySlot
import nl.t64.cot.screens.inventory.itemslot.InventoryImage
import nl.t64.cot.screens.inventory.tooltip.ItemSlotTooltip


private const val SLOTS_IN_ROW = 16
private const val SLOT_SIZE = 64f

class StorageSlotsTable(
    tooltip: ItemSlotTooltip
) : CounterpartSlotsTable(
    gameData.storage,
    tooltip,
    SLOTS_IN_ROW
) {

    init {
        fillSlots()
        selectorAndListener()
    }

    fun clearAndFill() {
        val index = selector.getCurrentSlot().index
        val isSelected = selector.getCurrentSlot().isSelected()
        val listener = counterpartSlotTable.listeners[0]
        counterpartSlotTable.clear()
        fillSlots()
        counterpartSlotTable.addListener(listener)
        counterpartSlotTable.stage.draw()
        if (isSelected) {
            selector.setNewSelectedByIndex(index)
        } else {
            selector.setNewCurrentByIndex(index)
        }
    }

    override fun createSlot(index: Int) {
        val storageSlot = InventorySlot(index, InventoryGroup.EVERYTHING, tooltip, inventory)
        inventory.getItemAt(index)?.let {
            storageSlot.addToStack(InventoryImage(it))
        }
        counterpartSlotTable.add(storageSlot).size(SLOT_SIZE, SLOT_SIZE)
        if ((index + 1) % SLOTS_IN_ROW == 0) {
            counterpartSlotTable.row()
        }
    }

}
