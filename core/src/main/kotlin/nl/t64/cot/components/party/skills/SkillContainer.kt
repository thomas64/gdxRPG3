package nl.t64.cot.components.party.skills

import com.fasterxml.jackson.annotation.JsonCreator


class SkillContainer() {

    private val skills: MutableMap<String, SkillItem> = HashMap()

    @JsonCreator
    constructor(startingSkills: Map<String, Int>) : this() {
        startingSkills
            .map { SkillDatabase.createSkillItem(it.key, it.value) }
            .forEach { this.skills[it.id.name] = it }
    }

    fun getById(skillItemId: SkillItemId): SkillItem {
        return skills[skillItemId.name] ?: SkillDatabase.createSkillItem(skillItemId.name, 0)
    }

    fun getAllAboveZero(): List<SkillItem> {
        return SkillItemId.values()
            .mapNotNull { skills[it.name] }
            .filter { hasPositiveQuantity(it) }
    }

    fun add(skillItem: SkillItem) {
        skills[skillItem.id.name] = skillItem
    }

    private fun hasPositiveQuantity(skillItem: SkillItem): Boolean {
        return skillItem.rank > 0
    }

}
