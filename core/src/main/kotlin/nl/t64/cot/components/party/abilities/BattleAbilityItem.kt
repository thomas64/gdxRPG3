package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.party.inventory.InventoryItem


class BattleAbilityItem(
    private val abilityItem: AbilityItem,
    val currentWeapon: InventoryItem?
) {
    val id: AbilityItemId = abilityItem.id
    val name: String = abilityItem.name
    val ap: Int = abilityItem.ap

    constructor(name: String) : this(AbilityItem(name = name), null)

    override fun toString(): String {
        return when (name) {
            "Back" -> name
            else -> "$name (${abilityItem.ap} AP)"
        }
    }

}
