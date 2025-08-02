package nl.t64.cot.screens.inventory.tooltip

import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.screens.inventory.itemslot.InventoryImage
import nl.t64.cot.screens.inventory.itemslot.ItemSlot


class LootSlotTooltip : ItemSlotTooltip() {

    override fun updateDescription(itemSlot: ItemSlot) {
        window.clear()
        if (itemSlot.hasItem()) {
            val hoveredImage = itemSlot.getCertainInventoryImage()
            val inventoryGroup = hoveredImage.inventoryItem.group

            if (inventoryGroup == InventoryGroup.RESOURCE
                || inventoryGroup == InventoryGroup.POTION
                || inventoryGroup == InventoryGroup.ITEM
            ) {
                createResourceTooltip(hoveredImage)
            } else {
                createSingleTooltip(hoveredImage)
            }
        }
        window.pack()
    }

    override fun createSingleTooltip(inventoryImage: InventoryImage) {
        val hoveredTable = Table()
        hoveredTable.defaults().left()

        val descriptionLines = inventoryImage.getComparelessDescription().toMutableList()
        descriptionLines.removeUnnecessaryAttributes()
        descriptionLines.forEach { hoveredTable.addDescriptionLine(it, createSingleLabelStyle(it)) }
        hoveredTable.addPossibleDescription(inventoryImage)
        window.add(hoveredTable)
    }

}
