package nl.t64.cot.components.battle

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.inventory.BattleWeaponItem
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem


class WeaponAction(
    currentParticipant: Participant,
    private val selectedWeapon: BattleWeaponItem
) {
    private val character: Character = currentParticipant.character
    private val hero: HeroItem = character as HeroItem

    fun isUnableToEquip(): String? {
        return hero.createMessageIfNotAbleToEquip(selectedWeapon.inventoryItem)
    }

    fun createConfirmationMessage(): String {
        return """
            Do you want to equip ${selectedWeapon.name} for 3 AP?""".trimIndent()
    }

    fun handle(): String {
        val currentWeapon: InventoryItem? = hero.getInventoryItem(InventoryGroup.WEAPON)
        val newWeapon: InventoryItem = selectedWeapon.inventoryItem

        gameData.inventory.forceRemoveItem(newWeapon)
        hero.forceSetInventoryItemFor(InventoryGroup.WEAPON, newWeapon)
        currentWeapon?.let { gameData.inventory.autoSetItem(it) }
        return "${character.name} equipped ${selectedWeapon.name}."
    }

}
