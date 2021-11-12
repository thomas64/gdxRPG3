package nl.t64.cot.components.party.skills

import com.fasterxml.jackson.annotation.JsonCreator


private const val NUMBER_OF_SKILL_SLOTS = 23

class SkillContainer() {

    private val skills: MutableMap<SkillItemId, SkillItem> = HashMap(NUMBER_OF_SKILL_SLOTS)

    @JsonCreator
    constructor(startingSkills: Map<String, Int>) : this() {
        startingSkills
            .map { SkillDatabase.createSkillItem(it.key, it.value) }
            .forEach { this.skills[it.id] = it }
    }

    fun getById(skillItemId: SkillItemId): SkillItem {
        return skills[skillItemId] ?: SkillDatabase.createSkillItem(skillItemId.name, 0)
    }

    fun getAllAboveZero(): List<SkillItem> {
        return SkillItemId.values()
            .mapNotNull { skills[it] }
            .filter { hasPositiveQuantity(it) }
    }

    fun add(skillItem: SkillItem) {
        skills[skillItem.id] = skillItem
    }

    private fun hasPositiveQuantity(skillItem: SkillItem): Boolean {
        return skillItem.rank > 0
    }

}
