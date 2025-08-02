package nl.t64.cot.screens.mechanic

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.stopAllSe
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.dialog.MessageDialog
import nl.t64.cot.screens.dialog.QuestionDialog
import nl.t64.cot.screens.inventory.BaseTable
import nl.t64.cot.screens.inventory.ListenerKeyVertical


private const val FIRST_COLUMN_WIDTH = 64f
private const val SECOND_COLUMN_WIDTH = 300f
private const val CONTAINER_HEIGHT = 704f
private const val ROW_HEIGHT = 64f
private const val SECOND_COLUMN_PAD_LEFT = 15f
private const val TABLE_PAD_TOP = 2f
private const val CONTAINER_WIDTH = FIRST_COLUMN_WIDTH + SECOND_COLUMN_WIDTH + SECOND_COLUMN_PAD_LEFT

class RepairTable(
    heroId: String,
    private val tooltip: MechanicRepairTooltip
) : BaseTable(tooltip) {

    private val heroMechanic: HeroItem = gameData.party.getCertainHero(heroId)
    private val mechanicRank: Int = heroMechanic.getCalculatedTotalSkillOf(SkillItemId.MECHANIC)
    private val verticalKeyListener = ListenerKeyVertical { updateIndex(it, itemsToRepair.size) }
    private var deltaIndex = 0

    private val itemsToRepair: List<InventoryItem>
        get() = (gameData.inventory.getAllRepairableInventoryItems(mechanicRank) +
            gameData.party.getAllHeroes().flatMap { it.getAllRepairableInventoryItems(mechanicRank) }
            ).sortedBy { it.sort }

    init {
        table.columnDefaults(0).width(FIRST_COLUMN_WIDTH)
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH)
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
        if (itemsToRepair.isEmpty()) {
            table.add(Label("Nothing to repair at Mechanic rank $mechanicRank.", LabelStyle(font, Color.BLACK)))
                .width(CONTAINER_WIDTH)
        } else {
            fillRowsWithContent()
        }
    }

    override fun doAction() {
        if (itemsToRepair.isEmpty()) return

        hideTooltip()
        val itemToRepair: InventoryItem = itemsToRepair[selectedIndex]
        val repairCosts: Map<String, Int> = itemToRepair.getRepairCosts().mapKeys { it.key.name.lowercase() }
        if (gameData.inventory.contains(repairCosts)) {
            itemToRepair.possibleRepairFor(repairCosts)
        } else {
            itemToRepair.doNotRepair()
        }
    }

    override fun toggleTooltip() {
        if (itemsToRepair.isNotEmpty()) {
            super.toggleTooltip()
        }
    }

    fun stopScrolling() {
        verticalKeyListener.cleanup()
    }

    private fun InventoryItem.possibleRepairFor(repairCosts: Map<String, Int>) {
        val readableFormat: String = repairCosts.entries.joinToString(", ") { "${it.value} ${it.key}" }
        val question = """
                    Do you want to repair this ${this.name}
                    for $readableFormat?""".trimIndent()
        QuestionDialog(question) { this.repairFor(repairCosts) }
            .show(table.stage, AudioEvent.SE_CONVERSATION_NEXT, 0, 0.5f)
    }

    private fun InventoryItem.repairFor(repairCosts: Map<String, Int>) {
        stopAllSe()
        gameData.inventory.autoRemoveItems(repairCosts)
        this.durability = this.maxDurability
        MessageDialog("${this.name} has successfully been repaired.")
            .show(table.stage, AudioEvent.SE_REPAIR)
    }

    private fun InventoryItem.doNotRepair() {
        MessageDialog("You don't have the required resources to repair this ${this.name}.")
            .show(table.stage, AudioEvent.SE_MENU_ERROR)
    }

    private fun fillRowsWithContent() {
        itemsToRepair.forEachIndexed { index, item -> fillRow(item, index) }

        if (deltaIndex != 0) {
            scrollScrollPane()
            deltaIndex = 0
        }
    }

    private fun fillRow(item: InventoryItem, index: Int) {
        table.add(createImageOf(item.id))
        val itemName = Label("${item.name} (${item.durability} / ${item.maxDurability})", LabelStyle(font, Color.BLACK))
        table.add(itemName).padLeft(SECOND_COLUMN_PAD_LEFT).row()
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
            scrollPane.scrollY = minOf(y + ROW_HEIGHT - scrollPane.height + scrollMarginDown, table.height - scrollPane.height)
        }
    }

}
