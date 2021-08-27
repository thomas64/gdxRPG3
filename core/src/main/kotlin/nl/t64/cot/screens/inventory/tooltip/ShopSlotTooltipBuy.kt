package nl.t64.cot.screens.inventory.tooltip

import nl.t64.cot.components.party.inventory.InventoryDescription


class ShopSlotTooltipBuy : ItemSlotTooltip() {

    override fun removeLeftUnnecessaryAttributes(descriptionList: MutableList<InventoryDescription>) {
        removeSell(descriptionList)
    }

    override fun removeRightUnnecessaryAttributes(descriptionList: MutableList<InventoryDescription>) {
        removeBuy(descriptionList)
    }

}
