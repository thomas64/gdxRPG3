package nl.t64.cot.components.battle

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.inventory.BattleWeaponItem
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem


private const val SWITCH_WEAPON_AP: Int = 3

class WeaponAction(
    private val currentParticipant: Participant,
    private val selectedWeapon: BattleWeaponItem
) {
    private val character: Character = currentParticipant.character
    private val hero: HeroItem = character as HeroItem

    fun isUnableToEquip(): String? {
        return hero.createMessageIfNotAbleToEquip(selectedWeapon.inventoryItem)
            ?: createMessageIfCostingTooMuchAp()
    }

    fun createConfirmationMessage(): String {
        val currentWeapon: InventoryItem? = hero.getInventoryItem(InventoryGroup.WEAPON)
        val newWeapon: InventoryItem = selectedWeapon.inventoryItem

        return currentWeapon?.let {
            """
                Current weapon:
                ${it.name}
                Durability: ${it.durability}
                ${it.getRangeText()}
                Chance to hit: ${it.getAttributeOfCalcAttributeId(CalcAttributeId.BASE_HIT)}
                Damage: ${it.getAttributeOfCalcAttributeId(CalcAttributeId.DAMAGE)}
                _________________

                New weapon:
                ${newWeapon.name}
                Durability: ${newWeapon.durability}
                ${newWeapon.getRangeText()}
                Chance to hit: ${newWeapon.getAttributeOfCalcAttributeId(CalcAttributeId.BASE_HIT)}
                Damage: ${newWeapon.getAttributeOfCalcAttributeId(CalcAttributeId.DAMAGE)}
                _________________

                Equip ($SWITCH_WEAPON_AP AP) ?
            """.trimIndent()
        } ?: """
            ${newWeapon.name}
            Durability: ${newWeapon.durability}
            ${newWeapon.getRangeText()}
            Chance to hit: ${newWeapon.getAttributeOfCalcAttributeId(CalcAttributeId.BASE_HIT)}
            Damage: ${newWeapon.getAttributeOfCalcAttributeId(CalcAttributeId.DAMAGE)}
            _________________

            Equip ($SWITCH_WEAPON_AP AP) ?
        """.trimIndent()
    }

    fun handle(): String {
        currentParticipant.currentAP -= SWITCH_WEAPON_AP
        val currentWeapon: InventoryItem? = hero.getInventoryItem(InventoryGroup.WEAPON)
        val newWeapon: InventoryItem = selectedWeapon.inventoryItem

        gameData.inventory.forceRemoveItem(newWeapon)
        hero.forceSetInventoryItemFor(InventoryGroup.WEAPON, newWeapon)
        currentWeapon?.let { gameData.inventory.autoSetItem(it) }
        return "${character.name} equipped ${selectedWeapon.name}."
    }

    private fun createMessageIfCostingTooMuchAp(): String? {
        return when {
            currentParticipant.currentAP < SWITCH_WEAPON_AP -> "Not enough AP!"
            else -> null
        }
    }
}
