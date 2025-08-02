package nl.t64.cot.screens.inventory.tooltip

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.party.inventory.InventoryDescription
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.itemslot.InventoryImage
import nl.t64.cot.screens.inventory.itemslot.ItemSlot


private const val SLOT_SIZE = 64f
private const val THREE_QUARTERS = SLOT_SIZE * 0.75f
private const val HALF_SPACING = 10f
private const val RIGHT_HEADER = "Currently Equipped"
private const val DELAY = 0.5f

open class ItemSlotTooltip : BaseTooltip() {

    private val totalMerchant: Int get() = gameData.party.getSumOfSkill(SkillItemId.MERCHANT)

    override fun toggle(itemSlot: ItemSlot?) {
        if (itemSlot?.hasItem() == true) {
            val isEnabled = gameData.isTooltipEnabled
            gameData.isTooltipEnabled = !isEnabled
            setupTooltip(itemSlot)
            window.isVisible = !isEnabled
            playSe(AudioEvent.SE_MENU_CONFIRM)
        } else {
            playSe(AudioEvent.SE_MENU_ERROR)
        }
    }

    override fun toggleCompare(itemSlot: ItemSlot?) {
        if (itemSlot?.hasItem() == true && !window.hasActions()) {
            val hoveredItem = itemSlot.getCertainInventoryImage().inventoryItem
            val equippedItem = InventoryUtils.getSelectedHero().getInventoryItem(hoveredItem.group)
            if (equippedItem != null) {
                val isEnabled = gameData.isComparingEnabled
                gameData.isComparingEnabled = !isEnabled
                updateDescription(itemSlot)
                playSe(AudioEvent.SE_MENU_CONFIRM)
                return
            }
        }
        if (!window.hasActions()) {
            playSe(AudioEvent.SE_MENU_ERROR)
        }
    }

    fun refresh(itemSlot: ItemSlot) {
        hide()
        if (gameData.isTooltipEnabled && itemSlot.hasItem()) {
            setupTooltip(itemSlot)
            window.addAction(Actions.sequence(Actions.delay(DELAY),
                                              Actions.show()))
        }
    }

    private fun setupTooltip(itemSlot: ItemSlot) {
        val localCoords = Vector2(itemSlot.originX, itemSlot.originY)
        itemSlot.localToStageCoordinates(localCoords)
        updateDescription(itemSlot)
        window.setPosition(localCoords.x + THREE_QUARTERS, localCoords.y + THREE_QUARTERS)
        window.toFront()
    }

    open fun updateDescription(itemSlot: ItemSlot) {
        window.clear()
        if (itemSlot.hasItem()) {
            val hoveredImage = itemSlot.getCertainInventoryImage()
            val hoveredItem = hoveredImage.inventoryItem
            val inventoryGroup = hoveredItem.group
            val selectedHero = InventoryUtils.getSelectedHero()
            val equippedItem = selectedHero.getInventoryItem(inventoryGroup)

            if (itemSlot.isOnHero()) {
                createSingleTooltip(hoveredImage)
            } else if (inventoryGroup == InventoryGroup.RESOURCE
                || inventoryGroup == InventoryGroup.POTION
                || inventoryGroup == InventoryGroup.ITEM
            ) {
                createResourceTooltip(hoveredImage)
            } else if (equippedItem != null
                && gameData.isComparingEnabled
            ) {
                createDualTooltip(hoveredImage, InventoryImage(equippedItem))
            } else {
                createSingleTooltip(hoveredImage)
            }
        }
        window.pack()
    }

    fun createResourceTooltip(inventoryImage: InventoryImage) {
        val hoveredTable = createDefaultTooltipTable(inventoryImage)
        window.add(hoveredTable)

        window.add().row()
        window.add(createEmptyLine()).row()
        val description = inventoryImage.inventoryItem.description.joinToString(System.lineSeparator())
        window.add(createLabel(description, Color.WHITE))
    }

    open fun createSingleTooltip(inventoryImage: InventoryImage) {
        val hoveredTable = createDefaultTooltipTable(inventoryImage)
        hoveredTable.addPossibleDescription(inventoryImage)
        window.add(hoveredTable)
    }

    private fun createDefaultTooltipTable(inventoryImage: InventoryImage): Table {
        val hoveredTable = Table()
        hoveredTable.defaults().left()

        val descriptionList = inventoryImage.getSingleDescription(totalMerchant).toMutableList()
        removeLeftUnnecessaryAttributes(descriptionList)
        descriptionList.forEach { hoveredTable.addDescriptionLine(it, createSingleLabelStyle(it)) }
        return hoveredTable
    }

    private fun createDualTooltip(hoveredImage: InventoryImage, equippedImage: InventoryImage) {
        val hoveredTable = createLeftTooltip(hoveredImage, equippedImage)
        val equippedTable = createRightTooltip(equippedImage, hoveredImage)
        window.add(hoveredTable)
        window.add().spaceRight(HALF_SPACING)
        window.add(equippedTable)
    }

    private fun createLeftTooltip(hoveredImage: InventoryImage, equippedImage: InventoryImage): Table {
        val hoveredTable = Table(window.skin).apply {
            background = Utils.createTooltipRightBorder()
            padRight(HALF_SPACING)
            defaults().left()
            add(createEmptyLine()).row()
        }
        val descriptionList = hoveredImage.getDualDescription(equippedImage, totalMerchant).toMutableList()
        removeLeftUnnecessaryAttributes(descriptionList)
        descriptionList.forEach { hoveredTable.addDescriptionLine(it, createLeftLabelStyle(it)) }
        if (equippedImage.inventoryItem.description.isNotEmpty() && hoveredImage.inventoryItem.description.isEmpty()) {
            hoveredTable.add(createEmptyLine()).row()
        }
        hoveredTable.addPossibleDescription(hoveredImage)
        return hoveredTable
    }

    private fun createRightTooltip(equippedImage: InventoryImage, hoveredImage: InventoryImage): Table {
        val equippedTable = Table().apply {
            defaults().left()
            add(createLabel(RIGHT_HEADER, Color.LIGHT_GRAY)).row()
        }

        val descriptionLines = equippedImage.getDualDescription(hoveredImage, totalMerchant).toMutableList()
        removeRightUnnecessaryAttributes(descriptionLines)
        descriptionLines.forEach { equippedTable.addDescriptionLine(it, createRightLabelStyle(it)) }
        if (equippedImage.inventoryItem.description.isEmpty() && hoveredImage.inventoryItem.description.isNotEmpty()) {
            equippedTable.add(createEmptyLine()).row()
        }
        equippedTable.addPossibleDescription(equippedImage)
        return equippedTable
    }

    open fun removeLeftUnnecessaryAttributes(descriptionList: MutableList<InventoryDescription>) {
        descriptionList.removeUnnecessaryAttributes()
    }

    open fun removeRightUnnecessaryAttributes(descriptionList: MutableList<InventoryDescription>) {
        descriptionList.removeUnnecessaryAttributes()
    }

}
