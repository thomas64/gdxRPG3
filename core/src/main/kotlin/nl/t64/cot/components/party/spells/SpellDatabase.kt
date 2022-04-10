package nl.t64.cot.components.party.spells

import nl.t64.cot.ConfigDataLoader


object SpellDatabase {

    private val spellItems: Map<String, SpellItem> = ConfigDataLoader.createSpells()

    fun createSpellItem(spellId: String, rank: Int): SpellItem {
        val spellItem = spellItems[spellId]!!
        return spellItem.createCopy(rank)
    }

}
