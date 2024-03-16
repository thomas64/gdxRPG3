package nl.t64.cot.components.party.inventory

import nl.t64.cot.Utils.gameData


private const val SORTING_SPLIT = 70000 // atm, item.json starts with this number.

class InventoryContainer(numberOfSlots: Int = 0) {

    private val inventory: MutableList<InventoryItem?> = MutableList(numberOfSlots) { null }

    fun getAllContent(): MutableMap<String, Int> {
        return inventory
            .filterNotNull()
            .associate { it.id to it.amount }   // .map { Pair(it.id, it.amount) }.toMap()
            .toMutableMap()
    }

    fun getAllFilledSlots(): List<InventoryItem> {
        return inventory.filterNotNull()
    }

    fun getAmountOfItemAt(index: Int): Int {
        return inventory[index]?.amount ?: 0
    }

    fun incrementAmountAt(index: Int, amount: Int) {
        inventory[index]
            ?.increaseAmountWith(amount)
            ?: error("There is no item to increment amount.")
        gameData.quests.updateFindItem()
    }

    fun decrementAmountAt(index: Int, amount: Int) {
        inventory[index]
            ?.decreaseAmountWith(amount)
            ?: error("There is no item to decrement amount.")
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

    fun autoRemoveItems(items: Map<String, Int>) {
        items.forEach { (itemId, amount) -> autoRemoveItem(itemId, amount) }
    }

    fun autoRemoveItem(itemId: String, amount: Int) {
        check(hasEnoughOfItem(itemId, amount)) { "Cannot remove this resource from Inventory." }

        var countAmount = amount
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
        val comparator: Comparator<InventoryItem?> = compareBy({ it.getSort() }, { it.getInventoryGroup() })
        inventory.sortWith(comparator)
    }

    fun contains(items: Map<String, Int>): Boolean {
        return items.isEmpty()
            || items.all { (itemId, amount) -> hasEnoughOfItem(itemId, amount) }
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
        return (0 until getSize())
            .firstOrNull { it.contains(itemId) }
    }

    fun findFirstFilledSlotIndex(): Int? {
        return findNextFilledSlotIndexFrom(0)
    }

    fun findNextFilledSlotIndexFrom(index: Int): Int? {
        return (index until getSize())
            .firstOrNull { it.isFilled() }
    }

    fun findFirstEmptySlotIndex(): Int? {
        return (0 until getSize())
            .firstOrNull { it.isEmpty() }
    }

    fun getLastIndex(): Int {
        return getSize() - 1
    }

    fun contains(itemId: String): Boolean {
        return inventory
            .filterNotNull()
            .any { it.hasSameIdAs(itemId) }
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
        getItem(newItem.id)
            ?.increaseAmountWith(newItem)
            ?: addItemAtEmptySlot(newItem)
    }

    private fun addItemAtEmptySlot(newItem: InventoryItem) {
        findFirstEmptySlotIndex()
            ?.let { slotIsEmptySoSetItemAt(it, newItem) }
            ?: error("Inventory is full.")
    }

    private fun slotIsEmptySoSetItemAt(index: Int, newItem: InventoryItem) {
        forceSetItemAt(index, newItem)
    }

    private fun getItem(itemId: String): InventoryItem? {
        return inventory
            .filterNotNull()
            .firstOrNull { it.hasSameIdAs(itemId) }
    }

    private fun findAllIndexesWithAmountOfItem(itemId: String): Map<Int, Int> {
        return inventory
            .mapIndexed { index, item -> Triple(index, item?.id, item?.amount) }
            .filter { (_, id, _) -> id == itemId }
            .filter { (_, _, amount) -> amount != null }
            .associate { (index, _, amount) -> index to amount!! }
    }

    private fun Int.isFilled(): Boolean {
        return inventory[this] != null
    }

    private fun Int.isEmpty(): Boolean {
        return inventory[this] == null
    }

    private fun Int.contains(itemId: String): Boolean {
        return inventory[this]?.hasSameIdAs(itemId) ?: false
    }

    private fun InventoryItem?.getInventoryGroup(): InventoryGroup {
        return this?.group ?: InventoryGroup.EMPTY
    }

    private fun InventoryItem?.getSort(): Int {
        return this?.sort ?: SORTING_SPLIT
    }

}
