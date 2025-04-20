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
    private val selectedEquipment: BattleWeaponItem,
    private val enemies: List<Participant>
) {
    private val character: Character = currentParticipant.character
    private val hero: HeroItem = character as HeroItem

    fun isUnableToEquip(): String? {
        return hero.createMessageIfNotAbleToEquip(selectedEquipment.inventoryItem)
            ?: createMessageIfCostingTooMuchAp()
    }

    fun createConfirmationMessage(): Triple<String, String, String> {
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

    private fun createConfirmationMessageWeapon(): Triple<String, String, String> {
        val currentWeapon: InventoryItem? = hero.getInventoryItem(InventoryGroup.WEAPON)
        val newWeapon: InventoryItem = selectedEquipment.inventoryItem

        if (newWeapon.durability == -1) {
            return currentWeapon!!.unequipWeapon()
        }
        return currentWeapon
            ?.switchWeaponTo(newWeapon)
            ?: newWeapon.equipWeapon()
    }

    private fun createConfirmationMessageShield(): Triple<String, String, String> {
        val currentShield: InventoryItem? = hero.getInventoryItem(InventoryGroup.SHIELD)
        val newShield: InventoryItem = selectedEquipment.inventoryItem

        if (newShield.durability == -1) {
            return currentShield!!.unequipShield()
        }

        return currentShield
            ?.switchShieldTo(newShield)
            ?: newShield.equipShield()
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

    private fun InventoryItem.unequipWeapon(): Triple<String, String, String> {
        return Triple("""
                ${this.listWeaponSpecs()}
                ______________

                Advantage:

                ${this.listEffectiveness()}
                ______________

                Unequip ($SWITCH_WEAPON_AP AP) ?
            """.trimIndent().trimMargin(), "", "")
    }

    private fun InventoryItem.switchWeaponTo(newWeapon: InventoryItem): Triple<String, String, String> {
        return Triple("""
                Current weapon:

                ${this.listWeaponSpecs()}
                __________________

                Advantage:

                ${this.listEffectiveness()}
            """.trimIndent().trimMargin(),
                      """
                New weapon:

                ${newWeapon.listWeaponSpecs()}
                ______________

                Advantage:

                ${newWeapon.listEffectiveness()}
            """.trimIndent().trimMargin(),
                      """
                ________________________________

                Equip ($SWITCH_WEAPON_AP AP) ?
            """.trimIndent())
    }

    private fun InventoryItem.equipWeapon(): Triple<String, String, String> {
        return Triple("""
                ${this.listWeaponSpecs()}
                ______________

                Advantage:

                ${this.listEffectiveness()}
                ______________

                   Equip ($SWITCH_WEAPON_AP AP) ?
            """.trimIndent().trimMargin(), "", "")
    }

    private fun InventoryItem.listWeaponSpecs(): String {
        return """
                ${this.name}
                Skill: ${this.skill?.title}
                ${this.getRangeText()}
                Durability: ${this.durability}
                Chance to hit: ${this.getAttributeOfCalcAttributeId(CalcAttributeId.BASE_HIT)}
                Damage: ${this.getAttributeOfCalcAttributeId(CalcAttributeId.DAMAGE)}
            """.trim()
    }

    private fun InventoryItem.listEffectiveness(): String {
        val result = enemies.joinToString(separator = "") { enemy ->
            val enemyWeapon = enemy.getCurrentWeapon()!!
            when {
                this.hasWeaponTriangleAdvantage(enemyWeapon) -> {
                    """[BLUE][v] ${enemy.character.name}[BLACK]
                       |"""
                }
                this.hasWeaponTriangleDisadvantage(enemyWeapon) -> {
                    """[FIREBRICK][x] ${enemy.character.name}[BLACK]
                       |"""
                }
                else -> {
                    """[o] ${enemy.character.name}
                       |"""
                }
            }
        }
        return result.replace(Regex("""[\n|\r]+$"""), "").trim()
    }

    private fun InventoryItem.unequipShield(): Triple<String, String, String> {
        return Triple("""
                ${this.listShieldSpecs()}
                ______________

                Unequip ($SWITCH_WEAPON_AP AP) ?
            """.trimIndent().trimMargin(), "", "")
    }

    private fun InventoryItem.switchShieldTo(newShield: InventoryItem): Triple<String, String, String> {
        return Triple("""
                Current shield:

                ${this.listShieldSpecs()}
            """.trimIndent().trimMargin(),
                      """
                New shield:

                ${newShield.listShieldSpecs()}
            """.trimIndent().trimMargin(),
                      """
                ________________________________

                Equip ($SWITCH_WEAPON_AP AP) ?
            """.trimIndent())
    }

    private fun InventoryItem.equipShield(): Triple<String, String, String> {
        return Triple("""
                ${this.listShieldSpecs()}
                ______________

                Equip ($SWITCH_WEAPON_AP AP) ?
            """.trimIndent().trimMargin(), "", "")
    }

    private fun InventoryItem.listShieldSpecs(): String {
        return """
                ${this.name}
                Durability: ${this.durability}
                Protection: ${this.getAttributeOfCalcAttributeId(CalcAttributeId.PROTECTION)}
                Defense: ${this.getAttributeOfCalcAttributeId(CalcAttributeId.DEFENSE)}
                Speed: ${this.getAttributeOfStatItemId(StatItemId.SPEED)}
                Stealth: ${this.getAttributeOfSkillItemId(SkillItemId.STEALTH)}
            """.trim()
    }

    private fun InventoryItem.hasWeaponTriangleAdvantage(enemyWeapon: InventoryItem): Boolean {
        return this.skill!!.hasAdvantageOver(enemyWeapon.skill)
    }

    private fun InventoryItem.hasWeaponTriangleDisadvantage(enemyWeapon: InventoryItem): Boolean {
        return this.skill!!.hasDisadvantageFrom(enemyWeapon.skill)
    }

}
