package nl.t64.cot.screens.inventory.tooltip

import nl.t64.cot.components.party.inventory.InventoryDescription


class ShopSlotTooltipSell : ItemSlotTooltip() {

    override fun removeLeftUnnecessaryAttributes(descriptionList: MutableList<InventoryDescription>) {
        removeBuy(descriptionList)
    }

}
