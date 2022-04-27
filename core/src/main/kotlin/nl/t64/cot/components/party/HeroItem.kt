package nl.t64.cot.components.party

import nl.t64.cot.Utils.gameData
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


data class HeroItem(
    val id: String = "",
    val name: String = "",
    val school: SchoolType = SchoolType.NONE,
    private val stats: StatContainer = StatContainer(),
    private val skills: SkillContainer = SkillContainer(),
    private val spells: SpellContainer = SpellContainer(),
    private val inventory: EquipContainer = EquipContainer()
) {

    var isAlive = true
    var hasBeenRecruited = false
    val isPlayer: Boolean get() = id == Constant.PLAYER_ID

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

    fun revive() {
        isAlive = true
        recoverFullHp()
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
        stats.gainXp(amount) { gainLevel(levelUpMessage, it) }
    }

    fun hasEnoughXpFor(xpCost: Int): Boolean {
        return stats.hasEnoughXpFor(xpCost)
    }

    fun doUpgrade(statItem: StatItem, xpCost: Int) {
        stats.takeXpToInvest(xpCost)
        statItem.doUpgrade()
    }

    fun doUpgrade(skillItem: SkillItem, xpCost: Int, goldCost: Int) {
        stats.takeXpToInvest(xpCost)
        gameData.inventory.autoRemoveItem("gold", goldCost)
        skillItem.doUpgrade()
        if (skillItem.rank == 1) {
            skills.add(skillItem)
        }
    }

    fun doUpgrade(spellItem: SpellItem, xpCost: Int, goldCost: Int) {
        stats.takeXpToInvest(xpCost)
        gameData.inventory.autoRemoveItem("gold", goldCost)
        spellItem.doUpgrade()
        if (spellItem.rank == 1) {
            spells.add(spellItem)
        }
    }

    val xpNeededForNextLevel: Int get() = stats.getXpNeededForNextLevel()
    val xpDeltaBetweenLevels: Int get() = stats.getXpDeltaBetweenLevels()
    val totalXp: Int get() = stats.totalXp
    val xpToInvest: Int get() = stats.xpToInvest
    fun getLevel(): Int = stats.levelRank
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
            inventoryItem.isTwoHanded -> createMessageIfShieldIsEquipped(inventoryItem)
            inventoryItem.isShield -> createMessageIfEquippedWeaponIsTwoHanded(inventoryItem)
            else -> null
        }
    }

    private fun createMessageIfShieldIsEquipped(twoHandedWeapon: InventoryItem): String? {
        return inventory.getInventoryItem(InventoryGroup.SHIELD)?.let { shieldItem ->
            twoHandedWeapon.createMessageFailToEquipTwoHanded(shieldItem)
        }
    }

    private fun createMessageIfEquippedWeaponIsTwoHanded(shieldItem: InventoryItem): String? {
        return inventory.getInventoryItem(InventoryGroup.WEAPON)
            ?.takeIf { it.isTwoHanded }
            ?.let { twoHandedWeapon -> shieldItem.createMessageFailToEquipTwoHanded(twoHandedWeapon) }
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
        return when (statItem.id) {
            StatItemId.CONSTITUTION,
            StatItemId.STAMINA -> statItem.variable - statItem.rank
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
        return when (getSumOfEquipmentOfCalc(CalcAttributeId.TRANSFORMATION)) {
            1 -> Constant.TRANSFORMATION_ORC
            else -> Constant.PLAYER_ID
        }
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

    fun getCalculatedActionPoints(): Int {
        return ((getCalculatedTotalStatOf(StatItemId.SPEED) / 3f)
                + ((getCalculatedTotalStatOf(StatItemId.INTELLIGENCE)
                + getCalculatedTotalStatOf(StatItemId.STRENGTH)
                + getCalculatedTotalStatOf(StatItemId.DEXTERITY)) / 6f)
                // een step is 1 AP, een attack is 5 AP?
                // loskomen van een close attack is x AP, wapen wisselen is x AP.
                ).roundToInt()
            .takeIf { it > 0f } ?: 1
    }

    fun getCalculatedTotalHit(): Int {
        // todo, is nu alleen nog maar voor wapens, niet voor potions. en ook niet voor ranged in de battle zelf.
        return inventory.getSkillOfCurrentWeapon()?.let {
            getCalculatedTotalHit(it)
        } ?: 0
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

    fun getCalculatedTotalDefense(): Int {
        return when {
            inventory.getInventoryItem(InventoryGroup.SHIELD) == null -> 0
            else -> {
                val shieldDefense = getSumOfEquipmentOfCalc(CalcAttributeId.DEFENSE)
                val wielderSkill = getCalculatedTotalSkillOf(SkillItemId.SHIELD)
                val staminaPenalty = stats.getDefenseStaminaPenalty()
                val formula = shieldDefense + ((shieldDefense / 100f) * (10f * wielderSkill)) - staminaPenalty
                // + getLevel() todo, this one shouldn't be shown in calculation but should be calculated in battle.
                formula.roundToInt()
            }
        }
    }

    private fun getCalculatedTotalHit(weaponSkill: SkillItemId): Int {
        val weaponHit = getSumOfEquipmentOfCalc(CalcAttributeId.BASE_HIT)
        val wielderSkill = getCalculatedTotalSkillOf(weaponSkill)
        val staminaPenalty = stats.getChanceToHitStaminaPenalty()
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
        val staminaPenalty = stats.getInflictDamageStaminaPenalty()
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
        val staminaPenalty = stats.getInflictDamageStaminaPenalty()
        val wielderDexterity = getCalculatedTotalStatOf(StatItemId.DEXTERITY) / staminaPenalty
        val formula = weaponDamage + ((weaponDamage / 100f) * (5f * wielderDexterity))
        return (formula
                // + getLevel() todo, this one shouldn't be shown in calculation but should be calculated in battle.
                // + crit warrior
                ).roundToInt()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun gainLevel(levelUpMessage: StringBuilder, amountOfLevels: Int) {
        recoverFullHp()
        if (amountOfLevels == 1) {
            levelUpMessage.append("$name gained a level!").append(System.lineSeparator())
        } else {
            levelUpMessage.append("$name gained $amountOfLevels levels!").append(System.lineSeparator())
        }
    }

}
