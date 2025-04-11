package nl.t64.cot.components.party.abilities

import com.fasterxml.jackson.annotation.JsonProperty
import nl.t64.cot.components.party.PersonalityItem
import nl.t64.cot.components.party.inventory.InventoryItem


data class AbilityItem(
    val id: AbilityItemId = AbilityItemId.STAGGER,  // Value will be replaced when constructed.
    val name: String = "",
    val ap: Int = 0,
    val sp: Int = 0,
    @JsonProperty("is_hand_to_hand_only")
    private val isHandToHandOnly: Boolean = false,
    private val description: List<String> = emptyList()
) : PersonalityItem {

    fun createCopy(): AbilityItem {
        return copy()
    }

    fun isWeaponAllowed(inventoryItem: InventoryItem?): Boolean {
        if (inventoryItem == null) return false
        if (!isHandToHandOnly) return true

        return inventoryItem.skill?.isHandToHandWeaponSkill() == true
    }

    override fun getTotalDescription(): String {
        TODO("Not yet implemented")
    }

}
