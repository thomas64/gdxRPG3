package nl.t64.cot.components.party.inventory

import nl.t64.cot.Utils.gameData


private const val SORTING_SPLIT = 70000 // atm, item.json starts with this number.

class InventoryContainer(numberOfSlots: Int = 0) {

    private val inventory: MutableList<InventoryItem?> = arrayOfNulls<InventoryItem>(numberOfSlots).toMutableList()

    fun getAllContent(): MutableMap<String, Int> {
        return inventory
            .filterNotNull()
            .associate { it.id to it.amount }   // .map(Pair(it.id, it.amount)).toMap()
            .toMutableMap()
    }

    fun getAllFilledSlots(): List<InventoryItem> {
        return inventory.filterNotNull()
    }

    fun getAmountOfItemAt(index: Int): Int {
        return inventory[index]?.amount ?: 0
    }

    fun incrementAmountAt(index: Int, amount: Int) {
        inventory[index]?.increaseAmountWith(amount)
            ?: throw IllegalArgumentException("There is no item to increment amount.")
        gameData.quests.updateFindItem()
    }

    fun decrementAmountAt(index: Int, amount: Int) {
        inventory[index]?.decreaseAmountWith(amount)
            ?: throw IllegalArgumentException("There is no item to decrement amount.")
        gameData.quests.updateFindItem()
    }

    fun getItemAt(index: Int): InventoryItem? {
        return inventory[index]
    }

    fun autoSetItem(newItem: InventoryItem) {
        if (newItem.isStackable) {
            addResource(newItem)
        } else {
            addItemAtEmptySlot(newItem)
        }
    }

    fun autoRemoveItem(items: Map<String, Int>) {
        items.forEach { autoRemoveItem(it.key, it.value) }
    }

    fun autoRemoveItem(itemId: String, orgAmount: Int) {
        check(hasEnoughOfItem(itemId, orgAmount)) { "Cannot remove this resource from Inventory." }

        var countAmount = orgAmount
        for ((index, foundAmount) in findAllIndexesWithAmountOfItem(itemId)) {
            if (countAmount == 0) {
                break
            } else if (countAmount >= foundAmount) {
                countAmount -= foundAmount
                clearItemAt(index)
            } else {
                decrementAmountAt(index, countAmount)
                countAmount = 0
            }
        }
    }

    fun clearItemAt(index: Int) {
        forceSetItemAt(index, null)
    }

    fun forceSetItemAt(index: Int, newItem: InventoryItem?) {
        inventory[index] = newItem
        gameData.quests.updateFindItem()
    }

    fun getSize(): Int {
        return inventory.size
    }

    fun isEmpty(): Boolean {
        return inventory.all { it == null }
    }

    fun sort() {
        InventoryStacksMerger(this).searchAll()
        val sortedList = inventory.sortedWith(compareBy({ getSort(it) }, { getInventoryGroup(it) }))
        inventory.clear()
        inventory.addAll(sortedList)
    }

    fun contains(items: Map<String, Int>): Boolean {
        return if (items.isEmpty()) {
            false
        } else {
            items.all { hasEnoughOfItem(it.key, it.value) }
        }
    }

    fun hasExactlyAmountOfItem(itemId: String, amount: Int): Boolean {
        return getTotalOfItem(itemId) == amount
    }

    fun hasEnoughOfItem(itemId: String?, amount: Int): Boolean {
        return getTotalOfItem(itemId) >= amount
    }

    fun hasEmptySlot(): Boolean {
        return findFirstEmptySlotIndex() != null
    }

    fun hasRoomForResource(itemId: String): Boolean {
        return (findFirstSlotIndexWithItem(itemId) != null
                || findFirstEmptySlotIndex() != null)
    }

    fun findFirstSlotIndexWithItem(itemId: String): Int? {
        return (0 until getSize()).firstOrNull { containsItemAt(it, itemId) }
    }

    fun findFirstFilledSlotIndex(): Int? {
        return findNextFilledSlotIndexFrom(0)
    }

    fun findNextFilledSlotIndexFrom(index: Int): Int? {
        return (index until getSize()).firstOrNull { isSlotFilled(it) }
    }

    fun findFirstEmptySlotIndex(): Int? {
        return (0 until getSize()).firstOrNull { isSlotEmpty(it) }
    }

    fun getLastIndex(): Int {
        return getSize() - 1
    }

    fun contains(itemId: String): Boolean {
        return inventory.filterNotNull().any { it.hasSameIdAs(itemId) }
    }

    fun getTotalOfItem(itemId: String?): Int {
        return inventory
            .filterNotNull()
            .filter { it.hasSameIdAs(itemId) }
            .sumOf { it.amount }
    }

    fun getNumberOfFilledSlots(): Int {
        return inventory.filterNotNull().count()
    }

    private fun addResource(newItem: InventoryItem) {
        getItem(newItem.id)?.increaseAmountWith(newItem) ?: addItemAtEmptySlot(newItem)
    }

    private fun addItemAtEmptySlot(newItem: InventoryItem) {
        findFirstEmptySlotIndex()?.let { slotIsEmptySoSetItemAt(it, newItem) }
            ?: throw IllegalStateException("Inventory is full.")
    }

    private fun slotIsEmptySoSetItemAt(index: Int, newItem: InventoryItem) {
        forceSetItemAt(index, newItem)
    }

    private fun getItem(itemId: String): InventoryItem? {
        return inventory.filterNotNull().firstOrNull { it.hasSameIdAs(itemId) }
    }

    private fun findAllIndexesWithAmountOfItem(itemId: String): Map<Int, Int> {
        return inventory
            .mapIndexed { index, item -> Triple(index, item?.id, item?.amount) }
            .filter { (_, id, _) -> id == itemId }
            .filter { (_, _, amount) -> amount != null }
            .associate { (index, _, amount) -> Pair(index, amount) } as Map<Int, Int>
    }

    private fun isSlotFilled(index: Int): Boolean {
        return !isSlotEmpty(index)
    }

    private fun isSlotEmpty(index: Int): Boolean {
        return inventory[index] == null
    }

    private fun containsItemAt(index: Int, itemId: String): Boolean {
        return inventory[index]?.hasSameIdAs(itemId) ?: false
    }

    private fun getInventoryGroup(inventoryItem: InventoryItem?): InventoryGroup {
        return inventoryItem?.group ?: InventoryGroup.EMPTY
    }

    private fun getSort(inventoryItem: InventoryItem?): Int {
        return inventoryItem?.sort ?: SORTING_SPLIT
    }

}
