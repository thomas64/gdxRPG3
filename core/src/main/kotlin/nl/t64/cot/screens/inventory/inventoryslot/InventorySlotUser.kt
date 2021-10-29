package nl.t64.cot.screens.inventory.inventoryslot

import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.itemslot.ItemSlot
import nl.t64.cot.screens.inventory.messagedialog.MessageDialog
import kotlin.math.roundToInt


class InventorySlotUser {

    private lateinit var currentSlot: ItemSlot
    private lateinit var inventoryItem: InventoryItem
    private lateinit var selectedHero: HeroItem

    fun drink(itemSlot: ItemSlot) {
        currentSlot = itemSlot
        inventoryItem = itemSlot.getCertainInventoryImage().inventoryItem
        selectedHero = InventoryUtils.getSelectedHero()
        when (inventoryItem.id) {
            "healing_potion" -> handlePotions(::potionFilter, ::doHealing)
            "curing_potion" -> handlePotions(::potionFilter, ::doCuring)
            "stamina_potion" -> handlePotions(::staminaFilter, ::doStamina)
            "restore_potion" -> handlePotions(::potionFilter, ::doRestore)
        }
    }

    private fun handlePotions(filter: () -> Boolean, action: () -> Unit) {
        if (filter.invoke()) {
            currentSlot.decrementAmountBy(1)
            val oldHp = selectedHero.getCurrentHp()
            action.invoke()
            val newHp = selectedHero.getCurrentHp()
            MessageDialog("${selectedHero.name} used a ${inventoryItem.name} and recovered ${newHp - oldHp} HP.")
                .show(currentSlot.stage, AudioEvent.SE_CONVERSATION_NEXT)
        } else {
            MessageDialog("A ${inventoryItem.name} cannot be used right now.")
                .show(currentSlot.stage, AudioEvent.SE_MENU_ERROR)
        }
    }

    private fun potionFilter(): Boolean {
        return selectedHero.getCurrentHp() < selectedHero.getMaximumHp()
    }

    private fun staminaFilter(): Boolean {
        return selectedHero.getCurrentStamina() < selectedHero.getMaximumStamina()
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
