package nl.t64.cot.components.party

import nl.t64.cot.components.party.inventory.EquipContainer
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.inventory.InventoryMinimal
import nl.t64.cot.components.party.skills.SkillContainer
import nl.t64.cot.components.party.skills.SkillItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.spells.SchoolType
import nl.t64.cot.components.party.spells.SpellContainer
import nl.t64.cot.components.party.spells.SpellItem
import nl.t64.cot.components.party.stats.*
import nl.t64.cot.constants.Constant
import kotlin.math.roundToInt


class HeroItem(
    val name: String = "",
    val school: SchoolType = SchoolType.NONE,
    private val stats: StatContainer = StatContainer(),
    private val skills: SkillContainer = SkillContainer(),
    private val spells: SpellContainer = SpellContainer(),
    private val inventory: EquipContainer = EquipContainer()
) {

    lateinit var id: String
    var hasBeenRecruited = false
    val isPlayer: Boolean get() = id == Constant.PLAYER_ID
    var isAlive = true

    fun hasSameIdAs(candidateHero: HeroItem): Boolean {
        return id == candidateHero.id
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun takeDamage(damage: Int) {
        stats.takeDamage(damage)
        if (getCurrentHp() <= 0) {
            isAlive = false
        }
    }

    fun recoverFullHp() {
        stats.recoverFullHp()
    }

    fun recoverPartHp(healPoints: Int) {
        stats.recoverPartHp(healPoints)
    }

    fun recoverFullStamina() {
        stats.recoverFullStamina()
    }

    fun gainXp(amount: Int, levelUpMessage: StringBuilder) {
        stats.gainXp(amount) { gainLevel(levelUpMessage) }
    }

    fun hasEnoughXpFor(xpCost: Int): Boolean {
        return stats.hasEnoughXpFor(xpCost)
    }

    fun doUpgrade(statItem: StatItem, xpCost: Int) {
        stats.doUpgrade(statItem, xpCost)
    }

    val xpNeededForNextLevel: Int get() = stats.getXpNeededForNextLevel()
    val xpDeltaBetweenLevels: Int get() = stats.getXpDeltaBetweenLevels()
    val totalXp: Int get() = stats.totalXp
    val xpToInvest: Int get() = stats.xpToInvest
    fun getLevel(): Int = stats.getLevel()
    fun getAllHpStats(): Map<String, Int> = stats.getAllHpStats()
    fun getMaximumHp(): Int = stats.getMaximumHp()
    fun getCurrentHp(): Int = stats.getCurrentHp()
    fun getMaximumStamina(): Int = stats.getMaximumStamina()
    fun getCurrentStamina(): Int = stats.getCurrentStamina()

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun getStatById(statItemId: StatItemId): StatItem {
        return stats.getById(statItemId)
    }

    fun getSkillById(skillItemId: SkillItemId): SkillItem {
        return skills.getById(skillItemId)
    }

    fun getAllStats(): List<StatItem> = stats.getAll()
    fun getAllSkillsAboveZero(): List<SkillItem> = skills.getAllAboveZero()
    fun getAllSpells(): List<SpellItem> = spells.getAll()

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun getStatValueOf(inventoryGroup: InventoryGroup, statItemId: StatItemId): Int {
        return inventory.getStatValueOf(inventoryGroup, statItemId)
    }

    fun getSkillValueOf(inventoryGroup: InventoryGroup, skillItemId: SkillItemId): Int {
        return inventory.getSkillValueOf(inventoryGroup, skillItemId)
    }

    fun getCalcValueOf(inventoryGroup: InventoryGroup, calcAttributeId: CalcAttributeId): Int {
        return inventory.getCalcValueOf(inventoryGroup, calcAttributeId)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun hasInventoryItem(itemId: String): Boolean {
        return inventory.hasInventoryItem(itemId)
    }

    fun getInventoryItem(inventoryGroup: InventoryGroup): InventoryItem? {
        return inventory.getInventoryItem(inventoryGroup)
    }

    fun clearInventory() {
        inventory.clearAll()
    }

    fun clearInventoryItemFor(inventoryGroup: InventoryGroup) {
        inventory.clearInventoryItem(inventoryGroup)
    }

    fun forceSetInventoryItemFor(inventoryGroup: InventoryGroup, inventoryItem: InventoryItem) {
        inventory.forceSetInventoryItem(inventoryGroup, inventoryItem)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun createMessageIfNotAbleToEquip(inventoryItem: InventoryItem): String? {
        return createMessageIfHeroIsDead()
            ?: createMessageIfHeroHasNotEnoughFor(inventoryItem)
            ?: createMessageIfWeaponAndShieldAreNotCompatible(inventoryItem)
            ?: createMessageIfNotAbleToDequip(getInventoryItem(inventoryItem.group)
                                                  ?: InventoryItem())   // does nothing
    }

    fun createMessageIfNotAbleToDequip(enhancerItem: InventoryItem): String? {
        return StatItemId.values()
            .filter { enhancerItem.getAttributeOfStatItemId(it) > 0 }
            .flatMap { createMessageIfNotAbleToDequip(enhancerItem, it) }
            .firstOrNull()
    }

    private fun createMessageIfNotAbleToDequip(enhancerItem: InventoryItem, statItemId: StatItemId): List<String> {
        return inventory.getItemsWithMinimalOf(statItemId)
            .filter { isMinimalToHigh(statItemId, it, enhancerItem) }
            .map { enhancerItem.createMessageFailToDequip(it) }
    }

    private fun isMinimalToHigh(statItemId: StatItemId, dependantItem: InventoryItem, enhancerItem: InventoryItem
    ): Boolean {
        return dependantItem.getMinimalAttributeOfStatItemId(statItemId) >
                getCalculatedTotalStatOf(statItemId) - enhancerItem.getAttributeOfStatItemId(statItemId)
    }

    private fun createMessageIfWeaponAndShieldAreNotCompatible(inventoryItem: InventoryItem): String? {
        return when {
            inventoryItem.isTwoHanded ->
                inventory.getInventoryItem(InventoryGroup.SHIELD)?.let {
                    inventoryItem.createMessageFailToEquipTwoHanded(it)
                }
            inventoryItem.group == InventoryGroup.SHIELD ->
                inventory.getInventoryItem(InventoryGroup.WEAPON)
                    ?.takeIf { it.isTwoHanded }
                    ?.let { inventoryItem.createMessageFailToEquipTwoHanded(it) }
            else -> null
        }
    }

    fun createMessageIfHeroHasNotEnoughFor(inventoryItem: InventoryItem): String? {
        return InventoryMinimal.values()
            .mapNotNull { it.createMessageIfHeroHasNotEnoughFor(inventoryItem, this) }
            .firstOrNull()
    }

    private fun createMessageIfHeroIsDead(): String? {
        return if (!isAlive) "$name is deceased." else null
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun getExtraStatForVisualOf(statItem: StatItem): Int {
        return when (statItem) {
            is Endurance,
            is Stamina -> statItem.variable - statItem.rank
            else -> {
                val extra = inventory.getSumOfStat(statItem.id) + statItem.bonus
                if (extra < 0 && extra < -statItem.rank) -statItem.rank else extra
            }
        }
    }

    fun getExtraSkillForVisualOf(skillItem: SkillItem): Int {
        val extra = inventory.getSumOfSkill(skillItem.id) + skillItem.bonus
        return if (extra < 0 && extra < -skillItem.rank) -skillItem.rank else extra
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

    fun getTransformation(): String {
        return when (getTotalCalcOf(CalcAttributeId.TRANSFORMATION)) {
            1 -> Constant.TRANSFORMATION_ORC
            else -> Constant.PLAYER_ID
        }
    }

    fun getTotalCalcOf(calcAttributeId: CalcAttributeId): Int {
        // todo, er moet nog wel een bonus komen voor protection en etc. bijv met een protection spell.
        // of hieronder
        return inventory.getSumOfCalc(calcAttributeId)
    }

    fun getPossibleExtraProtection(): Int {
        // todo, er moet nog wel een bonus komen voor protection en etc. bijv met een protection spell.
        // of hierboven
        return inventory.getBonusProtectionWhenArmorSetIsComplete()
    }

    fun getCalculatedMovepoints(): Int {
        return (10f
                + stats.getById(StatItemId.STAMINA).variable / 10f
                - getTotalCalcOf(CalcAttributeId.WEIGHT) / 3f)
            // todo:
            //  + bonus movepoints van equipment
            .roundToInt()
            .takeIf { it > 0f } ?: 1
    }

    fun getCalculatedTotalDamage(): Int {
        // todo, is nu alleen nog maar voor hand to hand wapens.
        return inventory.getWeaponSkill()
            ?.takeIf { it.isHandToHandWeaponSkill() }
            ?.let { getCalculatedTotalDamage(it) }
            ?: 0

    }

    private fun getCalculatedTotalDamage(weaponSkill: SkillItemId): Int {
        return (getTotalCalcOf(CalcAttributeId.DAMAGE)
                // + getLevel() todo, this one shouldn't be shown in calculation but should be calculated in battle.
                + getCalculatedTotalStatOf(StatItemId.STRENGTH) / stats.getInflictDamageStaminaPenalty()
                + getCalculatedTotalSkillOf(SkillItemId.WARRIOR)
                + getCalculatedTotalSkillOf(weaponSkill) * 2)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun gainLevel(levelUpMessage: StringBuilder) {
        recoverFullHp()
        levelUpMessage.append("$name gained a level!").append(System.lineSeparator())
    }

}
