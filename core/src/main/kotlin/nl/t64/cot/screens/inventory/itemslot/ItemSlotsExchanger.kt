package nl.t64.cot.screens.inventory.itemslot

import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.party.inventory.InventoryDatabase
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.messagedialog.MessageDialog


class ItemSlotsExchanger {

    private val draggedItem: InventoryImage
    private val sourceSlot: ItemSlot
    private val targetSlot: ItemSlot
    private var isSuccessfullyExchanged: Boolean

    constructor(draggedItem: InventoryImage, sourceSlot: ItemSlot, targetSlot: ItemSlot) {
        this.draggedItem = draggedItem.createCopy()
        this.sourceSlot = sourceSlot
        this.targetSlot = targetSlot
        isSuccessfullyExchanged = false
    }

    constructor(draggedItem: InventoryImage, amount: Int, sourceSlot: ItemSlot, targetSlot: ItemSlot) {
        this.draggedItem = draggedItem.createCopy(amount)
        this.sourceSlot = sourceSlot
        this.targetSlot = targetSlot
        isSuccessfullyExchanged = false
    }

    fun exchange() {
        if (targetSlot.doesAcceptItem(draggedItem)) {
            handleExchange()
        } else {
            sourceSlot.putItemBack(draggedItem)
        }
    }

    private fun handleExchange() {
        if (isShopPurchase()) {
            handlePurchase()
        } else if (isShopBarter()) {
            handleBarter()
        } else {
            handlePossibleExchange()
        }
    }

    private fun handlePurchase() {
        val totalMerchant = gameData.party.getSumOfSkill(SkillItemId.MERCHANT)
        val totalPrice = draggedItem.inventoryItem.getBuyPriceTotal(totalMerchant)
        if (gameData.inventory.hasEnoughOfItem("gold", totalPrice)) {
            handlePossibleExchange()
            if (isSuccessfullyExchanged) {
                InventoryUtils.getScreenUI().getInventorySlotsTable().removeResource("gold", totalPrice)
            }
        } else {
            val errorMessage = "I'm sorry. You don't seem to have enough gold."
            MessageDialog(errorMessage).show(sourceSlot.stage, AudioEvent.SE_MENU_ERROR)
            sourceSlot.putItemBack(draggedItem)
        }
        InventoryUtils.getScreenUI().getShopSlotsTable().refreshPurchaseColor()
    }

    private fun handleBarter() {
        val totalMerchant = gameData.party.getSumOfSkill(SkillItemId.MERCHANT)
        val totalValue = draggedItem.inventoryItem.getSellValueTotal(totalMerchant)
        if (totalValue == 0) {
            val errorMessage = "I'm sorry. I can't accept that."
            MessageDialog(errorMessage).show(sourceSlot.stage, AudioEvent.SE_MENU_ERROR)
            sourceSlot.putItemBack(draggedItem)
            return
        }
        if (gameData.inventory.hasRoomForResource("gold")) {
            handlePossibleExchange()
            if (isSuccessfullyExchanged) {
                val gold = InventoryDatabase.createInventoryItem("gold", totalValue)
                InventoryUtils.getScreenUI().getInventorySlotsTable().addResource(gold)
            }
        } else {
            val errorMessage = "I'm sorry. You don't seem to have room for gold."
            MessageDialog(errorMessage).show(sourceSlot.stage, AudioEvent.SE_MENU_ERROR)
            sourceSlot.putItemBack(draggedItem)
        }
        InventoryUtils.getScreenUI().getShopSlotsTable().refreshPurchaseColor()
    }

    private fun handlePossibleExchange() {
        targetSlot.getPossibleInventoryImage()?.let {
            putItemInFilledSlot(it)
        } ?: putItemInEmptySlot()
    }

    private fun putItemInFilledSlot(itemAtTarget: InventoryImage) {
        if (targetSlot == sourceSlot
            || (draggedItem.isSameItemAs(itemAtTarget) && draggedItem.isStackable())
        ) {
            doAudio()
            targetSlot.incrementAmountBy(draggedItem.getAmount())
            isSuccessfullyExchanged = true
        } else {
            swapStacks()
        }
    }

    private fun swapStacks() {
        if (doTargetAndSourceAcceptEachOther() && !sourceSlot.hasItem()) {
            doAudio()
            sourceSlot.addToStack(targetSlot.getCertainInventoryImage())
            targetSlot.addToStack(draggedItem)
            isSuccessfullyExchanged = true
        } else {
            sourceSlot.putItemBack(draggedItem)
            isSuccessfullyExchanged = false
        }
    }

    private fun putItemInEmptySlot() {
        doAudio()
        targetSlot.putInSlot(draggedItem)
        isSuccessfullyExchanged = true
    }

    private fun doAudio() {
        if (isShopPurchase()) {
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_COINS_BUY)
        } else if (isShopBarter()) {
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_COINS_SELL)
        } else if (isEquipingOrDequiping()) {
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_EQUIP)
        } else if (isSameSlotOrBoxOrStorage()) {
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_TAKE)
        } else {
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_TAKE)
        }
    }

    private fun isShopPurchase(): Boolean =
        sourceSlot.filterGroup == InventoryGroup.SHOP_ITEM
                && targetSlot.filterGroup != InventoryGroup.SHOP_ITEM

    private fun isShopBarter(): Boolean =
        sourceSlot.filterGroup != InventoryGroup.SHOP_ITEM
                && targetSlot.filterGroup == InventoryGroup.SHOP_ITEM

    private fun doTargetAndSourceAcceptEachOther(): Boolean {
        return !(sourceSlot.filterGroup == InventoryGroup.SHOP_ITEM
                || targetSlot.filterGroup == InventoryGroup.SHOP_ITEM)
                && targetSlot.doesAcceptItem(draggedItem)
                && sourceSlot.doesAcceptItem(targetSlot.getCertainInventoryImage())
    }

    private fun isEquipingOrDequiping(): Boolean =
        (!sourceSlot.isOnHero() && targetSlot.isOnHero())
                || (sourceSlot.isOnHero() && !targetSlot.isOnHero())

    private fun isSameSlotOrBoxOrStorage(): Boolean =
        targetSlot == sourceSlot
                || targetSlot.filterGroup == sourceSlot.filterGroup

}
