package nl.t64.cot.components.party.inventory

import com.fasterxml.jackson.annotation.JsonProperty
import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.stats.StatItemId
import kotlin.math.floor
import kotlin.math.roundToInt


data class InventoryItem(
    var amount: Int = 0,
    val id: String = "",
    val name: String = "",
    val sort: Int = 0,
    val description: List<String> = emptyList(),
    val group: InventoryGroup = InventoryGroup.EMPTY,
    @JsonProperty("is_unique")
    val isUnique: Boolean = false,
    @JsonProperty("is_two_handed")
    val isTwoHanded: Boolean = false,
    val skill: SkillItemId? = null,
    private val price: Int = 0,
    var durability: Int = 0,
    val maxDurability: Int = durability,
    @JsonProperty("min_intelligence")
    private val minIntelligence: Int = 0,
    @JsonProperty("min_dexterity")
    private val minDexterity: Int = 0,
    @JsonProperty("min_strength")
    private val minStrength: Int = 0,
    @JsonProperty("min_willpower")
    private val minWillpower: Int = 0,
    @JsonProperty("min_constitution")
    private val minConstitution: Int = 0,
    private val range: List<Int> = emptyList(),
    @JsonProperty("base_hit")
    private val baseHit: Int = 0,
    private val damage: Int = 0,
    private val protection: Int = 0,
    private val defense: Int = 0,
    @JsonProperty("spell_battery")
    private val spellBattery: Int = 0,
    private val transformation: Int = 0,
    private val intelligence: Int = 0,
    private val willpower: Int = 0,
    private val strength: Int = 0,
    private val dexterity: Int = 0,
    private val constitution: Int = 0,
    private val speed: Int = 0,
    private val alchemist: Int = 0,
    private val diplomat: Int = 0,
    private val healer: Int = 0,
    private val loremaster: Int = 0,
    private val mechanic: Int = 0,
    private val ranger: Int = 0,
    private val stealth: Int = 0,
    private val thief: Int = 0,
    private val troubadour: Int = 0,
    private val warrior: Int = 0,
    private val wizard: Int = 0,
    @JsonProperty("cheat_death")
    private val cheatDeath: Boolean = false,
    @JsonProperty("quick_switch")
    private val quickSwitch: Boolean = false,
    @JsonProperty("spell_boost")
    private val spellBoost: Boolean = false
) {

    val isStackable: Boolean get() = group.isStackable()
    val isShield: Boolean get() = group == InventoryGroup.SHIELD

    fun createCopy(amount: Int): InventoryItem {
        return copy(amount = amount)
    }

    fun getAttributeOfMinimal(minimal: InventoryMinimal): Any {
        return when (minimal) {
            InventoryMinimal.SKILL -> skill ?: 0
            InventoryMinimal.MIN_INTELLIGENCE -> minIntelligence
            InventoryMinimal.MIN_DEXTERITY -> minDexterity
            InventoryMinimal.MIN_STRENGTH -> minStrength
            InventoryMinimal.MIN_WILLPOWER -> minWillpower
            InventoryMinimal.MIN_CONSTITUTION -> minConstitution
        }
    }

    fun getMinimalAttributeOfStatItemId(statItemId: StatItemId): Int {
        return when (statItemId) {
            StatItemId.INTELLIGENCE -> minIntelligence
            StatItemId.DEXTERITY -> minDexterity
            StatItemId.STRENGTH -> minStrength
            StatItemId.WILLPOWER -> minWillpower
            StatItemId.CONSTITUTION -> minConstitution
            StatItemId.STAMINA,
            StatItemId.SPEED -> 0
        }
    }

    fun getStatItemIdOfMinimalTypeOfWeapon(): StatItemId {
        val weaponMinimals = listOf(minIntelligence, minStrength, minDexterity)
        check(weaponMinimals.count { it > 0 } == 1) { "Only one minimal possible for a weapon." }
        return when {
            minIntelligence != 0 -> StatItemId.INTELLIGENCE
            minDexterity != 0 -> StatItemId.DEXTERITY
            minStrength != 0 -> StatItemId.STRENGTH
            else -> throw IllegalStateException("Not all minimals can be 0 for a weapon.")
        }
    }

    fun getAttributeOfStatItemId(statItemId: StatItemId): Int {
        return when (statItemId) {
            StatItemId.INTELLIGENCE -> intelligence
            StatItemId.DEXTERITY -> dexterity
            StatItemId.STRENGTH -> strength
            StatItemId.SPEED -> speed
            StatItemId.WILLPOWER -> willpower
            StatItemId.CONSTITUTION -> constitution
            StatItemId.STAMINA -> 0
        }
    }

    fun getAttributeOfSkillItemId(skillItemId: SkillItemId): Int {
        return when (skillItemId) {
            SkillItemId.ALCHEMIST -> alchemist
            SkillItemId.BARBARIAN -> 0
            SkillItemId.DIPLOMAT -> diplomat
            SkillItemId.DRUID -> 0
            SkillItemId.GAMBLER -> 0
            SkillItemId.HEALER -> healer
            SkillItemId.JESTER -> 0
            SkillItemId.LOREMASTER -> loremaster
            SkillItemId.MECHANIC -> mechanic
            SkillItemId.MERCHANT -> 0
            SkillItemId.STEALTH -> stealth
            SkillItemId.RANGER -> ranger
            SkillItemId.SCHOLAR -> 0
            SkillItemId.THIEF -> thief
            SkillItemId.TROUBADOUR -> troubadour
            SkillItemId.WARRIOR -> warrior
            SkillItemId.WIZARD -> wizard
            SkillItemId.HAFTED,
            SkillItemId.MISSILE,
            SkillItemId.POLE,
            SkillItemId.SHIELD,
            SkillItemId.SWORD,
            SkillItemId.THROWN -> 0
            SkillItemId.BITE -> 0
        }
    }

    fun getAttributeOfCalcAttributeId(calcAttributeId: CalcAttributeId): Int {
        return when (calcAttributeId) {
            CalcAttributeId.ACTION_POINTS -> 0
            CalcAttributeId.BASE_HIT -> baseHit
            CalcAttributeId.DAMAGE -> damage
            CalcAttributeId.PROTECTION -> protection
            CalcAttributeId.DEFENSE -> defense
            CalcAttributeId.SPELL_BATTERY -> spellBattery
            CalcAttributeId.TRANSFORMATION -> transformation
            CalcAttributeId.DURABILITY -> durability
        }
    }

    fun getMinimalsOtherItemHasAndYouDont(otherItem: InventoryItem): Set<InventoryMinimal> {
        return when {
            hasOnlyOneMinimal() && otherItem.hasOnlyOneMinimal() -> emptySet()
            else -> InventoryMinimal.entries
                .filter { getAttributeOfMinimal(it) is Int }
                .filter { getAttributeOfMinimal(it) as Int == 0 }
                .filter { otherItem.getAttributeOfMinimal(it) as Int > 0 }
                .toSet()
        }
    }

    private fun hasOnlyOneMinimal(): Boolean {
        return InventoryMinimal.entries
            .filter { getAttributeOfMinimal(it) is Int }
            .filter { getAttributeOfMinimal(it) as Int > 0 }
            .count() == 1
    }

    fun getCalcsOtherItemHasAndYouDont(otherItem: InventoryItem): Set<CalcAttributeId> {
        return CalcAttributeId.entries
            .filter { getAttributeOfCalcAttributeId(it) == 0 }
            .filter { otherItem.getAttributeOfCalcAttributeId(it) > 0 }
            .toSet()
    }

    fun getStatsOtherItemHasAndYouDont(otherItem: InventoryItem): Set<StatItemId> {
        return StatItemId.entries
            .filter { getAttributeOfStatItemId(it) == 0 }
            .filter { otherItem.getAttributeOfStatItemId(it) > 0 }
            .toSet()
    }

    fun getSkillsOtherItemHasAndYouDont(otherItem: InventoryItem): Set<SkillItemId> {
        return SkillItemId.entries
            .filter { getAttributeOfSkillItemId(it) == 0 }
            .filter { otherItem.getAttributeOfSkillItemId(it) != 0 }
            .toSet()
    }

    fun hasSameIdAs(candidateItem: InventoryItem): Boolean {
        return hasSameIdAs(candidateItem.id)
    }

    fun hasSameIdAs(candidateId: String?): Boolean {
        return id.equals(candidateId, true)
    }

    fun getWeaponRange(): List<Int> {
        return when (skill) {
            SkillItemId.SWORD,
            SkillItemId.HAFTED,
            SkillItemId.POLE,
            SkillItemId.THROWN,
            SkillItemId.MISSILE,
            SkillItemId.BITE -> range.ifEmpty { listOf(1) }
            else -> throw IllegalArgumentException("Only possible to ask a Weapon Skill.")
        }
    }

    fun getBuyPriceTotal(totalMerchant: Int): Int {
        return getBuyPricePiece(totalMerchant) * amount
    }

    fun getBuyPricePiece(totalMerchant: Int): Int {
        return (price - ((price / 100f) * totalMerchant)).roundToInt().run {
            takeUnless { this == 0 } ?: 1
        }
    }

    fun getSellValueTotal(totalMerchant: Int): Int {
        return getSellValuePiece(totalMerchant) * amount
    }

    fun getSellValuePiece(totalMerchant: Int): Int {
        if (group == InventoryGroup.RESOURCE || group == InventoryGroup.ITEM) return 0
        val value = floor(price / 3f).toInt()
        return (value + ((value / 300f) * totalMerchant)).roundToInt()
    }

    fun increaseAmountWith(sourceItem: InventoryItem) {
        increaseAmountWith(sourceItem.amount)
    }

    fun increaseAmountWith(amount: Int) {
        this.amount += amount
    }

    fun decreaseAmountWith(amount: Int) {
        this.amount -= amount
        check(this.amount >= 1) { "Amount cannot be below 1." }
    }

    fun createMessageFailToDequip(dependantItem: InventoryItem): String {
        return """
            Cannot unequip the $name.
            The ${dependantItem.name} depends on it.""".trimIndent()
    }

    fun createMessageFailToEquipTwoHanded(otherItem: InventoryItem): String {
        return """
            Cannot equip the $name.
            First unequip the ${otherItem.name}.""".trimIndent()
    }

}
