package nl.t64.cot.screens.inventory.equipslot

import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.itemslot.InventoryImage
import nl.t64.cot.screens.inventory.itemslot.ItemSlot
import nl.t64.cot.screens.inventory.itemslot.ItemSlotsExchanger
import nl.t64.cot.screens.inventory.messagedialog.MessageDialog


internal class EquipSlotTaker(private val selector: EquipSlotSelector) {

    private lateinit var sourceSlot: ItemSlot
    private lateinit var candidateItem: InventoryImage
    private lateinit var targetSlot: ItemSlot

    fun dequip(equipSlot: ItemSlot) {
        sourceSlot = equipSlot
        tryPutEquipSlotToInventorySlot()
    }

    private fun tryPutEquipSlotToInventorySlot() {
        sourceSlot.getPossibleInventoryImage()?.let {
            tryPutEquipSlotToInventorySlot(it)
        }
    }

    private fun tryPutEquipSlotToInventorySlot(candidateItem: InventoryImage) {
        this.candidateItem = candidateItem
        InventoryUtils.getScreenUI().getInventorySlotsTable().getPossibleEmptySlot()?.let {
            exchangeWithEmptyInventorySlot(it)
        }
    }

    private fun exchangeWithEmptyInventorySlot(targetSlot: ItemSlot) {
        this.targetSlot = targetSlot
        InventoryUtils.getSelectedHero()
            .createMessageIfNotAbleToDequip(candidateItem.inventoryItem)?.let {
                showErrorMessage(it)
            } ?: exchange()
    }

    private fun showErrorMessage(message: String) {
        MessageDialog(message).show(sourceSlot.stage, AudioEvent.SE_MENU_ERROR)
    }

    private fun exchange() {
        sourceSlot.deselect()
        sourceSlot.clearStack()
        ItemSlotsExchanger(candidateItem, sourceSlot, targetSlot).exchange()
        selector.setNewSelected(sourceSlot)
    }

}
