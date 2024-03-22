package nl.t64.cot.components.party.inventory

import nl.t64.cot.Utils.gameData


class PlayerInventoryContainer(numberOfSlots: Int = 0) : InventoryContainer(numberOfSlots) {

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
        gameData.quests.updateFindItem()
    }

}
