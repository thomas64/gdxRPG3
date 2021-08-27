package nl.t64.cot.screens.shop

import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.itemslot.InventoryImage
import nl.t64.cot.screens.inventory.itemslot.ItemSlot
import nl.t64.cot.screens.inventory.itemslot.ItemSlotSelector
import nl.t64.cot.screens.inventory.itemslot.ItemSlotsExchanger


internal class ShopSlotTaker(
    private val selector: ItemSlotSelector
) {
    private lateinit var choice: Choice
    private lateinit var sourceSlot: ItemSlot
    private lateinit var candidateItem: InventoryImage

    fun buyOne(itemSlot: ItemSlot) {
        choice = Choice.ONE
        tryPutShopSlotToInventorySlot(itemSlot)
    }

    fun buyHalf(itemSlot: ItemSlot) {
        choice = Choice.HALF
        tryPutShopSlotToInventorySlot(itemSlot)
    }

    fun buyFull(itemSlot: ItemSlot) {
        choice = Choice.FULL
        tryPutShopSlotToInventorySlot(itemSlot)
    }

    private fun tryPutShopSlotToInventorySlot(sourceSlot: ItemSlot) {
        this.sourceSlot = sourceSlot
        sourceSlot.getPossibleInventoryImage()?.let { tryPutShopSlotToInventorySlot(it) }
    }

    private fun tryPutShopSlotToInventorySlot(candidateItem: InventoryImage) {
        this.candidateItem = candidateItem
        InventoryUtils.getScreenUI()
            .inventorySlotsTable
            .getPossibleSameStackableItemSlotWith(candidateItem.inventoryItem)?.let {
                exchangeWithInventorySlot(it)
            } ?: exchangeWithPossibleEmptyInventorySlot()
    }

    private fun exchangeWithPossibleEmptyInventorySlot() {
        InventoryUtils.getScreenUI()
            .inventorySlotsTable
            .getPossibleEmptySlot()?.let {
                exchangeWithInventorySlot(it)
            }
    }

    private fun exchangeWithInventorySlot(targetSlot: ItemSlot) {
        sourceSlot.deselect()
        val takeAmount = getAndTakeAmount()
        ItemSlotsExchanger(candidateItem, takeAmount, sourceSlot, targetSlot).exchange()
        selector.setNewSelectedByIndex(sourceSlot.index)
    }

    private fun getAndTakeAmount(): Int {
        val startAmount = sourceSlot.getAmount()
        return when (choice) {
            Choice.ONE -> getAndTakeOne(startAmount)
            Choice.HALF -> getAndTakeHalf(startAmount)
            Choice.FULL -> getAndTakeFull(startAmount)
        }
    }

    private fun getAndTakeOne(startAmount: Int): Int {
        return when (startAmount) {
            1 -> getAndTakeFull(startAmount)
            else -> {
                sourceSlot.decrementAmountBy(1)
                1
            }
        }
    }

    private fun getAndTakeHalf(startAmount: Int): Int {
        return when (startAmount) {
            1 -> getAndTakeFull(startAmount)
            else -> {
                val takeAmount = sourceSlot.getHalfOfAmount()
                sourceSlot.decrementAmountBy(takeAmount)
                takeAmount
            }
        }
    }

    private fun getAndTakeFull(startAmount: Int): Int {
        sourceSlot.clearStack()
        return startAmount
    }

    private enum class Choice {
        ONE, HALF, FULL
    }

}
