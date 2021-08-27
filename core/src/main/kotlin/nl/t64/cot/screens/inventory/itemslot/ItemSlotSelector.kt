package nl.t64.cot.screens.inventory.itemslot

import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.party.inventory.InventoryContainer


class ItemSlotSelector(
    private val itemContainer: InventoryContainer,
    private val itemSlotsTable: Table,
    private val slotsInRow: Int
) {

    private lateinit var itemSLot: ItemSlot

    fun getCurrentSlot(): ItemSlot =
        itemSLot

    fun deselectCurrentSlot() {
        itemSLot.deselect()
    }

    fun selectCurrentSlot() {
        itemSLot.select()
    }

    fun findNextSlot() {
        val nextIndex: Int =
            itemContainer.findNextFilledSlotFrom(itemSLot.index)
                ?: itemContainer.findFirstFilledSlot()
                ?: itemSLot.index
        itemSLot.deselect()
        setNewSelectedByIndex(nextIndex)
    }

    fun selectNewSlot(deltaIndex: Int) {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_MENU_CURSOR)
        itemSLot.deselect()
        val newSelectedIndex = calculateNewSelectedIndex(deltaIndex)
        setNewSelectedByIndex(newSelectedIndex)
    }

    fun setNewSelectedByIndex(index: Int) {
        setNewCurrentByIndex(index)
        itemSLot.select()
    }

    fun setNewCurrentByIndex(index: Int) {
        itemSLot = itemSlotsTable.getChild(index) as ItemSlot
    }

    private fun calculateNewSelectedIndex(deltaIndex: Int): Int {
        var newIndex = itemSLot.index + deltaIndex
        if (deltaIndex == 1 && newIndex % slotsInRow == 0) {
            newIndex -= slotsInRow
        } else if (deltaIndex == -1 && (newIndex == -1 || newIndex % slotsInRow == slotsInRow - 1)) {
            newIndex += slotsInRow
        }

        if (newIndex < 0) {
            newIndex = itemContainer.getSize() + newIndex
        } else if (newIndex >= itemContainer.getSize()) {
            newIndex = newIndex - itemContainer.getSize()
        }
        return newIndex
    }

}
