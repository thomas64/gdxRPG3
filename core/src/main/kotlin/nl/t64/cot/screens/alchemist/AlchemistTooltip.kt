package nl.t64.cot.screens.alchemist

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.screens.inventory.itemslot.InventoryImage
import nl.t64.cot.screens.inventory.tooltip.*


class AlchemistTooltip : PersonalityTooltip() {

    fun refreshWithInventoryItem(inventoryItem: InventoryItem, getUpdatedPosition: () -> Vector2) {
        hide()
        updateDescription(inventoryItem)
        super.show(getUpdatedPosition)
    }

    private fun updateDescription(inventoryItem: InventoryItem) {
        window.clear()
        val inventoryImage = InventoryImage(inventoryItem)

        addNameSection(inventoryImage)
        addDescriptionSection(inventoryImage)
        addResourcesSection(inventoryImage)

        window.pack()
    }

    private fun addNameSection(inventoryImage: InventoryImage) {
        val hoveredTable = Table()
        hoveredTable.defaults().left()

        val descriptionLines = inventoryImage.getComparelessDescription().toMutableList()
        descriptionLines.removeUnnecessaryAttributes()
        descriptionLines.forEach { hoveredTable.addDescriptionLine(it, createSingleLabelStyle(it)) }

        window.add(hoveredTable).row()
    }

    private fun addDescriptionSection(inventoryImage: InventoryImage) {
        window.add(createEmptyLine()).row()
        val description = inventoryImage.inventoryItem.description.joinToString(System.lineSeparator())
        window.add(createLabel(description, Color.WHITE)).row()
    }

    private fun addResourcesSection(inventoryImage: InventoryImage) {
        window.add(createEmptyLine()).row()

        val resourceTable = Table()
        resourceTable.defaults().left()
        resourceTable.add(createLabel("Required resources:", Color.GOLD)).colspan(2).row()

        inventoryImage.inventoryItem.getBrewCosts()
            .forEach { (resourceType, cost) ->
                resourceTable.add(createLabel("${resourceType.title}:", Color.WHITE))
                resourceTable.add(createLabel("$cost", Color.WHITE)).row()
            }
        window.add(resourceTable)
    }

}
