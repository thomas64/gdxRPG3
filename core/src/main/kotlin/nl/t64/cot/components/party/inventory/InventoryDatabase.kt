package nl.t64.cot.components.party.inventory

import nl.t64.cot.constants.Constant
import nl.t64.cot.resources.ConfigDataLoader


object InventoryDatabase {

    private val inventoryItems: Map<String, InventoryItem> = ConfigDataLoader.createItems()

    fun createInventoryItemForShop(itemId: String): InventoryItem? {
        val inventoryItem = inventoryItems[itemId] ?: return null
        val amount = inventoryItem.group.getDefaultShopAmount()
        return inventoryItem.createCopy(amount)
    }

    fun createInventoryItem(itemId: String, amount: Int = 1): InventoryItem {
        val inventoryItem = inventoryItems[itemId]!!
        return inventoryItem.createCopy(amount)
    }

    fun getItemsToCraftForMechanicRank(rank: Int): List<InventoryItem> {
        return inventoryItems.values
            .filter { it.isCraftableForMechanicRank(rank) }
            .sortedWith(
                compareBy<InventoryItem> {
                    when {
                        it.id.contains(Constant.BASIC) -> 0
                        it.id.contains(Constant.FINE) -> 1
                        it.id.contains(Constant.SPECIALIST) -> 2
                        it.id.contains(Constant.MASTERWORK) -> 3
                        else -> 4
                    }
                }.thenBy { it.sort }
            )
    }

}
