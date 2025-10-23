package nl.t64.cot.screens.inventory.inventoryslot

import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.screens.dialog.MessageDialog
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.itemslot.ItemSlot


class InventorySlotUser private constructor(itemSlot: ItemSlot) {

    companion object {
        fun doPreBattlePotionAction(itemSlot: ItemSlot) {
            InventorySlotUser(itemSlot).onlyDoPotionActions()
        }

        fun doRegularAction(itemSlot: ItemSlot) {
            InventorySlotUser(itemSlot).doRegularAction()
        }
    }

    private val currentSlot: ItemSlot = itemSlot
    private val inventoryItem: InventoryItem = itemSlot.getCertainInventoryImage().inventoryItem
    private val selectedHero: HeroItem = InventoryUtils.getSelectedHero()

    private fun onlyDoPotionActions() {
        if (canUsePotion()) {
            usePotion()
        } else {
            showFailMessage()
        }
    }

    private fun doRegularAction() {
        if (inventoryItem.id == "crystal_of_time") {
            CrystalHandler.doAction()
        } else if ((inventoryItem.hp > 0 || inventoryItem.sp > 0) && canUsePotion()) {
            usePotion()
        } else {
            showFailMessage()
        }
    }

    private fun canUsePotion(): Boolean {
        if (selectedHero.isDead) return false

        val needsHp: Boolean = inventoryItem.hp > 0 && selectedHero.currentHp < selectedHero.maximumHp
        val needsSp: Boolean = inventoryItem.sp > 0 && selectedHero.currentSp < selectedHero.maximumSp
        val canUseBuff: Boolean =
            inventoryItem.protection > 0
                || inventoryItem.intelligence > 0
                || inventoryItem.dexterity > 0
                || inventoryItem.strength > 0
                || inventoryItem.speed > 0
                || inventoryItem.willpower > 0
                || inventoryItem.stealth > 0

        return needsHp || needsSp || canUseBuff
    }

    private fun usePotion() {
        currentSlot.decrementAmountBy(1)
        val (recoveredHp, recoveredSp) = applyRecoveryEffects()
        applyBuffEffects()
        showSuccessMessage(recoveredHp, recoveredSp)
    }

    private fun applyRecoveryEffects(): Pair<Int, Int> {
        val oldHp = selectedHero.currentHp
        val oldSp = selectedHero.currentSp

        if (inventoryItem.hp > 0) selectedHero.recoverPartHp(inventoryItem.hp)
        if (inventoryItem.sp > 0) selectedHero.recoverPartSp(inventoryItem.sp)

        return Pair(selectedHero.currentHp - oldHp, selectedHero.currentSp - oldSp)
    }

    private fun applyBuffEffects() {
        with(selectedHero.bonus) {
            if (inventoryItem.protection > 0) this.protectionFromPotion = inventoryItem.protection
            if (inventoryItem.intelligence > 0) this.intelligenceFromPotion = inventoryItem.intelligence
            if (inventoryItem.dexterity > 0) this.dexterityFromPotion = inventoryItem.dexterity
            if (inventoryItem.strength > 0) this.strengthFromPotion = inventoryItem.strength
            if (inventoryItem.speed > 0) this.speedFromPotion = inventoryItem.speed
            if (inventoryItem.willpower > 0) this.willpowerFromPotion = inventoryItem.willpower
            if (inventoryItem.stealth > 0) this.stealthFromPotion = inventoryItem.stealth
        }
    }

    private fun showSuccessMessage(recoveredHp: Int, recoveredSp: Int) {
        val message1 = "${selectedHero.name} used a ${inventoryItem.name} "
        val message2 = when {
            // @formatter:off
            inventoryItem.hp > 0 && inventoryItem.sp > 0 -> "and recovered $recoveredHp HP and $recoveredSp SP."
            inventoryItem.hp > 0                         -> "and recovered $recoveredHp HP."
            inventoryItem.sp > 0                         -> "and recovered $recoveredSp SP."
            inventoryItem.protection > 0                 -> "and gained ${inventoryItem.protection} Protection."
            inventoryItem.intelligence > 0               -> "and gained ${inventoryItem.intelligence} Intelligence."
            inventoryItem.dexterity > 0                  -> "and gained ${inventoryItem.dexterity} Dexterity."
            inventoryItem.strength > 0                   -> "and gained ${inventoryItem.strength} Strength."
            inventoryItem.speed > 0                      -> "and gained ${inventoryItem.speed} Speed."
            inventoryItem.willpower > 0                  -> "and gained ${inventoryItem.willpower} Willpower."
            inventoryItem.stealth > 0                    -> "and gained ${inventoryItem.stealth} Stealth."
            // @formatter:on
            else -> throw IllegalStateException("Effect of potion ${inventoryItem.name} unknown.")
        }
        MessageDialog(message1 + message2).show(currentSlot.stage, AudioEvent.SE_POTION)
    }

    private fun showFailMessage() {
        MessageDialog("A ${inventoryItem.name} cannot be used right now.")
            .show(currentSlot.stage, AudioEvent.SE_MENU_ERROR)
    }

}
