package nl.t64.cot.screens.inventory.inventoryslot

import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.itemslot.ItemSlot
import nl.t64.cot.screens.inventory.messagedialog.MessageDialog
import kotlin.math.roundToInt


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
            "healing_potion" -> possibleHandleDrink(::potionCondition, ::doHealing)
            "curing_potion" -> possibleHandleDrink(::potionCondition, ::doCuring)
            "stamina_potion" -> possibleHandleDrink(::staminaCondition, ::doStamina)
            "restore_potion" -> possibleHandleDrink(::potionCondition, ::doRestore)
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
        val oldHp = selectedHero.getCurrentHp()
        drinkAction.invoke()
        val newHp = selectedHero.getCurrentHp()
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

    private fun potionCondition(): Boolean {
        return selectedHero.isAlive && selectedHero.getCurrentHp() < selectedHero.getMaximumHp()
    }

    private fun staminaCondition(): Boolean {
        return selectedHero.isAlive && selectedHero.getCurrentStamina() < selectedHero.getMaximumStamina()
    }

    private fun doHealing() {
        val healPoints = (selectedHero.getMaximumHp() / 5f).roundToInt()
        selectedHero.recoverPartHp(healPoints)
    }

    private fun doCuring() {
        val healPoints = (selectedHero.getMaximumHp() / 3f).roundToInt()
        selectedHero.recoverPartHp(healPoints)
    }

    private fun doStamina() {
        selectedHero.recoverFullStamina()
    }

    private fun doRestore() {
        selectedHero.recoverFullHp()
    }

}
