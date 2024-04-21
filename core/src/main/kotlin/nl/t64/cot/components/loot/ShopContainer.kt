package nl.t64.cot.components.loot

import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.party.inventory.InventoryContainer
import nl.t64.cot.components.party.inventory.InventoryDatabase
import nl.t64.cot.resources.ConfigDataLoader


private const val NUMBER_OF_SLOTS = 99

class ShopContainer {

    private val shops: Map<String, InventoryContainer> = fillShopContainer()

    fun getShop(shopId: String): InventoryContainer {
        return shops[shopId]!!
    }

    private fun fillShopContainer(): Map<String, InventoryContainer> {
        return ConfigDataLoader.getShopIds().associateWith { createShopInventoryContainer(it) }
        //                           means: .map { it to createShopInventoryContainer(it) }.toMap()
    }

    private fun createShopInventoryContainer(shopId: String): InventoryContainer {
        return InventoryContainer(NUMBER_OF_SLOTS).apply {
            resourceManager.getShopInventory(shopId)
                .map { InventoryDatabase.createInventoryItemForShop(it) }
                .forEach { autoSetItem(it) }
        }
    }

}
