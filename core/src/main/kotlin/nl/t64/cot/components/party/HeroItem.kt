package nl.t64.cot.components.party

import nl.t64.cot.components.battle.Character
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
import nl.t64.cot.components.party.stats.StatContainer
import nl.t64.cot.components.party.stats.StatItem
import nl.t64.cot.components.party.stats.StatItemId
import nl.t64.cot.constants.Constant
import kotlin.math.roundToInt


class HeroItem(
    id: String = "",
    name: String = "",
    school: SchoolType = SchoolType.NONE,
    stats: StatContainer = StatContainer(),
    skills: SkillContainer = SkillContainer(),
    spells: SpellContainer = SpellContainer(),
    inventory: EquipContainer = EquipContainer(),
    isAlive: Boolean = true,
    var hasBeenRecruited: Boolean = false,
    private var isForVeryFirstSetup: Boolean = false
) : Character(
    id, name, school, stats, skills, spells, inventory, isAlive
) {
    val isPlayer: Boolean get() = id == Constant.PLAYER_ID
    var totalXp: Int = 0
    var xpPoints: Int = 0

    init {
        if (isForVeryFirstSetup) {
            isForVeryFirstSetup = false
            totalXp = stats.getTotalXpCost() + skills.getTotalXpCost()
            currentHp = maximumHp
            currentSp = maximumSp
        }
    }

    fun createCopy(
        id: String,
        name: String = this.name,
        school: SchoolType = this.school,
        stats: StatContainer = this.stats,
        skills: SkillContainer = this.skills,
        spells: SpellContainer = this.spells,
        inventory: EquipContainer = this.inventory,
        isAlive: Boolean = this.isAlive,
        hasBeenRecruited: Boolean = this.hasBeenRecruited
    ): HeroItem {
        return HeroItem(id, name, school, stats, skills, spells, inventory, isAlive, hasBeenRecruited, isForVeryFirstSetup = true)
    }

    fun hasSameIdAs(candidateHero: HeroItem): Boolean {
        return id == candidateHero.id
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun revive() {
        isAlive = true
        recoverFullHp()
        recoverFullSp()
    }

    fun gainXp(amount: Int) {
        xpPoints += amount
        totalXp += amount
    }

    fun hasEnoughXpFor(xpCost: Int): Boolean {
        return xpPoints >= xpCost
    }

    fun doUpgrade(statItem: StatItem, xpCost: Int) {
        xpPoints -= xpCost
        statItem.doUpgrade()
    }

    fun doUpgrade(skillItem: SkillItem, xpCost: Int) {
        xpPoints -= xpCost
        skillItem.doUpgrade()
        if (skillItem.rank == 1) {
            skills.add(skillItem)
        }
    }

    fun doUpgrade(spellItem: SpellItem, xpCost: Int) {
        xpPoints -= xpCost
        spellItem.doUpgrade()
        if (spellItem.rank == 1) {
            spells.add(spellItem)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun getStatById(statItemId: StatItemId): StatItem {
        return stats.getById(statItemId)
    }

    fun getSkillById(skillItemId: SkillItemId): SkillItem {
        return skills.getById(skillItemId)
    }

    fun getSpellById(spellId: String): SpellItem {
        return spells.getById(spellId)
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

    fun clearInventory() {
        inventory.clearAll()
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
        return StatItemId.entries
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
            inventoryItem.isTwoHanded -> createMessageIfShieldIsEquipped(inventoryItem)
            inventoryItem.isShield -> createMessageIfEquippedWeaponIsTwoHanded(inventoryItem)
            else -> null
        }
    }

    private fun createMessageIfShieldIsEquipped(twoHandedWeapon: InventoryItem): String? {
        return inventory.getInventoryItem(InventoryGroup.SHIELD)
            ?.let { shieldItem -> twoHandedWeapon.createMessageFailToEquipTwoHanded(shieldItem) }
    }

    private fun createMessageIfEquippedWeaponIsTwoHanded(shieldItem: InventoryItem): String? {
        return inventory.getInventoryItem(InventoryGroup.WEAPON)
            ?.takeIf { it.isTwoHanded }
            ?.let { twoHandedWeapon -> shieldItem.createMessageFailToEquipTwoHanded(twoHandedWeapon) }
    }

    private fun createMessageIfHeroHasNotEnoughFor(inventoryItem: InventoryItem): String? {
        return InventoryMinimal.entries
            .mapNotNull { it.createMessageIfHeroHasNotEnoughFor(inventoryItem, this) }
            .firstOrNull()
    }

    private fun createMessageIfHeroIsDead(): String? {
        return if (!isAlive) "$name is deceased." else null
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun getExtraStatForVisualOf(statItem: StatItem): Int {
        val extra = inventory.getSumOfStat(statItem.id) + statItem.bonus
        return if (extra < 0 && extra < -statItem.rank) -statItem.rank else extra
    }

    fun getExtraSkillForVisualOf(skillItem: SkillItem): Int {
        val extra = inventory.getSumOfSkill(skillItem.id) + skillItem.bonus
        return if (extra < 0 && extra < -skillItem.rank) -skillItem.rank else extra
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun getTransformation(): String {
        return when (getSumOfEquipmentOfCalc(CalcAttributeId.TRANSFORMATION)) {
            1 -> Constant.TRANSFORMATION_ORC
            else -> Constant.PLAYER_ID
        }
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

}
