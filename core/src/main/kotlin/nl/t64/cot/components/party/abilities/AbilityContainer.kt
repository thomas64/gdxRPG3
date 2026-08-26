package nl.t64.cot.components.party.abilities

import com.fasterxml.jackson.annotation.JsonCreator


class AbilityContainer() {

    private val abilities: MutableList<AbilityItemId> = mutableListOf()

    @JsonCreator
    constructor(startingAbilities: List<String>) : this() {
        startingAbilities
            .map { AbilityItemId.valueOf(it.uppercase()) }
            .let { abilities.addAll(it) }
    }

    // toMutableList and not toList, because toList gives back an EmptyList or a SingletonList
    // for the smallest sizes, and libGDX cannot create those again while reading.
    fun toProgress(): List<AbilityItemId> {
        return abilities.toMutableList()
    }

    fun applyProgress(abilityIds: List<AbilityItemId>) {
        abilities.clear()
        abilities.addAll(abilityIds)
    }

    fun getById(abilityItemId: AbilityItemId): AbilityItem? {
        return abilityItemId
            .takeIf { it in abilities }
            ?.let { AbilityDatabase.createAbilityItem(it) }
    }

    fun getAll(): List<AbilityItem> {
        return abilities.map { AbilityDatabase.createAbilityItem(it) }.sortedBy { it.id }
    }

    fun add(abilityItemId: AbilityItemId) {
        abilities.add(abilityItemId)
    }

}
