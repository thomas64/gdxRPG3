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

    fun getById(abilityItemId: AbilityItemId): AbilityItem? {
        return abilityItemId
            .takeIf { it in abilities }
            ?.let { AbilityDatabase.createAbilityItem(it) }
    }

    fun getAll(): List<AbilityItem> {
        return abilities.map { AbilityDatabase.createAbilityItem(it) }
    }

    fun add(abilityItemId: AbilityItemId) {
        abilities.add(abilityItemId)
    }

}
