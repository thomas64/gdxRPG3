package nl.t64.cot.screens.inventory

import com.badlogic.gdx.scenes.scene2d.Stage
import nl.t64.cot.components.loot.Loot


interface WindowSelector {
    fun setKeyboardFocus(stage: Stage)
    fun deselectCurrentSlot()
    fun selectCurrentSlot()
    fun hideTooltip()
    fun toggleTooltip()
    fun toggleCompare()
    fun takeOne() {}
    fun takeHalf() {}
    fun takeFull() {}
    fun doAction() {}
    fun getItemsToDrop(): Loot? { return null }
}
