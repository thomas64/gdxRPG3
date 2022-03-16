package nl.t64.cot.screens.inventory.inventoryslot

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.inventory.InventoryContainer
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.WindowSelector
import nl.t64.cot.screens.inventory.itemslot.InventoryImage
import nl.t64.cot.screens.inventory.itemslot.ItemSlot
import nl.t64.cot.screens.inventory.itemslot.ItemSlotSelector
import nl.t64.cot.screens.inventory.tooltip.ItemSlotTooltip


private const val SLOT_SIZE = 64f
private const val SLOTS_IN_ROW = 6
private const val CONTAINER_HEIGHT = 704f

class InventorySlotsTable(private val tooltip: ItemSlotTooltip) : WindowSelector {

    private val inventory: InventoryContainer = gameData.inventory
    private val inventorySlotTable = Table()
    val container = Table().apply {
        add(ScrollPane(inventorySlotTable)).height(CONTAINER_HEIGHT)
        background = Utils.createTopBorder()
    }
    private val selector = ItemSlotSelector(inventory, inventorySlotTable, SLOTS_IN_ROW)
    private val taker = InventorySlotTaker(selector)

    init {
        fillInventorySlots()
        selector.setNewCurrentByIndex(0)
        inventorySlotTable.addListener(InventorySlotsTableListener({ selector.selectNewSlot(it) }, SLOTS_IN_ROW))
    }

    override fun setKeyboardFocus(stage: Stage) {
        stage.keyboardFocus = inventorySlotTable
        InventoryUtils.setWindowSelected(container)
    }

    override fun getCurrentSlot(): ItemSlot {
        return selector.getCurrentSlot()
    }

    override fun getCurrentTooltip(): ItemSlotTooltip {
        return tooltip
    }

    override fun deselectCurrentSlot() {
        selector.deselectCurrentSlot()
        InventoryUtils.setWindowDeselected(container)
    }

    override fun selectCurrentSlot() {
        selector.selectCurrentSlot()
    }

    override fun hideTooltip() {
        tooltip.hide()
    }

    override fun takeOne() {
        taker.sellOne(selector.getCurrentSlot())
    }

    override fun takeHalf() {
        taker.sellHalf(selector.getCurrentSlot())
    }

    override fun takeFull() {
        taker.sellFull(selector.getCurrentSlot())
    }

    override fun doAction() {
        val currentSlot: ItemSlot = selector.getCurrentSlot()
        if (currentSlot.getPossibleInventoryImage()?.inventoryGroup == InventoryGroup.POTION) {
            // todo, in battle not allowed to drink this way.
            InventorySlotUser.drink(currentSlot)
        } else {
            taker.equip(currentSlot)
        }
    }

    fun addResource(inventoryItem: InventoryItem) {
        inventory.autoSetItem(inventoryItem)
        clearAndFill()
    }

    fun removeResource(itemId: String, amount: Int) {
        inventory.autoRemoveItem(itemId, amount)
        clearAndFill()
    }

    fun clearAndFill() {
        val index = selector.getCurrentSlot().index
        val isSelected = selector.getCurrentSlot().isSelected()
        val listener = inventorySlotTable.listeners[0]
        inventorySlotTable.clear()
        fillInventorySlots()
        inventorySlotTable.addListener(listener)
        inventorySlotTable.stage.draw()
        if (isSelected) {
            selector.setNewSelectedByIndex(index)
        } else {
            selector.setNewCurrentByIndex(index)
        }
    }

    fun getPossibleSameStackableItemSlotWith(candidateItem: InventoryItem): ItemSlot? {
        return if (candidateItem.isStackable) getPossibleSameItemSlotWith(candidateItem) else null
    }

    private fun getPossibleSameItemSlotWith(candidateItem: InventoryItem): ItemSlot? {
        return inventory.findFirstSlotIndexWithItem(candidateItem.id)?.let { index ->
            inventorySlotTable.getChild(index) as ItemSlot
        }
    }

    fun getPossibleEmptySlot(): ItemSlot? {
        return inventory.findFirstEmptySlotIndex()?.let {
            inventorySlotTable.getChild(it) as ItemSlot
        }
    }

    private fun fillInventorySlots() {
        (0 until inventory.getSize()).forEach { createInventorySlot(it) }
    }

    private fun createInventorySlot(index: Int) {
        val inventorySlot = InventorySlot(index, InventoryGroup.EVERYTHING, tooltip, inventory)
        inventory.getItemAt(index)?.let {
            inventorySlot.addToStack(InventoryImage(it))
        }
        inventorySlotTable.add(inventorySlot).size(SLOT_SIZE, SLOT_SIZE)
        if ((index + 1) % SLOTS_IN_ROW == 0) {
            inventorySlotTable.row()
        }
    }

}
