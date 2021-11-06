package nl.t64.cot.components.party.inventory

import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.stats.StatItemId
import java.beans.ConstructorProperties


class EquipContainer() {

    private val equipment: MutableMap<String, InventoryItem?> = mutableMapOf(
        Pair(InventoryGroup.WEAPON.name, null),
        Pair(InventoryGroup.SHIELD.name, null),
        Pair(InventoryGroup.ACCESSORY.name, null),
        Pair(InventoryGroup.HELMET.name, null),
        Pair(InventoryGroup.NECKLACE.name, null),
        Pair(InventoryGroup.SHOULDERS.name, null),
        Pair(InventoryGroup.CHEST.name, null),
        Pair(InventoryGroup.CLOAK.name, null),
        Pair(InventoryGroup.BRACERS.name, null),
        Pair(InventoryGroup.GLOVES.name, null),
        Pair(InventoryGroup.RING.name, null),
        Pair(InventoryGroup.BELT.name, null),
        Pair(InventoryGroup.PANTS.name, null),
        Pair(InventoryGroup.BOOTS.name, null))

    @ConstructorProperties("weapon", "shield", "chest", "pants", "boots")
    constructor(weaponId: String, shieldId: String?, chestId: String, pantsId: String?, bootsId: String?) : this() {
        this.equipment[InventoryGroup.WEAPON.name] = InventoryDatabase.createInventoryItem(weaponId)
        shieldId?.let { this.equipment[InventoryGroup.SHIELD.name] = InventoryDatabase.createInventoryItem(it) }
        this.equipment[InventoryGroup.CHEST.name] = InventoryDatabase.createInventoryItem(chestId)
        pantsId?.let { this.equipment[InventoryGroup.PANTS.name] = InventoryDatabase.createInventoryItem(it) }
        bootsId?.let { this.equipment[InventoryGroup.BOOTS.name] = InventoryDatabase.createInventoryItem(it) }
    }

    fun hasInventoryItem(itemId: String): Boolean {
        return equipment.values.any { it?.id == itemId }
    }

    fun getInventoryItem(inventoryGroup: InventoryGroup): InventoryItem? {
        return equipment[inventoryGroup.name]
    }

    fun clearAll() {
        equipment.keys.forEach { equipment[it] = null }
    }

    fun clearInventoryItem(inventoryGroup: InventoryGroup) {
        equipment[inventoryGroup.name] = null
    }

    fun forceSetInventoryItem(inventoryGroup: InventoryGroup, inventoryItem: InventoryItem) {
        equipment[inventoryGroup.name] = inventoryItem
    }

    fun getStatValueOf(inventoryGroup: InventoryGroup, statItemId: StatItemId): Int {
        return getInventoryItem(inventoryGroup)?.getAttributeOfStatItemId(statItemId) ?: 0
    }

    fun getSkillValueOf(inventoryGroup: InventoryGroup, skillItemId: SkillItemId): Int {
        return getInventoryItem(inventoryGroup)?.getAttributeOfSkillItemId(skillItemId) ?: 0
    }

    fun getCalcValueOf(inventoryGroup: InventoryGroup, calcAttributeId: CalcAttributeId): Int {
        return getInventoryItem(inventoryGroup)?.getAttributeOfCalcAttributeId(calcAttributeId) ?: 0
    }

    fun getItemsWithMinimalOf(statItemId: StatItemId): List<InventoryItem> {
        return equipment.values.filterNotNull().filter { it.getMinimalAttributeOfStatItemId(statItemId) > 0 }
    }

    fun getSumOfStat(statItemId: StatItemId): Int {
        return equipment.values.filterNotNull().sumOf { it.getAttributeOfStatItemId(statItemId) }
    }

    fun getSumOfSkill(skillItemId: SkillItemId): Int {
        return equipment.values.filterNotNull().sumOf { it.getAttributeOfSkillItemId(skillItemId) }
    }

    fun getSumOfCalc(calcAttributeId: CalcAttributeId): Int {
        return equipment.values.filterNotNull().sumOf { it.getAttributeOfCalcAttributeId(calcAttributeId) }
    }

    fun getSkillOfCurrentWeapon(): SkillItemId? {
        return getInventoryItem(InventoryGroup.WEAPON)?.getAttributeOfMinimal(InventoryMinimal.SKILL) as SkillItemId?
    }

    fun getStatItemIdOfMinimalOfCurrentWeapon(): StatItemId? {
        return getInventoryItem(InventoryGroup.WEAPON)?.getStatItemIdOfMinimalTypeOfWeapon()
    }

    fun getBonusProtectionWhenArmorSetIsComplete(): Int {
        return getInventoryItem(InventoryGroup.HELMET)
            ?.takeIf { doesEquippedArmorAllHaveSamePrefix(getPrefixOfId(it)) }
            ?.getAttributeOfCalcAttributeId(CalcAttributeId.PROTECTION)
            ?: 0
    }

    private fun getPrefixOfId(inventoryItem: InventoryItem): String {
        val splittedId: List<String> = inventoryItem.id.split("_")
        return splittedId[0] + "_" + splittedId[1]
    }

    private fun doesEquippedArmorAllHaveSamePrefix(prefix: String): Boolean {
        return equipment.values
            .filterNotNull()
            .filter { it.group.isPartArmorOfSet() }
            .count { it.id.startsWith(prefix) } == 9
    }

}
