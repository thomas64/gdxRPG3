package nl.t64.cot.components.party.spells

import com.fasterxml.jackson.annotation.JsonCreator


class SpellContainer() {

    private val spells: MutableMap<String, SpellItem> = HashMap()

    @JsonCreator
    constructor(startingSpells: Map<String, Int>) : this() {
        startingSpells
            .map { SpellDatabase.createSpellItem(it.key, it.value) }
            .forEach { this.spells[it.id] = it }
    }

    fun getById(spellId: String): SpellItem {
        return spells[spellId] ?: SpellDatabase.createSpellItem(spellId, 0)
    }

    fun getAll(): List<SpellItem> {
        return spells.values.sortedBy { it.sort }
    }

    fun add(spellItem: SpellItem) {
        spells[spellItem.id] = spellItem
    }

}
