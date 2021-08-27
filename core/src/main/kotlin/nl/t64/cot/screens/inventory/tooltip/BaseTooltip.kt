package nl.t64.cot.screens.inventory.tooltip

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.utils.Align
import nl.t64.cot.Utils
import nl.t64.cot.screens.inventory.itemslot.ItemSlot


private const val PADDING = 10f

abstract class BaseTooltip {

    val window: Window = Window("", Utils.createTooltipWindowStyle()).apply {
        defaults().align(Align.left)
        pad(PADDING)
        pack()
        isVisible = false
    }

    open fun toggle(itemSlot: ItemSlot?) {
        throw IllegalStateException("Implement this method in child.")
    }

    open fun toggleCompare(itemSlot: ItemSlot?) {
        // do nothing. some tooltips don't need compare.
    }

    fun addToStage(stage: Stage) {
        stage.addActor(window)
    }

    fun hide() {
        window.clearActions()
        window.isVisible = false
    }

}
