package nl.t64.cot.components.party.inventory

import com.fasterxml.jackson.annotation.JsonProperty
import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.stats.StatItemId
import kotlin.math.floor
import kotlin.math.roundToInt


class InventoryItem(
    val name: String = "",
    val sort: Int = 0,
    val description: List<String> = emptyList(),
    val group: InventoryGroup = InventoryGroup.EMPTY,
    @JsonProperty("is_two_handed")
    val isTwoHanded: Boolean = false,
    val skill: SkillItemId? = null,
    private val price: Int = 0,
    private val weight: Int = 0,
    @JsonProperty("min_intelligence")
    private val minIntelligence: Int = 0,
    @JsonProperty("min_willpower")
    private val minWillpower: Int = 0,
    @JsonProperty("min_strength")
    private val minStrength: Int = 0,
    @JsonProperty("min_dexterity")
    private val minDexterity: Int = 0,
    @JsonProperty("action_points")
    private val actionPoints: Int = 0,
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
    private val spellBoost: Boolean = false,
    @JsonProperty("move_points")
    private val movePoints: Int = 0,
) {

    lateinit var id: String
    var amount: Int = 0
    val isStackable: Boolean get() = group.isStackable()

    fun createCopy(amount: Int): InventoryItem {
        val itemCopy = InventoryItem(
            name, sort, description, group, isTwoHanded, skill, price, weight,
            minIntelligence, minWillpower, minStrength, minDexterity,
            actionPoints, baseHit, damage, protection, defense, spellBattery, transformation,
            intelligence, willpower, strength, dexterity, constitution,
            alchemist, diplomat, healer, loremaster, mechanic, ranger, stealth, thief, troubadour, warrior, wizard,
            cheatDeath, quickSwitch, spellBoost, movePoints)
        itemCopy.id = id
        itemCopy.amount = amount
        return itemCopy
    }

    fun getAttributeOfMinimal(minimal: InventoryMinimal): Any {
        return when (minimal) {
            InventoryMinimal.SKILL -> skill ?: 0
            InventoryMinimal.MIN_INTELLIGENCE -> minIntelligence
            InventoryMinimal.MIN_WILLPOWER -> minWillpower
            InventoryMinimal.MIN_DEXTERITY -> minDexterity
            InventoryMinimal.MIN_STRENGTH -> minStrength
        }
    }

    fun getMinimalAttributeOfStatItemId(statItemId: StatItemId): Int {
        return when (statItemId) {
            StatItemId.INTELLIGENCE -> minIntelligence
            StatItemId.WILLPOWER -> minWillpower
            StatItemId.STRENGTH -> minStrength
            StatItemId.DEXTERITY -> minDexterity
            StatItemId.CONSTITUTION,
            StatItemId.STAMINA -> 0
        }
    }

    fun getAttributeOfStatItemId(statItemId: StatItemId): Int {
        return when (statItemId) {
            StatItemId.INTELLIGENCE -> intelligence
            StatItemId.WILLPOWER -> willpower
            StatItemId.STRENGTH -> strength
            StatItemId.DEXTERITY -> dexterity
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
            CalcAttributeId.WEIGHT -> weight
            CalcAttributeId.ACTION_POINTS -> actionPoints
            CalcAttributeId.BASE_HIT -> baseHit
            CalcAttributeId.DAMAGE -> damage
            CalcAttributeId.PROTECTION -> protection
            CalcAttributeId.DEFENSE -> defense
            CalcAttributeId.SPELL_BATTERY -> spellBattery
            CalcAttributeId.TRANSFORMATION -> transformation
        }
    }

    fun getMinimalsOtherItemHasAndYouDont(otherItem: InventoryItem): Set<InventoryMinimal> {
        return when {
            hasOnlyOneMinimal() && otherItem.hasOnlyOneMinimal() -> emptySet()
            else -> InventoryMinimal.values()
                .filter { getAttributeOfMinimal(it) is Int }
                .filter { getAttributeOfMinimal(it) as Int == 0 }
                .filter { otherItem.getAttributeOfMinimal(it) as Int > 0 }
                .toSet()
        }
    }

    private fun hasOnlyOneMinimal(): Boolean {
        return InventoryMinimal.values()
            .filter { getAttributeOfMinimal(it) is Int }
            .filter { getAttributeOfMinimal(it) as Int > 0 }
            .count() == 1
    }

    fun getCalcsOtherItemHasAndYouDont(otherItem: InventoryItem): Set<CalcAttributeId> {
        return CalcAttributeId.values()
            .filter { getAttributeOfCalcAttributeId(it) == 0 }
            .filter { otherItem.getAttributeOfCalcAttributeId(it) > 0 }
            .toSet()
    }

    fun getStatsOtherItemHasAndYouDont(otherItem: InventoryItem): Set<StatItemId> {
        return StatItemId.values()
            .filter { getAttributeOfStatItemId(it) == 0 }
            .filter { otherItem.getAttributeOfStatItemId(it) > 0 }
            .toSet()
    }

    fun getSkillsOtherItemHasAndYouDont(otherItem: InventoryItem): Set<SkillItemId> {
        return SkillItemId.values()
            .filter { getAttributeOfSkillItemId(it) == 0 }
            .filter { otherItem.getAttributeOfSkillItemId(it) > 0 }
            .toSet()
    }

    fun hasSameIdAs(candidateItem: InventoryItem): Boolean {
        return hasSameIdAs(candidateItem.id)
    }

    fun hasSameIdAs(candidateId: String?): Boolean {
        return id.equals(candidateId, true)
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
