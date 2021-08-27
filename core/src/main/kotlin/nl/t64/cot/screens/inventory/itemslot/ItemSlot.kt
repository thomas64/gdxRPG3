package nl.t64.cot.screens.inventory.itemslot

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.utils.Scaling
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.screens.inventory.tooltip.ItemSlotTooltip
import kotlin.math.floor


private const val SPRITE_BACKGROUND = "sprites/inventoryslot.png"
private const val SPRITE_SELECTED = "sprites/selected.png"
private const val NOTHING = "nothing"
private const val BASIC = "basic"
private const val FINE = "fine"
private const val SPECIALIST = "specialist"
private const val MASTERWORK = "masterwork"
private const val EPIC = "epic"
private const val LEGENDARY = "legendary"

abstract class ItemSlot(
    val index: Int,
    val filterGroup: InventoryGroup,
    val tooltip: ItemSlotTooltip
) : Stack() {

    val imagesBackground: Stack = createImageBackground().also {
        addToStack(it)
    }

    private fun createImageBackground(): Stack {
        val stack = Stack()
        val texture = resourceManager.getTextureAsset(SPRITE_BACKGROUND)
        val background = Image(texture)
        background.name = "background"
        stack.add(background)
        stack.add(createTierColor(NOTHING))
        return stack
    }

    fun select() {
        val texture = resourceManager.getTextureAsset(SPRITE_SELECTED)
        val selector = Image(texture)
        selector.name = "selector"
        super.add(selector)
        tooltip.refresh(this)
    }

    fun deselect() {
        if (isSelected()) {
            super.getChildren().pop()
        } else {
            throw IllegalStateException("Tried to deselect an unselected ItemSlot.")
        }
    }

    fun isSelected(): Boolean {
        val name: String? = super.getChildren().peek().name
        return name != null && name == "selector"
    }

    fun getPossibleInventoryImage(): InventoryImage? =
        if (hasItem()) getCertainInventoryImage() else null

    fun getCertainInventoryImage(): InventoryImage =
        super.getChild(1) as InventoryImage

    abstract fun addToStack(actor: Actor)
    abstract fun hasItem(): Boolean
    abstract fun isOnHero(): Boolean
    abstract fun doesAcceptItem(draggedItem: InventoryImage): Boolean
    abstract fun clearStack()
    abstract fun putInSlot(draggedItem: InventoryImage)
    abstract fun getAmount(): Int
    abstract fun incrementAmountBy(amount: Int)
    abstract fun decrementAmountBy(amount: Int)

    fun putItemBack(draggedItem: InventoryImage) {
        putInSlot(draggedItem)
    }

    fun getHalfOfAmount(): Int =
        floor(getAmount() / 2f).toInt()

    fun setItemColor() {
        imagesBackground.children.pop()
        if (hasItem()) {
            imagesBackground.add(createTierColor())
        } else {
            imagesBackground.add(createTierColor(NOTHING))
        }
    }

    private fun createTierColor(): Image {
        val itemId = getCertainInventoryImage().inventoryItem.id
        return when {
            itemId.contains(BASIC) -> createTierColor(BASIC)
            itemId.contains(FINE) -> createTierColor(FINE)
            itemId.contains(SPECIALIST) -> createTierColor(SPECIALIST)
            itemId.contains(MASTERWORK) -> createTierColor(MASTERWORK)
            itemId.contains(EPIC) -> createTierColor(EPIC)
            itemId.contains(LEGENDARY) -> createTierColor(LEGENDARY)
            else -> createTierColor(NOTHING)
        }
    }

    private fun createTierColor(tierColor: String): Image {
        val textureRegion = resourceManager.getAtlasTexture(tierColor)
        val image = Image(textureRegion)
        image.setScaling(Scaling.none)
        image.setPosition(1f, 1f)
        image.name = "tier color"
        return image
    }

}
