package nl.t64.cot.screens.inventory.equipslot

import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.party.inventory.InventoryDatabase
import nl.t64.cot.screens.dialog.MessageDialog
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.itemslot.InventoryImage
import nl.t64.cot.screens.inventory.itemslot.ItemSlot
import nl.t64.cot.screens.inventory.itemslot.ItemSlotsExchanger


internal class EquipSlotTaker(private val selector: EquipSlotSelector) {

    private lateinit var sourceSlot: ItemSlot
    private lateinit var candidateItem: InventoryImage
    private lateinit var targetSlot: ItemSlot

    fun dequip(equipSlot: ItemSlot) {
        sourceSlot = equipSlot
        tryPutEquipSlotToInventorySlot()
    }

    private fun tryPutEquipSlotToInventorySlot() {
        sourceSlot.getPossibleInventoryImage()
            ?.let { tryPutEquipSlotToInventorySlot(it) }
    }

    private fun tryPutEquipSlotToInventorySlot(candidateItem: InventoryImage) {
        this.candidateItem = candidateItem
        if (candidateItem.inventoryItem.goldPouch > 0) {
            tryConvertPouchToGold()
        } else {
            InventoryUtils.getScreenUI().getInventorySlotsTable().getPossibleEmptySlot()
                ?.let { exchangeWithEmptyInventorySlot(it) }
        }
    }

    private fun tryConvertPouchToGold() {
        if (gameData.inventory.hasRoomForResource("gold")) {
            dequipSourceSlot {
                val gold = InventoryDatabase.createInventoryItem("gold", candidateItem.inventoryItem.goldPouch)
                InventoryUtils.getScreenUI().getInventorySlotsTable().addResource(gold)
                playSe(AudioEvent.SE_COINS_SELL)
            }
        } else {
            showErrorMessage("Inventory is full.")
        }
    }

    private fun exchangeWithEmptyInventorySlot(targetSlot: ItemSlot) {
        this.targetSlot = targetSlot
        InventoryUtils.getSelectedHero()
            .createMessageIfNotAbleToDequip(candidateItem.inventoryItem)
            ?.let { showErrorMessage(it) }
            ?: dequipSourceSlot { ItemSlotsExchanger(candidateItem, sourceSlot, targetSlot).exchange() }
    }

    private fun showErrorMessage(message: String) {
        MessageDialog(message).show(sourceSlot.stage, AudioEvent.SE_MENU_ERROR)
    }

    private fun dequipSourceSlot(addToTarget: () -> Unit) {
        sourceSlot.deselect()
        sourceSlot.clearStack()
        addToTarget.invoke()
        selector.setNewSelected(sourceSlot)
    }

}
