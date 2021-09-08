package nl.t64.cot.screens.inventory.inventoryslot

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import nl.t64.cot.components.party.inventory.InventoryContainer
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.screens.inventory.itemslot.InventoryImage
import nl.t64.cot.screens.inventory.itemslot.ItemSlot
import nl.t64.cot.screens.inventory.tooltip.ItemSlotTooltip


open class InventorySlot(
    index: Int,
    filterGroup: InventoryGroup,
    tooltip: ItemSlotTooltip,
    private val inventory: InventoryContainer
) : ItemSlot(index, filterGroup, tooltip) {

    private val amountLabel: Label = createAmountLabel().also {
        addToStack(it)
    }

    private fun createAmountLabel(): Label {
        val labelStyle = LabelStyle(BitmapFont(), Color.WHITE)
        return Label(getAmount().toString(), labelStyle).apply {
            setAlignment(Align.bottomRight)
            isVisible = false
        }
    }

    override fun addToStack(actor: Actor) {
        if (actor is InventoryImage) {
            super.addActorBefore(super.getChildren().peek(), actor)
            inventory.forceSetItemAt(index, actor.inventoryItem)
            refreshSlot()
        } else {
            super.add(actor)
        }
    }

    override fun hasItem(): Boolean {
        return inventory.getItemAt(index) != null
    }

    override fun isOnHero(): Boolean {
        return false
    }

    override fun doesAcceptItem(draggedItem: InventoryImage): Boolean {
        return filterGroup != InventoryGroup.LOOT_ITEM
    }

    override fun clearStack() {
        if (hasItem()) {
            super.getChildren().removeIndex(1)
            inventory.clearItemAt(index)
            refreshSlot()
        }
    }

    override fun putInSlot(draggedItem: InventoryImage) {
        if (hasItem()) {
            incrementAmountBy(draggedItem.getAmount())
        } else {
            addToStack(draggedItem)
        }
    }

    override fun getAmount(): Int {
        return inventory.getAmountOfItemAt(index)
    }

    override fun incrementAmountBy(amount: Int) {
        inventory.incrementAmountAt(index, amount)
        refreshSlot()
    }

    override fun decrementAmountBy(amount: Int) {
        if (amount == getAmount()) {
            clearStack()
        } else {
            inventory.decrementAmountAt(index, amount)
            refreshSlot()
        }
    }

    private fun refreshSlot() {
        val amount = getAmount()
        amountLabel.setText(amount.toString())
        setVisibilityOfAmountLabel(amount)
        setItemColor()
    }

    private fun setVisibilityOfAmountLabel(amount: Int) {
        val shouldAmountLabelBeVisible = amount >= 2
        amountLabel.isVisible = shouldAmountLabelBeVisible
    }

}
