package nl.t64.cot.components.loot

import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.party.inventory.InventoryContainer
import nl.t64.cot.components.party.inventory.InventoryDatabase
import nl.t64.cot.components.party.inventory.InventoryProgress
import nl.t64.cot.resources.ConfigDataLoader


private const val NUMBER_OF_SLOTS = 99

class ShopContainer {

    private val shops: MutableMap<String, InventoryContainer> = fillShopContainer()

    // a shop that was never traded with holds exactly what the config says, so it is not stored at all.
    fun toProgress(): Map<String, InventoryProgress> {
        return shops
            .mapValues { (_, shop) -> shop.toProgress() }
            .filterNot { (shopId, progress) -> progress == createShopInventoryContainer(shopId).toProgress() }
    }

    // a shop that is no longer in the config is not in this container, so the save file cannot revive it.
    fun applyProgress(progress: Map<String, InventoryProgress>) {
        progress
            .filterKeys { it in shops }
            .forEach { (shopId, shopProgress) -> applyProgress(shopId, shopProgress) }
    }

    fun getShop(shopId: String): InventoryContainer {
        return shops[shopId]!!
    }

    private fun applyProgress(shopId: String, shopProgress: InventoryProgress) {
        val shop = InventoryContainer(NUMBER_OF_SLOTS)
        shop.applyProgress(shopProgress)
        shops[shopId] = shop
    }

    private fun fillShopContainer(): MutableMap<String, InventoryContainer> {
        return ConfigDataLoader.getShopIds().associateWith { createShopInventoryContainer(it) }.toMutableMap()
        //                           means: .map { it to createShopInventoryContainer(it) }.toMap()
    }

    private fun createShopInventoryContainer(shopId: String): InventoryContainer {
        return InventoryContainer(NUMBER_OF_SLOTS).apply {
            resourceManager.getShopInventory(shopId)
                .map { InventoryDatabase.createInventoryItemForShop(it) }
                .forEachIndexed { index, item -> forceSetItemAt(index, item) }
        }
    }

}
