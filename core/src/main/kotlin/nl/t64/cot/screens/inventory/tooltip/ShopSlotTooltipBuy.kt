package nl.t64.cot.screens.inventory.tooltip

import nl.t64.cot.components.party.inventory.InventoryDescription


class ShopSlotTooltipBuy : ItemSlotTooltip() {

    override fun removeLeftUnnecessaryAttributes(descriptionList: MutableList<InventoryDescription>) {
        descriptionList.removeSell()
    }

    override fun removeRightUnnecessaryAttributes(descriptionList: MutableList<InventoryDescription>) {
        descriptionList.removeBuy()
    }

}
