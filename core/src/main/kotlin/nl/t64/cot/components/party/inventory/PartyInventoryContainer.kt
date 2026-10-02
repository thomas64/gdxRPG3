package nl.t64.cot.components.party.inventory

import nl.t64.cot.Utils.gameData


class PartyInventoryContainer(numberOfSlots: Int = 0) : InventoryContainer(numberOfSlots) {

    private var totalsBeforeExchange: MutableMap<String, Int>? = null

    fun getTotalOfItemIncludingPartyEquipment(itemId: String): Int {
        return getTotalOfItem(itemId) + gameData.party.getAmountOfItemInEquipment(itemId)
    }

    // an exchange can take an item out and put it back when it is refused, like a shop that won't buy it.
    // so during an exchange the quests are not updated per step, but once afterward with only the net changes.
    fun updateQuestsOnceAfter(exchange: () -> Unit) {
        val totalsBefore: MutableMap<String, Int> = mutableMapOf()
        totalsBeforeExchange = totalsBefore
        exchange.invoke()
        totalsBeforeExchange = null
        updateQuestsForChangedTotals(totalsBefore)
    }

    override fun incrementAmountAt(index: Int, amount: Int) {
        val itemId: String = getItemAt(index)!!.id
        updateQuestsOnChange(setOf(itemId)) { super.incrementAmountAt(index, amount) }
    }

    override fun decrementAmountAt(index: Int, amount: Int) {
        val itemId: String = getItemAt(index)!!.id
        updateQuestsOnChange(setOf(itemId)) { super.decrementAmountAt(index, amount) }
    }

    override fun forceSetItemAt(index: Int, newItem: InventoryItem?) {
        val itemIds: Set<String> = setOfNotNull(getItemAt(index)?.id, newItem?.id)
        updateQuestsOnChange(itemIds) {
            super.forceSetItemAt(index, newItem)
            possibleReplaceItem(newItem)
        }
    }

    private fun updateQuestsOnChange(itemIds: Set<String>, change: () -> Unit) {
        val totalsBefore: Map<String, Int> = itemIds.associateWith { getTotalOfItemIncludingPartyEquipment(it) }
        change.invoke()
        val exchangeTotals: MutableMap<String, Int>? = totalsBeforeExchange
        if (exchangeTotals == null) {
            updateQuestsForChangedTotals(totalsBefore)
        } else {
            totalsBefore.forEach { (itemId, total) -> exchangeTotals.putIfAbsent(itemId, total) }
        }
    }

    private fun updateQuestsForChangedTotals(totalsBefore: Map<String, Int>) {
        totalsBefore.filter { (itemId, total) -> getTotalOfItemIncludingPartyEquipment(itemId) != total }
            .keys.forEach { gameData.quests.updateFindItem(it) }
    }

    private fun possibleReplaceItem(newItem: InventoryItem?) {
        newItem?.replaces
            ?.filter { contains(it) }
            ?.forEach { autoRemoveItem(it, 1) }
    }

}
