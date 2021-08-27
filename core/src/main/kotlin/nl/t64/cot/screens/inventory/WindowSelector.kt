package nl.t64.cot.screens.inventory

import com.badlogic.gdx.scenes.scene2d.Stage
import nl.t64.cot.screens.inventory.itemslot.ItemSlot
import nl.t64.cot.screens.inventory.tooltip.BaseTooltip


interface WindowSelector {
    fun setKeyboardFocus(stage: Stage)
    fun getCurrentSlot(): ItemSlot?
    fun getCurrentTooltip(): BaseTooltip
    fun deselectCurrentSlot()
    fun selectCurrentSlot()
    fun hideTooltip()
    fun takeOne() {}
    fun takeHalf() {}
    fun takeFull() {}
    fun doAction() {}
}
