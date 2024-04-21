package nl.t64.cot.components.party.inventory

import nl.t64.cot.resources.ConfigDataLoader


object InventoryDatabase {

    private val inventoryItems: Map<String, InventoryItem> = ConfigDataLoader.createItems()

    fun createInventoryItemForShop(itemId: String): InventoryItem {
        val inventoryItem = inventoryItems[itemId]!!
        val amount = inventoryItem.group.getDefaultShopAmount()
        return inventoryItem.createCopy(amount)
    }

    fun createInventoryItem(itemId: String, amount: Int = 1): InventoryItem {
        val inventoryItem = inventoryItems[itemId]!!
        return inventoryItem.createCopy(amount)
    }

}
