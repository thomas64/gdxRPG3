package nl.t64.cot.screens.mechanic

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.components.party.abilities.ResourceType
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.screens.inventory.itemslot.InventoryImage
import nl.t64.cot.screens.inventory.tooltip.*


abstract class MechanicTooltip : PersonalityTooltip() {

    fun refreshWithInventoryItem(inventoryItem: InventoryItem, getUpdatedPosition: () -> Vector2) {
        hide()
        updateDescription(inventoryItem)
        super.show(getUpdatedPosition)
    }

    private fun updateDescription(inventoryItem: InventoryItem) {
        window.clear()
        val inventoryImage = InventoryImage(inventoryItem)
        val hoveredTable = createTooltipTable(inventoryImage)
        window.add(hoveredTable)
        window.pack()
    }

    private fun createTooltipTable(inventoryImage: InventoryImage): Table {
        val hoveredTable = Table()
        hoveredTable.defaults().left()

        val descriptionLines = inventoryImage.getComparelessDescription().toMutableList()
        descriptionLines.removeUnnecessaryAttributes()
        descriptionLines.forEach { hoveredTable.addDescriptionLine(it, createSingleLabelStyle(it)) }

        hoveredTable.add(createEmptyLine()).colspan(2).row()
        hoveredTable.add(createLabel("Required resources:", Color.GOLD)).colspan(2).row()

        inventoryImage.inventoryItem.getCosts()
            .forEach { (resourceType, cost) ->
                hoveredTable.add(createLabel("${resourceType.title}:", Color.WHITE)).spaceRight(20f)
                hoveredTable.add(createLabel("$cost", Color.WHITE)).row()
            }
        return hoveredTable
    }

    protected abstract fun InventoryItem.getCosts(): Map<ResourceType, Int>

}
