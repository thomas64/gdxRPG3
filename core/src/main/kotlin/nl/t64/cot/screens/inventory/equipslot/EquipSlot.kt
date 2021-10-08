package nl.t64.cot.screens.inventory.equipslot

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.screens.inventory.itemslot.InventoryImage
import nl.t64.cot.screens.inventory.itemslot.ItemSlot
import nl.t64.cot.screens.inventory.messagedialog.MessageDialog
import nl.t64.cot.screens.inventory.tooltip.ItemSlotTooltip


private val TRANSPARENT = Color(1f, 1f, 1f, 0.3f)

private fun createShadowImage(inventoryGroup: InventoryGroup): Image {
    val groupId = inventoryGroup.name.lowercase()
    val textureRegion = resourceManager.getAtlasTexture(groupId)
    return Image(textureRegion).apply {
        color = TRANSPARENT
        name = "shadow"
    }
}

internal class EquipSlot(
    index: Int,
    filterGroup: InventoryGroup,
    tooltip: ItemSlotTooltip,
    private val heroItem: HeroItem
) : ItemSlot(index, filterGroup, tooltip) {

    init {
        imagesBackground.addActorBefore(imagesBackground.children.peek(), createShadowImage(filterGroup))
    }

    override fun addToStack(actor: Actor) {
        super.add(actor)
        if (actor is InventoryImage) {
            heroItem.forceSetInventoryItemFor(filterGroup, actor.inventoryItem)
            refreshSlot()
        }
    }

    override fun hasItem(): Boolean {
        return heroItem.getInventoryItem(filterGroup) != null
    }

    override fun isOnHero(): Boolean {
        return true
    }

    override fun doesAcceptItem(draggedItem: InventoryImage): Boolean {
        return filterGroup == draggedItem.inventoryGroup && doesHeroAcceptItem(draggedItem)
    }

    override fun clearStack() {
        if (hasItem()) {
            super.getChildren().pop()
            heroItem.clearInventoryItemFor(filterGroup)
            refreshSlot()
        }
    }

    override fun putInSlot(draggedItem: InventoryImage) {
        addToStack(draggedItem)
    }

    override fun getAmount(): Int {
        return if (hasItem()) 1 else 0
    }

    override fun incrementAmountBy(amount: Int) {
        throw IllegalStateException("EquipSlot amount cannot be incremented.")
    }

    override fun decrementAmountBy(amount: Int) {
        throw IllegalStateException("EquipSlot amount cannot be decremented.")
    }

    private fun doesHeroAcceptItem(draggedItem: InventoryImage): Boolean {
        val message = heroItem.createMessageIfNotAbleToEquip(draggedItem.inventoryItem)
        return message == null || showDialogHeroDoesNotAccept(message)
    }

    private fun showDialogHeroDoesNotAccept(message: String): Boolean {
        MessageDialog(message).show(stage, AudioEvent.SE_MENU_ERROR)
        return false
    }

    private fun refreshSlot() {
        setVisibilityOfShadow()
        setItemColor()
    }

    private fun setVisibilityOfShadow() {
        val shouldShadowBeVisible = !hasItem()
        setShadowVisible(shouldShadowBeVisible)
    }

    private fun setShadowVisible(visible: Boolean) {
        imagesBackground.getChild(1).isVisible = visible // 1 is index of actor named shadow
    }

}
