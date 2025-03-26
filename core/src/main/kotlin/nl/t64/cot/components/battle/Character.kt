package nl.t64.cot.components.battle

import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.abilities.AbilityContainer
import nl.t64.cot.components.party.abilities.AbilityItem
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
    val gender: String = "",
    val school: SchoolType = SchoolType.NONE,
    protected val stats: StatContainer = StatContainer(),
    protected val skills: SkillContainer = SkillContainer(),
    protected val abilities: AbilityContainer = AbilityContainer(),
    protected val spells: SpellContainer = SpellContainer(),
    protected val inventory: EquipContainer = EquipContainer(),
    var isAlive: Boolean = true
) {
    val isDead: Boolean get() = !isAlive
    open val maximumHp: Int get() = stats.maximumHp
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

    fun getAllAbilities(): List<AbilityItem> {
        return abilities.getAll()
    }

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

    open fun getCalculatedActionPoints(): Int {
        return ((getCalculatedTotalStatOf(StatItemId.INTELLIGENCE)
            + getCalculatedTotalStatOf(StatItemId.DEXTERITY)
            + getCalculatedTotalStatOf(StatItemId.STRENGTH)
            + getCalculatedTotalStatOf(StatItemId.SPEED)) / 10f)
            .roundToInt()
            .takeIf { it > 0f } ?: 1
    }

    fun getCalculatedTotalHit(): Int {
        // todo, is nu alleen nog maar voor wapens, niet voor potions. en ook niet voor ranged in de battle zelf.
        return inventory.getSkillOfCurrentWeapon()?.toCalculatedTotalHit() ?: 0
    }

    fun getCalculatedTotalDamage(): Int {
        // todo, is nu alleen nog maar voor wapens, niet voor potions.
        val currentWeaponSkill: SkillItemId? = inventory.getSkillOfCurrentWeapon()
        return when {
            currentWeaponSkill == null -> 0
            currentWeaponSkill.isHandToHandWeaponSkill() -> getCalculatedTotalDamageClose()
            else -> getCalculatedTotalDamageRange()
        }
    }

    fun getCalculatedTotalProtection(): Int {
        return getSumOfEquipmentOfCalc(CalcAttributeId.PROTECTION) + getPossibleExtraProtection()
    }

    fun getCalculatedTotalDefense(): Int {
        return when {
            inventory.getInventoryItem(InventoryGroup.SHIELD) == null -> 0
            else -> {
                val shieldDefense: Int = getSumOfEquipmentOfCalc(CalcAttributeId.DEFENSE)
                val shieldSkillAmount: Int = getCalculatedTotalSkillOf(SkillItemId.SHIELD)
                val defenderDefense: Float = (shieldDefense / 100f) * (10f * shieldSkillAmount)
                return (shieldDefense + defenderDefense).roundToInt()
            }
        }
    }

    private fun SkillItemId.toCalculatedTotalHit(): Int {
        val weaponSkill: SkillItemId = this
        val weaponHit: Int = getSumOfEquipmentOfCalc(CalcAttributeId.BASE_HIT)
        val weaponSkillAmount: Int = getCalculatedTotalSkillOf(weaponSkill)
        val attackerHit: Float = (weaponHit / 100f) * (5f * weaponSkillAmount)
        // + troubadour ?
        // + backAttack Thief bonus hit ?
        // + gambler todo, overal
        return (weaponHit + attackerHit).roundToInt()
    }

    private fun getCalculatedTotalDamageClose(): Int {
        val currentWeaponMinimal: StatItemId = inventory.getStatItemIdOfMinimalOfCurrentWeapon()!!
        val totalDamageOfAllEquipment: Int = getSumOfEquipmentOfCalc(CalcAttributeId.DAMAGE)
        val attributeAmount: Int = getCalculatedTotalStatOf(currentWeaponMinimal)
        val attackerDamage: Float = (totalDamageOfAllEquipment / 100f) * (5f * attributeAmount)
        // todo, + backAttack Thief bonus damage ?
        return (totalDamageOfAllEquipment + attackerDamage).roundToInt()
    }

    private fun getCalculatedTotalDamageRange(): Int {
        val currentWeaponMinimal: StatItemId = inventory.getStatItemIdOfMinimalOfCurrentWeapon()!!
        // todo, moet wel voor ranged sum of all equipment zijn?
        val totalDamageOfAllEquipment: Int = getSumOfEquipmentOfCalc(CalcAttributeId.DAMAGE)
        val attributeAmount: Int = getCalculatedTotalStatOf(currentWeaponMinimal)
        val attackerDamage: Float = (totalDamageOfAllEquipment / 100f) * (5f * attributeAmount)
        return (totalDamageOfAllEquipment + attackerDamage).roundToInt()
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
