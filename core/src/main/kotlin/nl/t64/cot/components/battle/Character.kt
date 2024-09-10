package nl.t64.cot.components.battle

import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.inventory.EquipContainer
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.skills.SkillContainer
import nl.t64.cot.components.party.skills.SkillItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.spells.SchoolType
import nl.t64.cot.components.party.spells.SpellContainer
import nl.t64.cot.components.party.stats.StatContainer
import nl.t64.cot.components.party.stats.StatItem
import nl.t64.cot.components.party.stats.StatItemId
import kotlin.math.roundToInt


abstract class Character(
    val id: String = "",
    val name: String = "",
    val school: SchoolType = SchoolType.NONE,
    protected val stats: StatContainer = StatContainer(),
    protected val skills: SkillContainer = SkillContainer(),
    protected val spells: SpellContainer = SpellContainer(),
    protected val inventory: EquipContainer = EquipContainer(),
    var isAlive: Boolean = true
) {
    val maximumHp: Int get() = stats.maximumHp
    var currentHp: Int = 0
    val maximumSp: Int get() = stats.maximumSp
    var currentSp: Int = 0

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // todo, speciale bonus toepassen inventoryItem: epic_ring_of_healing, die 1 hp geeft om je leven 1malig te redden.
    fun takeDamage(damage: Int) {
        currentHp = (currentHp - damage).coerceAtLeast(0)
        if (currentHp <= 0) {
            currentSp = 0
            isAlive = false
        }
    }

    fun recoverFullHp() {
        currentHp = maximumHp
    }

    fun recoverPartHp(healPoints: Int) {
        currentHp = (currentHp + healPoints).coerceAtMost(maximumHp)
    }

    fun recoverFullSp() {
        currentSp = maximumSp
    }

    fun recoverPartSp(recoverPoints: Int) {
        currentSp = (currentSp + recoverPoints).coerceAtMost(maximumSp)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun getInventoryItem(inventoryGroup: InventoryGroup): InventoryItem? {
        return inventory.getInventoryItem(inventoryGroup)
    }

    fun clearInventoryItemFor(inventoryGroup: InventoryGroup) {
        inventory.clearInventoryItem(inventoryGroup)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun getCalculatedTotalStatOf(statItemId: StatItemId): Int {
        val statItem = stats.getById(statItemId)
        return getRealTotalStatOf(statItem).takeIf { it > 0 } ?: 1
    }

    fun getCalculatedTotalSkillOf(skillItemId: SkillItemId): Int {
        val skillItem = skills.getById(skillItemId)
        if (skillItem.rank <= 0) return 0
        return getRealTotalSkillOf(skillItem).takeIf { it > 0 } ?: 0
    }

    private fun getRealTotalStatOf(statItem: StatItem): Int {
        return statItem.rank + inventory.getSumOfStat(statItem.id) + statItem.bonus
    }

    private fun getRealTotalSkillOf(skillItem: SkillItem): Int {
        return skillItem.rank + inventory.getSumOfSkill(skillItem.id) + skillItem.bonus
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun getCalculatedActionPoints(): Int {
        return ((getCalculatedTotalStatOf(StatItemId.INTELLIGENCE)
            + getCalculatedTotalStatOf(StatItemId.DEXTERITY)
            + getCalculatedTotalStatOf(StatItemId.STRENGTH)
            + getCalculatedTotalStatOf(StatItemId.SPEED)) / 10f
            // een step is 1 AP, een attack is 3 AP?
            // loskomen van een close attack is 2-3? AP, wapen wisselen is 3 AP?
            ).roundToInt()
            .takeIf { it > 0f } ?: 1
    }

    fun getCalculatedTotalHit(): Int {
        // todo, is nu alleen nog maar voor wapens, niet voor potions. en ook niet voor ranged in de battle zelf.
        return inventory.getSkillOfCurrentWeapon()
            ?.let { getCalculatedTotalHit(it) }
            ?: 0
    }

    fun getCalculatedTotalDamage(): Int {
        // todo, is nu alleen nog maar voor wapens, niet voor potions.
        val currentWeaponSkill: SkillItemId? = inventory.getSkillOfCurrentWeapon()
        val currentWeaponMinimal: StatItemId? = inventory.getStatItemIdOfMinimalOfCurrentWeapon()
        return when {
            currentWeaponSkill == null -> 0
            currentWeaponSkill.isHandToHandWeaponSkill() -> getCalculatedTotalDamageClose(currentWeaponMinimal!!)
            else -> getCalculatedTotalDamageRange()
        }
    }

    fun getCalculatedTotalProtection(): Int {
        return getSumOfEquipmentOfCalc(CalcAttributeId.PROTECTION) + getPossibleExtraProtection()
    }

    private fun getCalculatedTotalHit(weaponSkill: SkillItemId): Int {
        val weaponHit: Int = getSumOfEquipmentOfCalc(CalcAttributeId.BASE_HIT)
        val weaponSkillAmount: Int = getCalculatedTotalSkillOf(weaponSkill)
        val attackerHit: Float = (weaponHit / 100f) * (5f * weaponSkillAmount)
        // + troubadour ?
        // + backAttack Thief bonus hit ?
        // + gambler todo, overal
        return (weaponHit + attackerHit).roundToInt()
    }

    private fun getCalculatedTotalDamageClose(minimalType: StatItemId): Int {
        val totalDamageOfAllEquipment: Int = getSumOfEquipmentOfCalc(CalcAttributeId.DAMAGE)
        val intelligenceOrStrengthAmount: Int = getCalculatedTotalStatOf(minimalType)
        val attackerDamage: Float = (totalDamageOfAllEquipment / 100f) * (5f * intelligenceOrStrengthAmount)
        // + backAttack Thief bonus damage ?
        return (totalDamageOfAllEquipment + attackerDamage).roundToInt()
    }

    private fun getCalculatedTotalDamageRange(): Int {
        val weaponDamage: Int = getSumOfEquipmentOfCalc(CalcAttributeId.DAMAGE)
        val dexterityAmount: Int = getCalculatedTotalStatOf(StatItemId.DEXTERITY)
        val attackerDamage: Float = (weaponDamage / 100f) * (5f * dexterityAmount)
        return (weaponDamage + attackerDamage).roundToInt()
    }

    fun getSumOfEquipmentOfCalc(calcAttributeId: CalcAttributeId): Int {
        // todo, er moet nog wel een bonus komen voor protection en etc. bijv met een protection spell.
        // of hieronder
        return inventory.getSumOfCalc(calcAttributeId)
    }

    fun getPossibleExtraProtection(): Int {
        // todo, er moet nog wel een bonus komen voor protection en etc. bijv met een protection spell.
        // of hierboven
        return inventory.getBonusProtectionWhenArmorSetIsComplete()
    }

}
