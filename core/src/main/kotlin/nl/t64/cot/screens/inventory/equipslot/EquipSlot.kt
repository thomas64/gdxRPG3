package nl.t64.cot.screens.inventory.equipslot

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.screens.dialog.MessageDialog
import nl.t64.cot.screens.inventory.itemslot.InventoryImage
import nl.t64.cot.screens.inventory.itemslot.ItemSlot
import nl.t64.cot.screens.inventory.tooltip.ItemSlotTooltip


private const val INVENTORY_GROUP_SHADOW_NAME = "shadow"
private val TRANSPARENT = Color(1f, 1f, 1f, 0.3f)

private fun createShadowImageOf(inventoryGroup: InventoryGroup): Image {
    val groupId = inventoryGroup.name.lowercase()
    val textureRegion = resourceManager.getAtlasTexture(groupId)
    return Image(textureRegion).apply {
        color = TRANSPARENT
        name = INVENTORY_GROUP_SHADOW_NAME
    }
}

internal class EquipSlot(
    index: Int,
    filterGroup: InventoryGroup,
    tooltip: ItemSlotTooltip,
    private val heroItem: HeroItem
) : ItemSlot(index, filterGroup, tooltip) {

    init {
        imagesBackground.addActorBefore(imagesBackground.children.peek(), createShadowImageOf(filterGroup))
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
        if (filterGroup != draggedItem.inventoryGroup) return false
        if (isPouchEquipped_ShowWarning_Else()) return false

        return doesHeroAcceptItem(draggedItem)
    }

    private fun isPouchEquipped_ShowWarning_Else(): Boolean {
        val pouchItem = getPossibleInventoryImage()?.inventoryItem ?: return false
        if (pouchItem.goldPouch <= 0) return false

        showDialogHeroDoesNotAccept(pouchItem.createMessageFailToReplace())
        return true
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
        val message: String? = heroItem.createMessageIfNotAbleToEquip(draggedItem.inventoryItem)
        return message?.let { showDialogHeroDoesNotAccept(it) } ?: true
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
        imagesBackground.children
            .firstOrNull { it.name == INVENTORY_GROUP_SHADOW_NAME }
            ?.isVisible = visible
    }

}
