package nl.t64.cot.screens.shop

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.Utils
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.party.inventory.InventoryContainer
import nl.t64.cot.components.party.inventory.InventoryDatabase
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.WindowSelector
import nl.t64.cot.screens.inventory.inventoryslot.InventorySlotsTableListener
import nl.t64.cot.screens.inventory.itemslot.InventoryImage
import nl.t64.cot.screens.inventory.itemslot.ItemSlot
import nl.t64.cot.screens.inventory.itemslot.ItemSlotSelector
import nl.t64.cot.screens.inventory.tooltip.ItemSlotTooltip


private const val SLOT_SIZE = 64f
private const val NUMBER_OF_SLOTS = 99
private const val SLOTS_IN_ROW = 9
private const val CONTAINER_HEIGHT = 704f

class ShopSlotsTable(
    shopId: String,
    private val tooltip: ItemSlotTooltip
) : WindowSelector {

    private val inventory = InventoryContainer(NUMBER_OF_SLOTS)
    private val shopSlotTable = Table()
    val container = Table().apply {
        add(ScrollPane(shopSlotTable)).height(CONTAINER_HEIGHT)
        background = Utils.createTopBorder()
    }
    private val selector = ItemSlotSelector(inventory, shopSlotTable, SLOTS_IN_ROW)
    private val taker = ShopSlotTaker(selector)

    init {
        fillShopContainer(shopId)
        fillShopSlots()
        refreshPurchaseColor()
        selector.setNewCurrentByIndex(0)
        shopSlotTable.addListener(InventorySlotsTableListener({ selector.selectNewSlot(it) }, SLOTS_IN_ROW))
    }

    override fun setKeyboardFocus(stage: Stage) {
        stage.keyboardFocus = shopSlotTable
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
        taker.buyOne(selector.getCurrentSlot())
    }

    override fun takeHalf() {
        taker.buyHalf(selector.getCurrentSlot())
    }

    override fun takeFull() {
        taker.buyFull(selector.getCurrentSlot())
    }

    fun refreshPurchaseColor() {
        shopSlotTable.children.forEach { (it as ShopSlot).refreshPurchaseColor() }
    }

    fun getPossibleSameStackableItemSlotWith(candidateItem: InventoryItem): ItemSlot? {
        return if (candidateItem.isStackable) getPossibleSameItemSlotWith(candidateItem) else null
    }

    private fun getPossibleSameItemSlotWith(candidateItem: InventoryItem): ItemSlot? {
        return inventory.findFirstSlotIndexWithItem(candidateItem.id)?.let { index ->
            shopSlotTable.getChild(index) as ItemSlot
        }
    }

    fun getPossibleEmptySlot(): ItemSlot? {
        return inventory.findFirstEmptySlotIndex()?.let {
            shopSlotTable.getChild(it) as ItemSlot
        }
    }

    private fun fillShopContainer(shopId: String) {
        resourceManager.getShopInventory(shopId)
            .map { InventoryDatabase.createInventoryItemForShop(it) }
            .forEach { inventory.autoSetItem(it) }
    }

    private fun fillShopSlots() {
        (0 until inventory.getSize()).forEach { createShopSlot(it) }
    }

    private fun createShopSlot(index: Int) {
        val shopSlot = ShopSlot(index, tooltip, inventory)
        inventory.getItemAt(index)?.let {
            shopSlot.addToStack(InventoryImage(it))
        }
        shopSlotTable.add(shopSlot).size(SLOT_SIZE, SLOT_SIZE)
        if ((index + 1) % SLOTS_IN_ROW == 0) {
            shopSlotTable.row()
        }
    }

}
