package nl.t64.cot.screens.mechanic

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.stopAllSe
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.inventory.InventoryDatabase
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.dialog.MessageDialog
import nl.t64.cot.screens.dialog.QuestionDialog
import nl.t64.cot.screens.inventory.BaseTable
import nl.t64.cot.screens.inventory.ListenerKeyVertical


private const val FIRST_COLUMN_WIDTH = 64f
private const val SECOND_COLUMN_WIDTH = 300f
private const val THIRD_COLUMN_WIDTH = 50f
private const val CONTAINER_HEIGHT = 704f
private const val ROW_HEIGHT = 64f
private const val SECOND_COLUMN_PAD_LEFT = 15f
private const val TABLE_PAD_TOP = 2f
private const val CONTAINER_WIDTH = FIRST_COLUMN_WIDTH + SECOND_COLUMN_WIDTH + SECOND_COLUMN_PAD_LEFT

class CraftTable(
    heroId: String,
    private val tooltip: MechanicCraftTooltip
) : BaseTable(tooltip) {

    private val heroMechanic: HeroItem = gameData.party.getCertainHero(heroId)
    private val mechanicRank: Int = heroMechanic.getCalculatedTotalSkillOf(SkillItemId.MECHANIC)
    private val itemsToCraft: List<InventoryItem> = InventoryDatabase.getItemsToCraftForMechanicRank(mechanicRank)
    private val verticalKeyListener = ListenerKeyVertical { updateIndex(it, itemsToCraft.size) }
    private var deltaIndex = 0

    init {
        table.columnDefaults(0).width(FIRST_COLUMN_WIDTH)
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH)
        table.columnDefaults(2).width(THIRD_COLUMN_WIDTH)
        table.defaults().height(ROW_HEIGHT)
        table.padTop(TABLE_PAD_TOP)

        container.add(scrollPane).height(CONTAINER_HEIGHT)
        container.background = Utils.createTopBorder()
        container.addListener(verticalKeyListener)
    }

    override fun selectAnotherSlotWhenIndexBecameOutOfBounds() {
        // Not necessary for this table, because there is no hero switching possible in MechanicScreen.
    }

    override fun updateIndex(deltaIndex: Int, size: Int) {
        this.deltaIndex = deltaIndex
        super.updateIndex(deltaIndex, size)
    }

    override fun fillRows() {
        if (itemsToCraft.isEmpty()) {
            val label = """
                Nothing to craft at Mechanic rank ${mechanicRank}.


                Improve your Mechanic skill to
                unlock more craftable items.

                The required Mechanic ranks are:
                - 2: Basic equipment
                - 4: Fine equipment
                - 6: Specialist equipment
                - 8: Masterwork equipment
            """.trimIndent()
            table.add(Label(label, LabelStyle(font, Color.BLACK)))
                .width(CONTAINER_WIDTH).padTop(150f)
        } else {
            fillRowsWithContent()
        }
    }

    override fun doAction() {
        if (itemsToCraft.isEmpty()) return

        hideTooltip()
        val itemToCraft: InventoryItem = itemsToCraft[selectedIndex]
        val craftCosts: Map<String, Int> = itemToCraft.getCraftCosts().mapKeys { it.key.name.lowercase() }
        if (gameData.inventory.contains(craftCosts)) {
            itemToCraft.possibleCraftFor(craftCosts)
        } else {
            itemToCraft.doNotCraft()
        }
    }

    override fun toggleTooltip() {
        if (itemsToCraft.isNotEmpty()) {
            super.toggleTooltip()
        }
    }

    fun stopScrolling() {
        verticalKeyListener.cleanup()
    }

    private fun InventoryItem.possibleCraftFor(craftCosts: Map<String, Int>) {
        val readableFormat: String = craftCosts.entries.joinToString(", ") { "${it.value} ${it.key}" }
        val question = """
                    Do you want to craft a ${this.name}
                    for $readableFormat?""".trimIndent()
        QuestionDialog(question) { this.craftFor(craftCosts) }
            .show(table.stage, AudioEvent.SE_CONVERSATION_NEXT, 0, 0.5f)
    }

    private fun InventoryItem.craftFor(craftCosts: Map<String, Int>) {
        stopAllSe()
        if (gameData.inventory.hasEmptySlot()) {
            val newItem: InventoryItem = InventoryDatabase.createInventoryItem(this.id)
            gameData.inventory.autoRemoveItems(craftCosts)
            gameData.inventory.autoSetItem(newItem)
            MessageDialog("You have successfully crafted a ${this.name}.")
                .show(table.stage, AudioEvent.SE_REPAIR)
        } else {
            MessageDialog("Inventory is full.").show(table.stage, AudioEvent.SE_MENU_ERROR)
        }
    }

    private fun InventoryItem.doNotCraft() {
        MessageDialog("You don't have the required resources to craft a ${this.name}.")
            .show(table.stage, AudioEvent.SE_MENU_ERROR)
    }

    private fun fillRowsWithContent() {
        itemsToCraft.forEachIndexed { index, item -> fillRow(item, index) }

        if (deltaIndex != 0) {
            scrollScrollPane()
            deltaIndex = 0
        }
    }

    private fun fillRow(item: InventoryItem, index: Int) {
        table.add(createImageOf(item.id))
        val itemName = Label(item.name, LabelStyle(font, Color.BLACK))
        table.add(itemName).padLeft(SECOND_COLUMN_PAD_LEFT)

        val currentAmount = gameData.inventory.getTotalOfItem(item.id)
        val amountLabel = Label("($currentAmount)", LabelStyle(font, Color.DARK_GRAY))
        table.add(amountLabel).row()

        possibleSetSelectedInventoryItem(index, itemName, item)
    }

    private fun possibleSetSelectedInventoryItem(index: Int, itemLabel: Label, inventoryItem: InventoryItem) {
        if (table.hasKeyboardFocus() && index == selectedIndex) {
            setSelectedInventoryItem(itemLabel, inventoryItem)
        }
    }

    private fun setSelectedInventoryItem(itemLabel: Label, inventoryItem: InventoryItem) {
        itemLabel.style.fontColor = Constant.LIGHT_RED
        refreshTooltipOnlyOnce(inventoryItem)
    }

    private fun refreshTooltipOnlyOnce(inventoryItem: InventoryItem) {
        if (hasJustUpdated) {
            hasJustUpdated = false
            tooltip.refreshWithInventoryItem(inventoryItem) { getTooltipPosition() }
        }
    }

    private fun scrollScrollPane() {
        if (!table.hasKeyboardFocus()) return
        val y = getSelected().y
        val scrollMarginUp = ROW_HEIGHT * 1.5f
        val scrollMarginDown = ROW_HEIGHT
        val visibleStart = scrollPane.scrollY
        val visibleEnd = scrollPane.scrollY + scrollPane.height

        if (y < visibleStart + scrollMarginUp) {
            scrollPane.scrollY = maxOf(y - scrollMarginUp, 0f)
        } else if (y + ROW_HEIGHT > visibleEnd - scrollMarginDown) {
            scrollPane.scrollY = minOf(y + ROW_HEIGHT - scrollPane.height + scrollMarginDown, scrollPane.maxY)
        }
    }

}
