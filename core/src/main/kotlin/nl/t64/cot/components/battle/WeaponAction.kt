package nl.t64.cot.components.battle

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.inventory.BattleWeaponItem
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.stats.StatItemId


private const val SWITCH_WEAPON_AP: Int = 3

class WeaponAction(
    private val currentParticipant: Participant,
    private val selectedEquipment: BattleWeaponItem
) {
    private val character: Character = currentParticipant.character
    private val hero: HeroItem = character as HeroItem

    fun isUnableToEquip(): String? {
        return hero.createMessageIfNotAbleToEquip(selectedEquipment.inventoryItem)
            ?: createMessageIfCostingTooMuchAp()
    }

    fun createConfirmationMessage(): String {
        return when (selectedEquipment.group) {
            InventoryGroup.WEAPON -> createConfirmationMessageWeapon()
            InventoryGroup.SHIELD -> createConfirmationMessageShield()
            else -> throw IllegalArgumentException("Invalid inventory group: ${selectedEquipment.group}")
        }
    }

    fun handle(): String {
        return when (selectedEquipment.group) {
            InventoryGroup.WEAPON -> handleWeapon()
            InventoryGroup.SHIELD -> handleShield()
            else -> throw IllegalArgumentException("Invalid inventory group: ${selectedEquipment.group}")
        }
    }

    private fun createConfirmationMessageWeapon(): String {
        val currentWeapon: InventoryItem? = hero.getInventoryItem(InventoryGroup.WEAPON)
        val newWeapon: InventoryItem = selectedEquipment.inventoryItem

        if (newWeapon.durability == -1) {
            return currentWeapon!!.let {
                """
                    ${it.name}
                    ${it.getRangeText()}
                    Durability: ${it.durability}
                    Chance to hit: ${it.getAttributeOfCalcAttributeId(CalcAttributeId.BASE_HIT)}
                    Damage: ${it.getAttributeOfCalcAttributeId(CalcAttributeId.DAMAGE)}
                    _________________

                    Unequip (${SWITCH_WEAPON_AP} AP) ?
                """.trimIndent()
            }
        }

        return currentWeapon?.let {
            """
                Current weapon:
                ${it.name}
                ${it.getRangeText()}
                Durability: ${it.durability}
                Chance to hit: ${it.getAttributeOfCalcAttributeId(CalcAttributeId.BASE_HIT)}
                Damage: ${it.getAttributeOfCalcAttributeId(CalcAttributeId.DAMAGE)}
                _________________

                New weapon:
                ${newWeapon.name}
                ${newWeapon.getRangeText()}
                Durability: ${newWeapon.durability}
                Chance to hit: ${newWeapon.getAttributeOfCalcAttributeId(CalcAttributeId.BASE_HIT)}
                Damage: ${newWeapon.getAttributeOfCalcAttributeId(CalcAttributeId.DAMAGE)}
                _________________

                Equip ($SWITCH_WEAPON_AP AP) ?
            """.trimIndent()
        } ?: """
            ${newWeapon.name}
            ${newWeapon.getRangeText()}
            Durability: ${newWeapon.durability}
            Chance to hit: ${newWeapon.getAttributeOfCalcAttributeId(CalcAttributeId.BASE_HIT)}
            Damage: ${newWeapon.getAttributeOfCalcAttributeId(CalcAttributeId.DAMAGE)}
            _________________

            Equip ($SWITCH_WEAPON_AP AP) ?
        """.trimIndent()
    }

    private fun createConfirmationMessageShield(): String {
        val currentShield: InventoryItem? = hero.getInventoryItem(InventoryGroup.SHIELD)
        val newShield: InventoryItem = selectedEquipment.inventoryItem

        if (newShield.durability == -1) {
            return currentShield!!.let {
                """
                    ${it.name}
                    Durability: ${it.durability}
                    Protection: ${it.getAttributeOfCalcAttributeId(CalcAttributeId.PROTECTION)}
                    Defense: ${it.getAttributeOfCalcAttributeId(CalcAttributeId.DEFENSE)}
                    Speed: ${it.getAttributeOfStatItemId(StatItemId.SPEED)}
                    Stealth: ${it.getAttributeOfSkillItemId(SkillItemId.STEALTH)}
                    _________________

                    Unequip (${SWITCH_WEAPON_AP} AP) ?
                """.trimIndent()
            }
        }

        return currentShield?.let {
            """
                Current shield:
                ${it.name}
                Durability: ${it.durability}
                Protection: ${it.getAttributeOfCalcAttributeId(CalcAttributeId.PROTECTION)}
                Defense: ${it.getAttributeOfCalcAttributeId(CalcAttributeId.DEFENSE)}
                Speed: ${it.getAttributeOfStatItemId(StatItemId.SPEED)}
                Stealth: ${it.getAttributeOfSkillItemId(SkillItemId.STEALTH)}
                _________________

                New shield:
                ${newShield.name}
                Durability: ${newShield.durability}
                Protection: ${newShield.getAttributeOfCalcAttributeId(CalcAttributeId.PROTECTION)}
                Defense: ${newShield.getAttributeOfCalcAttributeId(CalcAttributeId.DEFENSE)}
                Speed: ${newShield.getAttributeOfStatItemId(StatItemId.SPEED)}
                Stealth: ${newShield.getAttributeOfSkillItemId(SkillItemId.STEALTH)}
                _________________

                Equip ($SWITCH_WEAPON_AP AP) ?
            """.trimIndent()
        } ?: """
            ${newShield.name}
            Durability: ${newShield.durability}
            Protection: ${newShield.getAttributeOfCalcAttributeId(CalcAttributeId.PROTECTION)}
            Defense: ${newShield.getAttributeOfCalcAttributeId(CalcAttributeId.DEFENSE)}
            Speed: ${newShield.getAttributeOfStatItemId(StatItemId.SPEED)}
            Stealth: ${newShield.getAttributeOfSkillItemId(SkillItemId.STEALTH)}
            _________________

            Equip ($SWITCH_WEAPON_AP AP) ?
        """.trimIndent()
    }

    private fun handleWeapon(): String {
        currentParticipant.currentAP -= SWITCH_WEAPON_AP
        val currentWeapon: InventoryItem? = hero.getInventoryItem(InventoryGroup.WEAPON)
        val newWeapon: InventoryItem = selectedEquipment.inventoryItem

        if (newWeapon.durability == -1) {
            hero.clearInventoryItemFor(InventoryGroup.WEAPON)
            currentWeapon?.let { gameData.inventory.autoSetItem(it) }
            return "${character.name} unequipped the ${currentWeapon?.name}."
        }

        gameData.inventory.forceRemoveItem(newWeapon)
        hero.forceSetInventoryItemFor(InventoryGroup.WEAPON, newWeapon)
        currentWeapon?.let { gameData.inventory.autoSetItem(it) }
        return "${character.name} equipped the ${selectedEquipment.name}."
    }

    private fun handleShield(): String {
        currentParticipant.currentAP -= SWITCH_WEAPON_AP
        val currentShield: InventoryItem? = hero.getInventoryItem(InventoryGroup.SHIELD)
        val newShield: InventoryItem = selectedEquipment.inventoryItem

        if (newShield.durability == -1) {
            hero.clearInventoryItemFor(InventoryGroup.SHIELD)
            currentShield?.let { gameData.inventory.autoSetItem(it) }
            return "${character.name} unequipped the ${currentShield?.name}."
        }

        gameData.inventory.forceRemoveItem(newShield)
        hero.forceSetInventoryItemFor(InventoryGroup.SHIELD, newShield)
        currentShield?.let { gameData.inventory.autoSetItem(it) }
        return "${character.name} equipped the ${selectedEquipment.name}."
    }

    private fun createMessageIfCostingTooMuchAp(): String? {
        return when {
            currentParticipant.currentAP < SWITCH_WEAPON_AP -> "Not enough AP!"
            else -> null
        }
    }
}
