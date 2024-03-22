package nl.t64.cot.screens.shop

import nl.t64.cot.Utils.gameData
import nl.t64.cot.screens.inventory.CounterpartSlotsTable
import nl.t64.cot.screens.inventory.itemslot.InventoryImage
import nl.t64.cot.screens.inventory.tooltip.ItemSlotTooltip


private const val SLOTS_IN_ROW = 9
private const val SLOT_SIZE = 64f

class ShopSlotsTable(
    shopId: String,
    tooltip: ItemSlotTooltip
) : CounterpartSlotsTable(
    gameData.shops.getShop(shopId),
    tooltip,
    SLOTS_IN_ROW
) {

    init {
        fillSlots()
        refreshPurchaseColor()
        selectorAndListener()
    }

    fun refreshPurchaseColor() {
        counterpartSlotTable.children.forEach { (it as ShopSlot).refreshPurchaseColor() }
    }

    override fun createSlot(index: Int) {
        val shopSlot = ShopSlot(index, tooltip, inventory)
        inventory.getItemAt(index)?.let {
            shopSlot.addToStack(InventoryImage(it))
        }
        counterpartSlotTable.add(shopSlot).size(SLOT_SIZE, SLOT_SIZE)
        if ((index + 1) % SLOTS_IN_ROW == 0) {
            counterpartSlotTable.row()
        }
    }

}
