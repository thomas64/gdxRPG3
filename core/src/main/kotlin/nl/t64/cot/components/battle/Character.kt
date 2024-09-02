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
        val currentWeaponSkill = inventory.getSkillOfCurrentWeapon()
        val currentWeaponMinimal = inventory.getStatItemIdOfMinimalOfCurrentWeapon()
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
        val weaponHit = getSumOfEquipmentOfCalc(CalcAttributeId.BASE_HIT)
        val wielderSkill = getCalculatedTotalSkillOf(weaponSkill)
        val staminaPenalty = getPossibleChanceToHitPenalty()
        val formula = weaponHit + ((weaponHit / 100f) * (10f * wielderSkill)) - staminaPenalty
        return (formula
            // + troubadour
            // + back attack + thief bonus
            // - movement penalty voor ranged
            // - weight penalty voor ranged
            // - distance penalty voor ranged
            // - obstacle penalty voor ranged
            // + gambler todo, overal
            // + getLevel() todo, this one shouldn't be shown in calculation but should be calculated in battle.
            ).roundToInt()
    }

    private fun getCalculatedTotalDamageClose(minimalType: StatItemId): Int {
        val totalDamageOfAllEquipment = getSumOfEquipmentOfCalc(CalcAttributeId.DAMAGE)
        val staminaPenalty = getPossibleInflictDamagePenalty()
        val statOfWielder = getCalculatedTotalStatOf(minimalType) / staminaPenalty
        val formula = totalDamageOfAllEquipment + ((totalDamageOfAllEquipment / 100f) * (5f * statOfWielder))
        return (formula
            // + back thief
            // + getLevel() todo, this one shouldn't be shown in calculation but should be calculated in battle.
            // + crit warrior
            ).roundToInt()
    }

    private fun getCalculatedTotalDamageRange(): Int {
        val weaponDamage = getSumOfEquipmentOfCalc(CalcAttributeId.DAMAGE)
        val staminaPenalty = getPossibleInflictDamagePenalty()
        val wielderDexterity = getCalculatedTotalStatOf(StatItemId.DEXTERITY) / staminaPenalty
        val formula = weaponDamage + ((weaponDamage / 100f) * (5f * wielderDexterity))
        return (formula
            // + getLevel() todo, this one shouldn't be shown in calculation but should be calculated in battle.
            // + crit warrior
            ).roundToInt()
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

    private fun getPossibleInflictDamagePenalty(): Int = if (currentSp <= 0) 5 else 1
    private fun getPossibleChanceToHitPenalty(): Int = if (currentSp <= 0) 25 else 0

}
