package nl.t64.cot.components.party.abilities

import nl.t64.cot.resources.ConfigDataLoader


object AbilityDatabase {

    private val abilityItems: Map<String, AbilityItem> = ConfigDataLoader.createAbilities()

    fun createAbilityItem(abilityId: String): AbilityItem {
        val abilityItem = abilityItems[abilityId.lowercase()]!!
        return abilityItem.createCopy()
    }

}
