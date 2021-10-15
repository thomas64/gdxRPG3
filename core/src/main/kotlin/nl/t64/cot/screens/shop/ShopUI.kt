package nl.t64.cot.screens.shop

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Window
import nl.t64.cot.Utils.createDefaultWindow
import nl.t64.cot.screens.ScreenUI
import nl.t64.cot.screens.inventory.HeroesTable
import nl.t64.cot.screens.inventory.WindowSelector
import nl.t64.cot.screens.inventory.equipslot.EquipSlotsTables
import nl.t64.cot.screens.inventory.inventoryslot.InventorySlotsTable
import nl.t64.cot.screens.inventory.tooltip.ItemSlotTooltip
import nl.t64.cot.screens.inventory.tooltip.ShopSlotTooltipBuy
import nl.t64.cot.screens.inventory.tooltip.ShopSlotTooltipSell


private const val EQUIP_WINDOW_POSITION_X = 1566f
private const val EQUIP_WINDOW_POSITION_Y = 50f
private const val INVENTORY_WINDOW_POSITION_X = 1145f
private const val INVENTORY_WINDOW_POSITION_Y = 50f
private const val SHOP_WINDOW_POSITION_X = 532f
private const val SHOP_WINDOW_POSITION_Y = 50f
private const val MERCHANT_WINDOW_POSITION_X = 63f
private const val MERCHANT_WINDOW_POSITION_Y = 50f
private const val HEROES_WINDOW_POSITION_X = 63f
private const val HEROES_WINDOW_POSITION_Y = 834f

private const val TITLE_PERSONAL = "   Equipment"
private const val TITLE_GLOBAL = "   Inventory"
private const val TITLE_SHOP = "   Shop"
private const val TITLE_MERCHANT = "   Merchant"
private const val TITLE_HEROES = "   Heroes"

internal class ShopUI(
    stage: Stage,
    npcId: String,
    shopId: String,

    private val shopSlotTooltipSell: ItemSlotTooltip = ShopSlotTooltipSell(),
    private val shopSlotTooltipBuy: ItemSlotTooltip = ShopSlotTooltipBuy(),

    private val equipSlotsTables: EquipSlotsTables = EquipSlotsTables(shopSlotTooltipSell),
    private val equipWindow: Window = createDefaultWindow(TITLE_PERSONAL, equipSlotsTables.getCurrentEquipTable()),

    private val inventorySlotsTable: InventorySlotsTable = InventorySlotsTable(shopSlotTooltipSell),
    private val inventoryWindow: Window = createDefaultWindow(TITLE_GLOBAL, inventorySlotsTable.container),

    private val shopSlotsTable: ShopSlotsTable = ShopSlotsTable(shopId, shopSlotTooltipBuy),
    private val shopWindow: Window = createDefaultWindow(TITLE_SHOP, shopSlotsTable.container),

    merchantTable: MerchantTable = MerchantTable(npcId),
    private val merchantWindow: Window = createDefaultWindow(TITLE_MERCHANT, merchantTable.table),

    heroesTable: HeroesTable = HeroesTable(),
    private val heroesWindow: Window = createDefaultWindow(TITLE_HEROES, heroesTable.heroes),

    tableList: List<WindowSelector> = listOf(shopSlotsTable, inventorySlotsTable, equipSlotsTables),
    selectedTableIndex: Int = 0

) : ScreenUI(stage, heroesTable, tableList, selectedTableIndex) {

    init {
        super.init()
    }

    override fun getEquipSlotsTables(): EquipSlotsTables {
        return equipSlotsTables
    }

    override fun getInventorySlotsTable(): InventorySlotsTable {
        return inventorySlotsTable
    }

    override fun getShopSlotsTable(): ShopSlotsTable {
        return shopSlotsTable
    }

    fun updateSelectedHero(updateHero: () -> Unit) {
        getSelectedTable().deselectCurrentSlot()

        val oldCurrentIndex = equipSlotsTables.getIndexOfCurrentSlot()
        equipWindow.getChild(1).remove()
        updateHero.invoke()
        equipWindow.add(equipSlotsTables.getCurrentEquipTable())
        equipSlotsTables.setCurrentByIndex(oldCurrentIndex)

        setFocusOnSelectedTable()
        getSelectedTable().selectCurrentSlot()
    }

    fun takeOne() {
        getSelectedTable().takeOne()
    }

    fun takeHalf() {
        getSelectedTable().takeHalf()
    }

    fun takeFull() {
        getSelectedTable().takeFull()
    }

    fun equip() {
        getSelectedTable().doAction()
    }

    fun update() {
        heroesTable.update()

        merchantWindow.pack()
        heroesWindow.pack()
    }

    override fun setWindowPositions() {
        equipWindow.setPosition(EQUIP_WINDOW_POSITION_X, EQUIP_WINDOW_POSITION_Y)
        inventoryWindow.setPosition(INVENTORY_WINDOW_POSITION_X, INVENTORY_WINDOW_POSITION_Y)
        shopWindow.setPosition(SHOP_WINDOW_POSITION_X, SHOP_WINDOW_POSITION_Y)
        merchantWindow.setPosition(MERCHANT_WINDOW_POSITION_X, MERCHANT_WINDOW_POSITION_Y)
        heroesWindow.setPosition(HEROES_WINDOW_POSITION_X, HEROES_WINDOW_POSITION_Y)
    }

    override fun addToStage() {
        shopSlotTooltipSell.addToStage(stage)
        shopSlotTooltipBuy.addToStage(stage)
        stage.addActor(equipWindow)
        stage.addActor(inventoryWindow)
        stage.addActor(shopWindow)
        stage.addActor(merchantWindow)
        stage.addActor(heroesWindow)
    }

}
