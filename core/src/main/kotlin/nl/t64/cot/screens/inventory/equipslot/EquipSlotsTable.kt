package nl.t64.cot.screens.inventory.equipslot

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.screens.inventory.itemslot.InventoryImage
import nl.t64.cot.screens.inventory.itemslot.ItemSlot
import nl.t64.cot.screens.inventory.tooltip.ItemSlotTooltip


private const val SPRITE_SILHOUETTE = "sprites/silhouette.png"
private const val SLOT_SIZE = 64f
private const val EQUIP_SPACING = 10f

class EquipSlotsTable(
    private val heroItem: HeroItem,
    private val tooltip: ItemSlotTooltip
) {

    val container: Table = Table()

    private val helmetSlot: ItemSlot = createEquipSlot(0, InventoryGroup.HELMET)
    private val necklaceSlot: ItemSlot = createEquipSlot(1, InventoryGroup.NECKLACE)
    private val shouldersSlot: ItemSlot = createEquipSlot(21, InventoryGroup.SHOULDERS)

    private val chestSlot: ItemSlot = createEquipSlot(2, InventoryGroup.CHEST)
    private val cloakSlot: ItemSlot = createEquipSlot(22, InventoryGroup.CLOAK)

    private val bracersSlot: ItemSlot = createEquipSlot(3, InventoryGroup.BRACERS)
    private val glovesSlot: ItemSlot = createEquipSlot(4, InventoryGroup.GLOVES)
    private val weaponSlot: ItemSlot = createEquipSlot(5, InventoryGroup.WEAPON)

    private val accessorySlot: ItemSlot = createEquipSlot(23, InventoryGroup.ACCESSORY)
    private val ringSlot: ItemSlot = createEquipSlot(24, InventoryGroup.RING)
    private val shieldSlot: ItemSlot = createEquipSlot(25, InventoryGroup.SHIELD)

    private val beltSlot: ItemSlot = createEquipSlot(14, InventoryGroup.BELT)
    private val pantsSlot: ItemSlot = createEquipSlot(15, InventoryGroup.PANTS)
    private val bootsSlot: ItemSlot = createEquipSlot(16, InventoryGroup.BOOTS)

    private val equipSlotList: List<ItemSlot> = listOf(helmetSlot, necklaceSlot, shouldersSlot, chestSlot, cloakSlot,
                                                       bracersSlot, glovesSlot, weaponSlot, accessorySlot, ringSlot,
                                                       shieldSlot, beltSlot, pantsSlot, bootsSlot)

    init {
        createTable()
    }

    private val selector = EquipSlotSelector(equipSlotList)
    private val taker = EquipSlotTaker(selector)

    init {
        container.addAction(Actions.sequence(
                Actions.delay(.1f),
                Actions.addListener(EquipSlotsTableListener { selector.trySelectNewSlot(it) }, false))
        )
    }

    private fun createEquipSlot(index: Int, inventoryGroup: InventoryGroup): EquipSlot {
        return EquipSlot(index, inventoryGroup, tooltip, heroItem)
    }

    fun toggleTooltip() {
        tooltip.toggle(selector.getCurrentSlot())
    }

    fun toggleCompare() {
        tooltip.toggleCompare(selector.getCurrentSlot())
    }

    fun dequipItem() {
        taker.dequip(selector.getCurrentSlot())
    }

    fun getIndexOfCurrentSlot(): Int = selector.getIndex()

    fun deselectCurrentSlot() {
        selector.deselectCurrentSlot()
    }

    fun selectCurrentSlot() {
        selector.selectCurrentSlot()
    }

    fun setCurrentByIndex(index: Int) {
        selector.setNewCurrentByIndex(index)
    }

    fun getPossibleSlotOfGroup(inventoryGroup: InventoryGroup): ItemSlot? {
        return equipSlotList.firstOrNull { it.filterGroup == inventoryGroup }
    }

    private fun createTable() {
        equipSlotList.forEach { addPossibleEquippedItemToEquipSlot(it) }
        setDefaults()
        fillTable()
    }

    private fun addPossibleEquippedItemToEquipSlot(equipSlot: ItemSlot) {
        heroItem.getInventoryItem(equipSlot.filterGroup)?.let {
            equipSlot.addToStack(InventoryImage(it))
        }
    }

    private fun setDefaults() {
        container.defaults().space(EQUIP_SPACING).size(SLOT_SIZE, SLOT_SIZE)
        val texture = resourceManager.getTextureAsset(SPRITE_SILHOUETTE)
        val sprite = Sprite(texture)
        val silhouette = SpriteDrawable(sprite)
        container.background = silhouette
    }

    private fun fillTable() {
        container.add(helmetSlot).colspan(3)

        container.row()

        container.add()
        container.add(necklaceSlot)
        container.add(shouldersSlot).left()

        container.row()

        val bodySlots = Table()
        bodySlots.defaults().space(EQUIP_SPACING).size(SLOT_SIZE, SLOT_SIZE)
        bodySlots.add(chestSlot)
        bodySlots.add(cloakSlot)
        container.add(bodySlots).colspan(3)

        container.row()

        container.add(bracersSlot).expandX().left().padLeft(EQUIP_SPACING)
        container.add()
        container.add(accessorySlot).expandX().right().padRight(EQUIP_SPACING)

        container.row()

        container.add(glovesSlot).expandX().left().padLeft(EQUIP_SPACING)
        container.add(beltSlot)
        container.add(ringSlot).expandX().right().padRight(EQUIP_SPACING)

        container.row()

        container.add(weaponSlot).expandX().left().padLeft(EQUIP_SPACING)
        container.add(pantsSlot)
        container.add(shieldSlot).expandX().right().padRight(EQUIP_SPACING)

        container.row()

        container.add().colspan(3)
        container.row()
        container.add().colspan(3)
        container.row()

        container.add(bootsSlot).colspan(3)
    }

}
