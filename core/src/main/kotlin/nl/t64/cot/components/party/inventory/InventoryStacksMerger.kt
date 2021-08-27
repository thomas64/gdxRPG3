package nl.t64.cot.components.party.inventory


internal class InventoryStacksMerger(private val container: InventoryContainer) {

    private lateinit var inventoryItem1: InventoryItem
    private lateinit var inventoryItem2: InventoryItem

    fun searchAll() {
        (0 until container.getSize()).forEach { setItem1IfPresent(it) }
    }

    private fun setItem1IfPresent(index1: Int) {
        container.getItemAt(index1)?.let {
            inventoryItem1 = it
            searchIfStackable(index1)
        }
    }

    private fun searchIfStackable(index1: Int) {
        if (inventoryItem1.isStackable) {
            searchLeftoverForSameItem(index1 + 1)
        }
    }

    private fun searchLeftoverForSameItem(index2: Int) {
        (index2 until container.getSize()).forEach { setItem2IfPresent(it) }
    }

    private fun setItem2IfPresent(index2: Int) {
        container.getItemAt(index2)?.let {
            inventoryItem2 = it
            stackIfItemsAreTheSame(index2)
        }
    }

    private fun stackIfItemsAreTheSame(index2: Int) {
        if (inventoryItem1.hasSameIdAs(inventoryItem2)) {
            stackTwoItems(index2)
        }
    }

    private fun stackTwoItems(index2: Int) {
        inventoryItem1.increaseAmountWith(inventoryItem2)
        container.clearItemAt(index2)
    }

}
