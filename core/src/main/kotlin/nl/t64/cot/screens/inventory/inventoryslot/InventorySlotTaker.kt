package nl.t64.cot.screens.inventory.inventoryslot

import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.itemslot.InventoryImage
import nl.t64.cot.screens.inventory.itemslot.ItemSlot
import nl.t64.cot.screens.inventory.itemslot.ItemSlotSelector
import nl.t64.cot.screens.inventory.itemslot.ItemSlotsExchanger


internal class InventorySlotTaker(private val selector: ItemSlotSelector) {

    private lateinit var choice: Choice
    private lateinit var sourceSlot: ItemSlot
    private lateinit var candidateItem: InventoryImage

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun equip(itemSlot: ItemSlot) {
        sourceSlot = itemSlot
        tryPutInventorySlotToEquipSlot()
    }

    private fun tryPutInventorySlotToEquipSlot() {
        sourceSlot.getPossibleInventoryImage()?.let {
            tryPutInventorySlotToEquipSlot(it)
        }
    }

    private fun tryPutInventorySlotToEquipSlot(candidateItem: InventoryImage) {
        this.candidateItem = candidateItem
        InventoryUtils.getScreenUI().getEquipSlotsTables().getCurrentEquipSlots()
            .getPossibleSlotOfGroup(candidateItem.inventoryGroup)?.let {
                exchangeWithEquipSlotOfSameInventoryGroup(it)
            }
    }

    private fun exchangeWithEquipSlotOfSameInventoryGroup(targetSlot: ItemSlot) {
        sourceSlot.deselect()
        sourceSlot.clearStack()
        ItemSlotsExchanger(candidateItem, sourceSlot, targetSlot).exchange()
        selector.setNewSelectedByIndex(sourceSlot.index)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun sellOne(itemSlot: ItemSlot) {
        choice = Choice.ONE
        tryPutInventorySlotToCounterpartSlot(itemSlot)
    }

    fun sellHalf(itemSlot: ItemSlot) {
        choice = Choice.HALF
        tryPutInventorySlotToCounterpartSlot(itemSlot)
    }

    fun sellFull(itemSlot: ItemSlot) {
        choice = Choice.FULL
        tryPutInventorySlotToCounterpartSlot(itemSlot)
    }

    private fun tryPutInventorySlotToCounterpartSlot(sourceSlot: ItemSlot) {
        this.sourceSlot = sourceSlot
        sourceSlot.getPossibleInventoryImage()?.let {
            tryPutInventorySlotToCounterpartSlot(it)
        }
    }

    private fun tryPutInventorySlotToCounterpartSlot(candidateItem: InventoryImage) {
        this.candidateItem = candidateItem
        InventoryUtils.getScreenUI().getCounterpartSlotsTable()
            .getPossibleSameStackableItemSlotWith(candidateItem.inventoryItem)?.let {
                exchangeWithCounterpartSlot(it)
            } ?: exchangeWithPossibleEmptyCounterpartSlot()
    }

    private fun exchangeWithPossibleEmptyCounterpartSlot() {
        InventoryUtils.getScreenUI().getCounterpartSlotsTable().getPossibleEmptySlot()?.let {
            exchangeWithCounterpartSlot(it)
        }
    }

    private fun exchangeWithCounterpartSlot(targetSlot: ItemSlot) {
        sourceSlot.deselect()
        val takeAmount: Int = getAndTakeAmount()
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
