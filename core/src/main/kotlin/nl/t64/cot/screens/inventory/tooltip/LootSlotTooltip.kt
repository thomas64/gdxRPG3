package nl.t64.cot.screens.inventory.tooltip

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.skills.SkillItemId
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
        hoveredTable.defaults().align(Align.left)

        val totalMerchant = gameData.party.getSumOfSkill(SkillItemId.MERCHANT)
        val descriptionLines = inventoryImage.getComparelessDescription(totalMerchant).toMutableList()
        removeLeftUnnecessaryAttributes(descriptionLines)
        descriptionLines.forEach { hoveredTable.addDescriptionLine(it, createSingleLabelStyle(it)) }
        addPossibleDescription(inventoryImage, hoveredTable)
        window.add(hoveredTable)
    }

}
