package nl.t64.cot.screens.inventory

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.Utils
import nl.t64.cot.components.party.inventory.InventoryContainer
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.screens.inventory.inventoryslot.InventorySlotsTableListener
import nl.t64.cot.screens.inventory.itemslot.ItemSlot
import nl.t64.cot.screens.inventory.itemslot.ItemSlotSelector
import nl.t64.cot.screens.inventory.tooltip.ItemSlotTooltip
import nl.t64.cot.screens.shop.ShopSlotTaker


private const val CONTAINER_HEIGHT = 704f

abstract class CounterpartSlotsTable(
    val inventory: InventoryContainer,
    val tooltip: ItemSlotTooltip,
    private val slotsInRow: Int
) : WindowSelector {

    val counterpartSlotTable = Table()
    val container = Table().apply {
        add(ScrollPane(counterpartSlotTable)).height(CONTAINER_HEIGHT)
        background = Utils.createTopBorder()
    }
    val selector = ItemSlotSelector(inventory, counterpartSlotTable, slotsInRow)
    private val taker = ShopSlotTaker(selector)

    override fun setKeyboardFocus(stage: Stage) {
        stage.keyboardFocus = counterpartSlotTable
        InventoryUtils.setWindowSelected(container)
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

    override fun toggleTooltip() {
        tooltip.toggle(selector.getCurrentSlot())
    }

    override fun toggleCompare() {
        tooltip.toggleCompare(selector.getCurrentSlot())
    }

    override fun takeOne() {
        taker.buyOne(selector.getCurrentSlot())
    }

    override fun takeHalf() {
        taker.buyHalf(selector.getCurrentSlot())
    }

    override fun takeFull() {
        taker.buyFull(selector.getCurrentSlot())
    }

    fun getPossibleSameStackableItemSlotWith(candidateItem: InventoryItem): ItemSlot? {
        return if (candidateItem.isStackable) getPossibleSameItemSlotWith(candidateItem) else null
    }

    private fun getPossibleSameItemSlotWith(candidateItem: InventoryItem): ItemSlot? {
        return inventory.findFirstSlotIndexWithItem(candidateItem.id)?.let { index ->
            counterpartSlotTable.getChild(index) as ItemSlot
        }
    }

    fun getPossibleEmptySlot(): ItemSlot? {
        return inventory.findFirstEmptySlotIndex()?.let {
            counterpartSlotTable.getChild(it) as ItemSlot
        }
    }

    fun fillSlots() {
        (0 until inventory.getSize()).forEach { createSlot(it) }
    }

    abstract fun createSlot(index: Int)

    fun selectorAndListener() {
        selector.setNewCurrentByIndex(0)
        counterpartSlotTable.addListener(InventorySlotsTableListener({ selector.selectNewSlot(it) }, slotsInRow))
    }

}
