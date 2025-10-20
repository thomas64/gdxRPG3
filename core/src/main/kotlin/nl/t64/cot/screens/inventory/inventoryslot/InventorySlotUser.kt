package nl.t64.cot.screens.inventory.inventoryslot

import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.screens.dialog.MessageDialog
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.itemslot.ItemSlot


class InventorySlotUser private constructor(itemSlot: ItemSlot) {

    companion object {
        fun doAction(itemSlot: ItemSlot) {
            InventorySlotUser(itemSlot).selectActionBasedOnItemId()
        }
    }

    private val currentSlot: ItemSlot = itemSlot
    private val inventoryItem: InventoryItem = itemSlot.getCertainInventoryImage().inventoryItem
    private val selectedHero: HeroItem = InventoryUtils.getSelectedHero()

    private enum class RecoveryType { HP, SP, BOTH }

    private data class PotionEffect(
        val recoveryType: RecoveryType,
        val hpAmount: Int = 0,
        val spAmount: Int = 0
    )

    private fun selectActionBasedOnItemId() {
        when (inventoryItem.id) {
            "crystal_of_time" -> CrystalHandler.doAction()
            "healing_potion" -> handlePotion(PotionEffect(RecoveryType.HP, hpAmount = 20))
            "healing_potion_+" -> handlePotion(PotionEffect(RecoveryType.HP, hpAmount = 80))
            "healing_potion_++" -> handlePotion(PotionEffect(RecoveryType.HP, hpAmount = 200))
            "energy_potion" -> handlePotion(PotionEffect(RecoveryType.SP, spAmount = 10))
            "energy_potion_+" -> handlePotion(PotionEffect(RecoveryType.SP, spAmount = 30))
            "energy_potion_++" -> handlePotion(PotionEffect(RecoveryType.SP, spAmount = 70))
            "restore_potion" -> handlePotion(PotionEffect(RecoveryType.BOTH, hpAmount = 20, spAmount = 10))
            "restore_potion_+" -> handlePotion(PotionEffect(RecoveryType.BOTH, hpAmount = 80, spAmount = 30))
            "restore_potion_++" -> handlePotion(PotionEffect(RecoveryType.BOTH, hpAmount = 200, spAmount = 70))
            // todo, other potions for pre battle
        }
    }

    private fun handlePotion(effect: PotionEffect) {
        if (canUsePotion(effect.recoveryType)) {
            usePotion(effect)
        } else {
            showFailMessage()
        }
    }

    private fun canUsePotion(recoveryType: RecoveryType): Boolean {
        if (!selectedHero.isAlive) return false

        return when (recoveryType) {
            RecoveryType.HP -> selectedHero.currentHp < selectedHero.maximumHp
            RecoveryType.SP -> selectedHero.currentSp < selectedHero.maximumSp
            RecoveryType.BOTH -> selectedHero.currentHp < selectedHero.maximumHp
                || selectedHero.currentSp < selectedHero.maximumSp
        }
    }

    private fun usePotion(effect: PotionEffect) {
        currentSlot.decrementAmountBy(1)
        val (recoveredHp, recoveredSp) = applyPotionEffect(effect)
        showSuccessMessage(effect.recoveryType, recoveredHp, recoveredSp)
    }

    private fun applyPotionEffect(effect: PotionEffect): Pair<Int, Int> {
        var recoveredHp = 0
        var recoveredSp = 0

        if (effect.hpAmount > 0) {
            val oldHp = selectedHero.currentHp
            selectedHero.recoverPartHp(effect.hpAmount)
            recoveredHp = selectedHero.currentHp - oldHp
        }

        if (effect.spAmount > 0) {
            val oldSp = selectedHero.currentSp
            selectedHero.recoverPartSp(effect.spAmount)
            recoveredSp = selectedHero.currentSp - oldSp
        }

        return Pair(recoveredHp, recoveredSp)
    }

    private fun showSuccessMessage(recoveryType: RecoveryType, recoveredHp: Int, recoveredSp: Int) {
        val message = when (recoveryType) {
            RecoveryType.HP -> "${selectedHero.name} used a ${inventoryItem.name} and recovered $recoveredHp HP."
            RecoveryType.SP -> "${selectedHero.name} used a ${inventoryItem.name} and recovered $recoveredSp SP."
            RecoveryType.BOTH -> "${selectedHero.name} used a ${inventoryItem.name} and recovered $recoveredHp HP and $recoveredSp SP."
        }
        MessageDialog(message).show(currentSlot.stage, AudioEvent.SE_POTION)
    }

    private fun showFailMessage() {
        MessageDialog("A ${inventoryItem.name} cannot be used right now.")
            .show(currentSlot.stage, AudioEvent.SE_MENU_ERROR)
    }

}
