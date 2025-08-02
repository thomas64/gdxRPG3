package nl.t64.cot.screens.mechanic

import nl.t64.cot.components.party.abilities.ResourceType
import nl.t64.cot.components.party.inventory.InventoryItem


class MechanicCraftTooltip : MechanicTooltip() {

    override fun InventoryItem.getCosts(): Map<ResourceType, Int> {
        return this.getCraftCosts()
    }

}
