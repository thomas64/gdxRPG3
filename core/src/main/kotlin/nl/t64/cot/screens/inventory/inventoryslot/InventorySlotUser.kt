package nl.t64.cot.screens.inventory.inventoryslot

import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.itemslot.ItemSlot
import nl.t64.cot.screens.inventory.messagedialog.MessageDialog


class InventorySlotUser private constructor(itemSlot: ItemSlot) {

    companion object {
        fun doAction(itemSlot: ItemSlot) {
            InventorySlotUser(itemSlot).selectActionBasedOnItemId()
        }
    }

    private val currentSlot: ItemSlot = itemSlot
    private val inventoryItem: InventoryItem = itemSlot.getCertainInventoryImage().inventoryItem
    private val selectedHero: HeroItem = InventoryUtils.getSelectedHero()

    private fun selectActionBasedOnItemId() {
        when (inventoryItem.id) {
            "crystal_of_time" -> CrystalHandler.doAction()
            "healing_potion" -> possibleHandleDrink(::hpCondition, ::doHealing)
            "curing_potion" -> possibleHandleDrink(::hpCondition, ::doCuring)
            "restore_potion" -> possibleHandleDrink(::hpCondition, ::doRestore)
            "energy_potion" -> possibleHandleDrink(::mpCondition, ::doEnergy)
            "endurance_potion" -> possibleHandleDrink(::mpCondition, ::doEndurance)
            "stamina_potion" -> possibleHandleDrink(::mpCondition, ::doStamina)
        }
    }

    private fun possibleHandleDrink(condition: () -> Boolean, drinkAction: () -> Unit) {
        if (condition.invoke()) certainHandleDrink(drinkAction) else showFailMessage()
    }

    private fun certainHandleDrink(drinkAction: () -> Unit) {
        currentSlot.decrementAmountBy(1)
        val recoveredHp = drinkPotion(drinkAction)
        showSuccessMessage(recoveredHp)
    }

    private fun drinkPotion(drinkAction: () -> Unit): Int {
        val oldHp = selectedHero.currentHp
        drinkAction.invoke()
        val newHp = selectedHero.currentHp
        return newHp - oldHp
    }

    private fun showSuccessMessage(recoveredHp: Int) {
        MessageDialog("${selectedHero.name} used a ${inventoryItem.name} and recovered $recoveredHp HP.")
            .show(currentSlot.stage, AudioEvent.SE_CONVERSATION_NEXT)
    }

    private fun showFailMessage() {
        MessageDialog("A ${inventoryItem.name} cannot be used right now.")
            .show(currentSlot.stage, AudioEvent.SE_MENU_ERROR)
    }

    private fun hpCondition(): Boolean {
        return selectedHero.isAlive && selectedHero.currentHp < selectedHero.maximumHp
    }

    private fun mpCondition(): Boolean {
        return selectedHero.isAlive && selectedHero.currentMp < selectedHero.maximumMp
    }

    private fun doHealing() {
        selectedHero.recoverPartHp(20)
    }

    private fun doCuring() {
        selectedHero.recoverPartHp(80)
    }

    private fun doRestore() {
        selectedHero.recoverFullHp()
    }

    private fun doEnergy() {
        selectedHero.recoverPartMp(20)
    }

    private fun doEndurance() {
        selectedHero.recoverPartMp(80)
    }

    private fun doStamina() {
        selectedHero.recoverFullMp()
    }

}
