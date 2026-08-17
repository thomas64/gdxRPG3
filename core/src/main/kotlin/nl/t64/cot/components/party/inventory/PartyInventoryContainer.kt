package nl.t64.cot.components.party.inventory

import nl.t64.cot.Utils.gameData


class PartyInventoryContainer(numberOfSlots: Int = 0) : InventoryContainer(numberOfSlots) {

    fun getTotalOfItemIncludingPartyEquipment(itemId: String): Int {
        return getTotalOfItem(itemId) + gameData.party.getAmountOfItemInEquipment(itemId)
    }

    override fun incrementAmountAt(index: Int, amount: Int) {
        super.incrementAmountAt(index, amount)
        gameData.quests.updateFindItem()
    }

    override fun decrementAmountAt(index: Int, amount: Int) {
        super.decrementAmountAt(index, amount)
        gameData.quests.updateFindItem()
    }

    override fun forceSetItemAt(index: Int, newItem: InventoryItem?) {
        super.forceSetItemAt(index, newItem)
        possibleReplaceItem(newItem)
        gameData.quests.updateFindItem()
    }

    private fun possibleReplaceItem(newItem: InventoryItem?) {
        newItem?.replaces
            ?.filter { contains(it) }
            ?.forEach { autoRemoveItem(it, 1) }
    }

}
