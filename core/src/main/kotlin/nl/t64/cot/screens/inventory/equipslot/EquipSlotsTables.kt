package nl.t64.cot.screens.inventory.equipslot

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.PartyContainer
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.WindowSelector
import nl.t64.cot.screens.inventory.tooltip.ItemSlotTooltip


class EquipSlotsTables(private val tooltip: ItemSlotTooltip) : WindowSelector {

    private val equipSlots: MutableMap<String, EquipSlotsTable> = HashMap(PartyContainer.MAXIMUM)

    init {
        fillEquipSlotsTables()
        setCurrentByIndex(7)
    }

    override fun setKeyboardFocus(stage: Stage) {
        val container: Table = getCurrentEquipTable()
        stage.keyboardFocus = container
        InventoryUtils.setWindowSelected(container)
    }

    override fun deselectCurrentSlot() {
        getCurrentEquipSlots().deselectCurrentSlot()
        InventoryUtils.setWindowDeselected(getCurrentEquipTable())
    }

    override fun selectCurrentSlot() {
        getCurrentEquipSlots().selectCurrentSlot()
    }

    override fun hideTooltip() {
        tooltip.hide()
    }

    override fun toggleTooltip() {
        getCurrentEquipSlots().toggleTooltip()
    }

    override fun toggleCompare() {
        getCurrentEquipSlots().toggleCompare()
    }

    override fun doAction() {
        getCurrentEquipSlots().dequipItem()
    }

    fun getIndexOfCurrentSlot(): Int =
        getCurrentEquipSlots().getIndexOfCurrentSlot()

    fun setCurrentByIndex(index: Int) {
        getCurrentEquipSlots().setCurrentByIndex(index)
    }

    fun getCurrentEquipTable(): Table {
        return getCurrentEquipSlots().container
    }

    fun getCurrentEquipSlots(): EquipSlotsTable =
        equipSlots[InventoryUtils.getSelectedHero().id]!!

    private fun fillEquipSlotsTables() {
        gameData.party.getAllHeroes().forEach { equipSlots[it.id] = EquipSlotsTable(it, tooltip) }
    }

}
