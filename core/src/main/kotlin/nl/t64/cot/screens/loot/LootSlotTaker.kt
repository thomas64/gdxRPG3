package nl.t64.cot.screens.loot

import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.screens.inventory.itemslot.ItemSlot
import nl.t64.cot.screens.inventory.itemslot.ItemSlotSelector
import nl.t64.cot.screens.inventory.messagedialog.MessageDialog


internal class LootSlotTaker(
    private val selector: ItemSlotSelector
) {

    private lateinit var sourceSlot: ItemSlot

    fun take(itemSlot: ItemSlot) {
        sourceSlot = itemSlot
        tryPutLootSlotToInventorySlot()
    }

    private fun tryPutLootSlotToInventorySlot() {
        sourceSlot.getPossibleInventoryImage()?.let {
            tryPutLootSlotToInventorySlot(it.inventoryItem)
        }
    }

    private fun tryPutLootSlotToInventorySlot(candidateItem: InventoryItem) {
        if (itemIsResource(candidateItem) || gameData.inventory.hasEmptySlot()) {
            putLootSlotToInventorySlot(candidateItem)
        } else {
            MessageDialog("Inventory is full.").show(sourceSlot.stage, AudioEvent.SE_MENU_ERROR)
        }
    }

    private fun itemIsResource(candidateItem: InventoryItem): Boolean {
        return (candidateItem.isStackable
                && gameData.inventory.hasRoomForResource(candidateItem.id))
    }

    private fun putLootSlotToInventorySlot(candidateItem: InventoryItem) {
        playSe(AudioEvent.SE_TAKE)
        gameData.inventory.autoSetItem(candidateItem)
        sourceSlot.clearStack()
        selector.findNextSlot()
    }

}
