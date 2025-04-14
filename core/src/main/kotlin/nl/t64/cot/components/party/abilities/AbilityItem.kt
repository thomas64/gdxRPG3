package nl.t64.cot.components.party.abilities

import com.fasterxml.jackson.annotation.JsonProperty
import nl.t64.cot.components.party.PersonalityItem
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.skills.SkillItemId


data class AbilityItem(
    override val id: AbilityItemId = AbilityItemId.STAGGER,  // Value will be replaced when constructed.
    override val name: String = "",
    override val description: List<String> = emptyList(),
    val imageId: String = "",
    val ap: Int = 0,
    val sp: Int = 0,
    private val skill: SkillItemId = SkillItemId.NONE,
    @JsonProperty("weapon_skills")
    private val weaponSkills: List<SkillItemId> = emptyList(),
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
        if (description.isEmpty()) return ""
        return getDescription()
    }

    private fun getDescription(): String {
        return (description.joinToString(System.lineSeparator()) + System.lineSeparator()
            + createRequiredSkill()
            + createRequiredWeapon()
            + System.lineSeparator() + "AP cost: " + ap
            + createSpCost())
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

    private fun createSpCost(): String {
        if (sp > 0) {
            return System.lineSeparator() + "SP cost: $sp"
        } else {
            return ""
        }
    }

}
