package nl.t64.cot.components.party.abilities

import com.fasterxml.jackson.annotation.JsonCreator


class AbilityContainer() {

    private val abilities: AbilityItemMap<AbilityItemId, AbilityItem> = AbilityItemMap()

    @JsonCreator
    constructor(startingAbilities: List<String>) : this() {
        startingAbilities
            .map { AbilityDatabase.createAbilityItem(it) }
            .forEach { this.abilities[it.id] = it }
    }

    fun getById(abilityItemId: AbilityItemId): AbilityItem? {
        return abilities[abilityItemId]
    }

    fun getAll(): List<AbilityItem> {
        return AbilityItemId.entries.mapNotNull { abilities[it] }
    }

    fun add(abilityItem: AbilityItem) {
        abilities[abilityItem.id] = abilityItem
    }
}

private class AbilityItemMap<K : Enum<K>, V> {
    private val map: MutableMap<String, V> = HashMap()
    operator fun get(key: Enum<K>): V? = map[key.name]
    operator fun set(key: Enum<K>, value: V) {
        map[key.name] = value
    }
}
