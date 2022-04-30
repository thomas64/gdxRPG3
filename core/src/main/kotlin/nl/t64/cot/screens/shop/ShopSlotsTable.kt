package nl.t64.cot.screens.shop

import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.party.inventory.InventoryContainer
import nl.t64.cot.components.party.inventory.InventoryDatabase
import nl.t64.cot.screens.inventory.CounterpartSlotsTable
import nl.t64.cot.screens.inventory.WindowSelector
import nl.t64.cot.screens.inventory.itemslot.InventoryImage
import nl.t64.cot.screens.inventory.tooltip.ItemSlotTooltip


private const val NUMBER_OF_SLOTS = 99
private const val SLOTS_IN_ROW = 9
private const val SLOT_SIZE = 64f

class ShopSlotsTable(
    shopId: String,
    tooltip: ItemSlotTooltip
) : WindowSelector, CounterpartSlotsTable(
    InventoryContainer(NUMBER_OF_SLOTS),
    tooltip,
    SLOTS_IN_ROW
) {

    init {
        fillShopContainer(shopId)
        fillSlots()
        refreshPurchaseColor()
        selectorAndListener()
    }

    fun refreshPurchaseColor() {
        counterpartSlotTable.children.forEach { (it as ShopSlot).refreshPurchaseColor() }
    }

    private fun fillShopContainer(shopId: String) {
        resourceManager.getShopInventory(shopId)
            .map { InventoryDatabase.createInventoryItemForShop(it) }
            .forEach { inventory.autoSetItem(it) }
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
