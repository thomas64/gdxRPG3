package nl.t64.cot.components.party.abilities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import nl.t64.cot.components.party.PersonalityItem
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.skills.SkillItemId
import kotlin.math.roundToInt


@JsonIgnoreProperties(ignoreUnknown = true)
data class AbilityItem(
    override val id: AbilityItemId = AbilityItemId.STAGGER,  // Value will be replaced when constructed.
    override val name: String = "",
    val imageId: String = "",
    @JsonProperty("gold_cost") val goldCost: Int = 50,
    @JsonProperty("xp_cost") private val xpCost: Int = 200, // todo, eventueel goedkopere waarden in json voor fire, wind, etc?
    @JsonProperty("resource") private val requiredResource: ResourceType = ResourceType.NONE,
    val ap: Int = 0,
    val sp: Int = 0,
    private val skill: SkillItemId = SkillItemId.NONE,
    @JsonProperty("min_skill") val minSkill: Int = 0,
    @JsonProperty("weapon_skills") private val weaponSkills: List<SkillItemId> = emptyList(),
    @JsonProperty("hit_multiplier") val hitMultiplier: Float = 1f,
    @JsonProperty("damage_multiplier") val damageMultiplier: Float = 1f,
) : PersonalityItem() {

    fun createCopy(): AbilityItem {
        return copy()
    }

    fun isWeaponAllowed(weapon: InventoryItem?): Boolean {
        if (weapon == null) return false
        if (weaponSkills.isEmpty()) return true
        return weapon.skill in weaponSkills
    }

    override fun getTotalDescription(): String {
        val descriptionLines: List<String> = AbilityDatabase.getDescription(id)
        if (descriptionLines.isEmpty()) return ""
        return (descriptionLines.joinToString(System.lineSeparator())
            + System.lineSeparator()
            + createRequiredSkill()
            + createRequiredWeapon()
            + createRequiredResource()
            + createApCost()
            + createSpCost())
    }

    fun getTeacherDescription(totalXp: Int): String {
        return (getTotalDescription() + System.lineSeparator()
            + System.lineSeparator()
            + "[GOLD]XP cost: ${calculateXpCost(totalXp)}" + System.lineSeparator()
            + "[GOLD]Gold cost: $goldCost")
    }

    fun calculateXpCost(totalXp: Int): Int {
        return maxOf((totalXp * 0.1f).roundToInt(), xpCost)
    }

    private fun createRequiredSkill(): String {
        if (skill == SkillItemId.NONE) {
            return ""
        } else {
            return System.lineSeparator() + "Required skill: " + skill.title
        }
    }

    private fun createRequiredWeapon(): String {
        if (weaponSkills.isEmpty()) {
            return ""
        } else {
            return System.lineSeparator() + "Required weapon: " + weaponSkills.joinToString(", ") { it.title }
        }
    }

    private fun createRequiredResource(): String {
        if (requiredResource == ResourceType.NONE) {
            return ""
        } else {
            return System.lineSeparator() + "Requires: " + requiredResource.title
        }
    }

    private fun createApCost(): String {
        return System.lineSeparator() + "AP cost: $ap"
    }

    private fun createSpCost(): String {
        if (sp > 0) {
            return System.lineSeparator() + "SP cost: $sp"
        } else {
            return ""
        }
    }

}
