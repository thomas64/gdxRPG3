package nl.t64.cot.components.battle

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.inventory.BattleWeaponItem
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.stats.StatItemId
import nl.t64.cot.removeColorCoding


const val SWITCH_WEAPON_AP: Int = 3

class WeaponAction(
    private val currentParticipant: Participant,
    private val selectedEquipment: BattleWeaponItem,
    private val enemies: List<Participant>,
    private val switchWeaponAp: Int = SWITCH_WEAPON_AP
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

    fun handle() {
        return when (selectedEquipment.group) {
            InventoryGroup.WEAPON -> handleWeapon()
            InventoryGroup.SHIELD -> handleShield()
            else -> throw IllegalArgumentException("Invalid inventory group: ${selectedEquipment.group}")
        }
    }

    private fun createConfirmationMessageWeapon(): Triple<String, String, String> {
        val currentWeapon: InventoryItem? = hero.getInventoryItem(InventoryGroup.WEAPON)
        val newWeapon: InventoryItem = selectedEquipment.inventoryItem

        if (newWeapon.name.contains("Unequip")) {
            return currentWeapon!!.unequipWeapon()
        }
        return currentWeapon
            ?.switchWeaponTo(newWeapon)
            ?: newWeapon.equipWeapon()
    }

    private fun createConfirmationMessageShield(): Triple<String, String, String> {
        val currentShield: InventoryItem? = hero.getInventoryItem(InventoryGroup.SHIELD)
        val newShield: InventoryItem = selectedEquipment.inventoryItem

        if (newShield.name.contains("Unequip")) {
            return currentShield!!.unequipShield()
        }

        return currentShield
            ?.switchShieldTo(newShield)
            ?: newShield.equipShield()
    }

    private fun handleWeapon() {
        currentParticipant.currentAP -= switchWeaponAp
        val currentWeapon: InventoryItem? = hero.getInventoryItem(InventoryGroup.WEAPON)
        val newWeapon: InventoryItem = selectedEquipment.inventoryItem

        if (newWeapon.name.contains("Unequip")) {
            unequip(InventoryGroup.WEAPON, currentWeapon)
        } else {
            equip(InventoryGroup.WEAPON, newWeapon, currentWeapon)
        }
    }

    private fun handleShield() {
        currentParticipant.currentAP -= switchWeaponAp
        val currentShield: InventoryItem? = hero.getInventoryItem(InventoryGroup.SHIELD)
        val newShield: InventoryItem = selectedEquipment.inventoryItem

        if (newShield.name.contains("Unequip")) {
            unequip(InventoryGroup.SHIELD, currentShield)
        } else {
            equip(InventoryGroup.SHIELD, newShield, currentShield)
        }
    }

    private fun unequip(group: InventoryGroup, currentItem: InventoryItem?) {
        hero.clearInventoryItemFor(group)
        currentItem?.let { gameData.inventory.autoSetItem(it) }
    }

    private fun equip(group: InventoryGroup, itemToEquip: InventoryItem, currentItem: InventoryItem?) {
        gameData.inventory.forceRemoveItem(itemToEquip)
        hero.forceSetInventoryItemFor(group, itemToEquip)
        currentItem?.let { gameData.inventory.autoSetItem(it) }
    }

    private fun createMessageIfCostingTooMuchAp(): String? {
        return when {
            currentParticipant.currentAP < switchWeaponAp -> "Not enough AP!"
            else -> null
        }
    }

    private fun InventoryItem.unequipWeapon(): Triple<String, String, String> {
        val weaponSpecs: String = this.listWeaponSpecs()
        val effectiveness: String = this.listEffectiveness()
        val underscores: String = createUnderscoresWithLengthOf(weaponSpecs, effectiveness)

        return Triple("""
                $weaponSpecs
                $underscores

                [BLUE]+[BLACK] Advantage/
                [FIREBRICK]-[BLACK] Disadvantage:

                $effectiveness
            """.trimIndent().trimMargin(marginPrefix = ">"),
                      "",
                      """

                $underscores

                Unequip?${getApCostText()}
            """.trimIndent())
    }

    private fun InventoryItem.switchWeaponTo(newWeapon: InventoryItem): Triple<String, String, String> {
        val currentWeaponSpecs: String = this.listWeaponSpecs()
        val currentEffectiveness: String = this.listEffectiveness()
        val currentUnderscores: String = createUnderscoresWithLengthOf(currentWeaponSpecs, currentEffectiveness)
        val newWeaponSpecs: String = newWeapon.listWeaponSpecs("|  ")
        val newEffectiveness: String = newWeapon.listEffectiveness("|  ")
        val newUnderscores: String = createUnderscoresWithLengthOf(newWeaponSpecs, newEffectiveness)

        return Triple("""
                Current weapon:

                $currentWeaponSpecs
                ${currentUnderscores}__

                $currentEffectiveness
                ${currentUnderscores}__
            """.trimIndent().trimMargin(marginPrefix = ">"),
                      """
                |  New weapon:
                |
                $newWeaponSpecs
                |__${newUnderscores.dropLast(2)}
                |
                $newEffectiveness
                |__${newUnderscores.dropLast(2)}
            """.trimIndent().trimMargin(marginPrefix = ">"),
                      """

                Equip?${getApCostText()}
            """.trimIndent())
    }

    private fun InventoryItem.equipWeapon(): Triple<String, String, String> {
        val weaponSpecs: String = this.listWeaponSpecs()
        val effectiveness: String = this.listEffectiveness()
        val underscores: String = createUnderscoresWithLengthOf(weaponSpecs, effectiveness)

        return Triple("""
                $weaponSpecs
                $underscores

                [BLUE]+[BLACK] Advantage/
                [FIREBRICK]-[BLACK] Disadvantage:

                $effectiveness
            """.trimIndent().trimMargin(marginPrefix = ">"),
                      "",
                      """

                $underscores

                Equip?${getApCostText()}
            """.trimIndent())
    }

    private fun InventoryItem.listWeaponSpecs(prefix: String = ""): String {
        return """
                ${prefix}${this.name} ${this.getDurabilityText()}
                ${prefix}Range:    ${String.format("%3s", this.getRangeText())}
                ${prefix}Base hit: ${String.format("%3d", this.getAttributeOfCalcAttributeId(CalcAttributeId.BASE_HIT))} %
                ${prefix}Damage:   ${String.format("%3d", this.getAttributeOfCalcAttributeId(CalcAttributeId.DAMAGE))}
            """.trim()
    }

    private fun InventoryItem.listEffectiveness(prefix: String = ""): String {
        if (enemies.size == 1 &&
            enemies.map { it.getCurrentWeapon()!! }
                .none { this.hasWeaponTriangleAdvantage(it) || this.hasWeaponTriangleDisadvantage(it) }
        ) return "${prefix}[GRAY]None[BLACK]"

        val result = enemies.joinToString(separator = "") { enemy ->
            val enemyWeapon = enemy.getCurrentWeapon()!!
            when {
                this.hasWeaponTriangleAdvantage(enemyWeapon) -> {
                    """${prefix}[BLUE]+ ${enemy.character.name}[BLACK]
                       >"""
                }
                this.hasWeaponTriangleDisadvantage(enemyWeapon) -> {
                    """${prefix}[FIREBRICK]- ${enemy.character.name}[BLACK]
                       >"""
                }
                else -> {
                    """${prefix}[GRAY]o ${enemy.character.name}[BLACK]
                       >"""
                }
            }
        }
        return result.replace(Regex("""[\n>\r]+$"""), "").trim()
    }

    private fun InventoryItem.unequipShield(): Triple<String, String, String> {
        val shieldSpecs: String = this.listShieldSpecs()
        val underscores: String = createUnderscoresWithLengthOf(shieldSpecs)

        return Triple("""
                $shieldSpecs
            """.trimIndent().trimMargin(),
                      "",
                      """

                $underscores

                Unequip?${getApCostText()}
            """.trimIndent())
    }

    private fun InventoryItem.switchShieldTo(newShield: InventoryItem): Triple<String, String, String> {
        val currentShieldSpecs: String = this.listShieldSpecs()
        val currentUnderscores: String = createUnderscoresWithLengthOf(currentShieldSpecs)
        val newShieldSpecs: String = newShield.listShieldSpecs("|  ")
        val newUnderscores: String = createUnderscoresWithLengthOf(newShieldSpecs)

        return Triple("""
                Current shield:

                $currentShieldSpecs
                ${currentUnderscores}__
            """.trimIndent().trimMargin(),
                      """
                |  New shield:
                |
                $newShieldSpecs
                |__${newUnderscores.dropLast(2)}
            """.trimIndent().trimMargin(marginPrefix = ";"),
                      """

                Equip?${getApCostText()}
            """.trimIndent())
    }

    private fun InventoryItem.equipShield(): Triple<String, String, String> {
        val shieldSpecs: String = this.listShieldSpecs()
        val underscores: String = createUnderscoresWithLengthOf(shieldSpecs)

        return Triple("""
                $shieldSpecs
            """.trimIndent().trimMargin(),
                      "",
                      """

                $underscores

                Equip?${getApCostText()}
            """.trimIndent())
    }

    private fun InventoryItem.listShieldSpecs(prefix: String = ""): String {
        return """
                ${prefix}${this.name} ${this.getDurabilityText()}
                ${prefix}Protection: ${String.format("%3d", this.getAttributeOfCalcAttributeId(CalcAttributeId.PROTECTION))}
                ${prefix}Defense:    ${String.format("%3d", this.getAttributeOfCalcAttributeId(CalcAttributeId.DEFENSE))}
                ${prefix}Speed:      ${String.format("%3d", this.getAttributeOfStatItemId(StatItemId.SPEED))}
                ${prefix}Stealth:    ${String.format("%3d", this.getAttributeOfSkillItemId(SkillItemId.STEALTH))}
            """.trim()
    }

    private fun getApCostText(): String {
        return if (switchWeaponAp == 0) "" else " ($SWITCH_WEAPON_AP AP)"
    }

    private fun InventoryItem.hasWeaponTriangleAdvantage(enemyWeapon: InventoryItem): Boolean {
        return this.skill!!.hasAdvantageOver(enemyWeapon.skill)
    }

    private fun InventoryItem.hasWeaponTriangleDisadvantage(enemyWeapon: InventoryItem): Boolean {
        return this.skill!!.hasDisadvantageFrom(enemyWeapon.skill)
    }

    private fun createUnderscoresWithLengthOf(vararg allLines: String): String {
        val minLength = 16
        val maxLength = allLines.getLongestLineLength()
        return "_".repeat(maxOf(minLength, maxLength))
    }

    private fun Array<out String>.getLongestLineLength(): Int {
        return this.flatMap { it.lines() }
            .map { it.removeColorCoding().trim() }
            .maxOf { it.length }
    }

}
