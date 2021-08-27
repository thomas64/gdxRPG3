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

internal class ShopUI private constructor(
    stage: Stage,
    private val shopSlotTooltipSell: ItemSlotTooltip,
    private val shopSlotTooltipBuy: ItemSlotTooltip,
    equipWindow: Window,
    private val inventoryWindow: Window,
    private val shopWindow: Window,
    private val merchantWindow: Window,
    private val heroesWindow: Window,
    equipSlotsTables: EquipSlotsTables,
    inventorySlotsTable: InventorySlotsTable,
    private val shopSlotsTable: ShopSlotsTable,
    heroesTable: HeroesTable,
    tableList: List<WindowSelector>,
    selectedTableIndex: Int
) : ScreenUI(equipWindow, equipSlotsTables, inventorySlotsTable, heroesTable, tableList, selectedTableIndex) {

    companion object {
        fun create(stage: Stage, npcId: String, shopId: String): ShopUI {

            val shopSlotTooltipSell = ShopSlotTooltipSell()
            val shopSlotTooltipBuy = ShopSlotTooltipBuy()

            val equipSlotsTables = EquipSlotsTables(shopSlotTooltipSell)
            val equipWindow = createDefaultWindow(TITLE_PERSONAL, equipSlotsTables.getCurrentEquipTable())

            val inventorySlotsTable = InventorySlotsTable(shopSlotTooltipSell)
            val inventoryWindow = createDefaultWindow(TITLE_GLOBAL, inventorySlotsTable.container)

            val shopSlotsTable = ShopSlotsTable(shopId, shopSlotTooltipBuy)
            val shopWindow = createDefaultWindow(TITLE_SHOP, shopSlotsTable.container)

            val merchantTable = MerchantTable(npcId)
            val merchantWindow = createDefaultWindow(TITLE_MERCHANT, merchantTable.table)

            val heroesTable = HeroesTable()
            val heroesWindow = createDefaultWindow(TITLE_HEROES, heroesTable.heroes)

            val tableList = listOf(shopSlotsTable, inventorySlotsTable, equipSlotsTables)
            val selectedTableIndex = 0

            return ShopUI(stage, shopSlotTooltipSell, shopSlotTooltipBuy,
                          equipWindow, inventoryWindow, shopWindow, merchantWindow, heroesWindow,
                          equipSlotsTables, inventorySlotsTable, shopSlotsTable, heroesTable,
                          tableList, selectedTableIndex)
        }
    }

    init {
        setWindowPositions()
        addToStage(stage)
        setFocusOnSelectedTable()
        getSelectedTable().selectCurrentSlot()
    }

    override fun getShopSlotsTable(): ShopSlotsTable {
        return shopSlotsTable
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

    private fun setWindowPositions() {
        equipWindow.setPosition(EQUIP_WINDOW_POSITION_X, EQUIP_WINDOW_POSITION_Y)
        inventoryWindow.setPosition(INVENTORY_WINDOW_POSITION_X, INVENTORY_WINDOW_POSITION_Y)
        shopWindow.setPosition(SHOP_WINDOW_POSITION_X, SHOP_WINDOW_POSITION_Y)
        merchantWindow.setPosition(MERCHANT_WINDOW_POSITION_X, MERCHANT_WINDOW_POSITION_Y)
        heroesWindow.setPosition(HEROES_WINDOW_POSITION_X, HEROES_WINDOW_POSITION_Y)
    }

    private fun addToStage(stage: Stage) {
        shopSlotTooltipSell.addToStage(stage)
        shopSlotTooltipBuy.addToStage(stage)
        stage.addActor(equipWindow)
        stage.addActor(inventoryWindow)
        stage.addActor(shopWindow)
        stage.addActor(merchantWindow)
        stage.addActor(heroesWindow)
    }

}
