package nl.t64.cot.components.party.spells

import com.fasterxml.jackson.annotation.JsonCreator


class SpellContainer() {

    private val spells: SpellItemMap<SpellItemId, SpellItem> = SpellItemMap()

    @JsonCreator
    constructor(startingSpells: Map<String, Int>) : this() {
        startingSpells
            .map { SpellDatabase.createSpellItem(it.key, it.value) }
            .forEach { this.spells[it.id] = it }
    }

    fun getById(spellId: SpellItemId): SpellItem {
        return spells[spellId] ?: SpellDatabase.createSpellItem(spellId.name, 0)
    }

    fun getAll(): List<SpellItem> {
        return SpellItemId.entries.mapNotNull { spells[it] }.sortedBy { it.sort }
    }

    fun add(spellItem: SpellItem) {
        spells[spellItem.id] = spellItem
    }

}

private class SpellItemMap<K : Enum<K>, V> {
    private val map: MutableMap<String, V> = HashMap()
    operator fun get(key: Enum<K>): V? = map[key.name]
    operator fun set(key: Enum<K>, value: V) {
        map[key.name] = value
    }
}
