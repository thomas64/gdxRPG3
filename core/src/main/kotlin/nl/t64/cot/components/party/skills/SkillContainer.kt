package nl.t64.cot.components.party.skills

import com.fasterxml.jackson.annotation.JsonCreator


private const val TOTAL_XP_NECESSARY_FOR_MOZES_STARTING_SKILLS = 26

class SkillContainer() {

    private val skills: SkillItemMap<SkillItemId, SkillItem> = SkillItemMap()

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
        return SkillItemId.entries
            .mapNotNull { skills[it] }
            .filter { hasPositiveQuantity(it) }
    }

    fun add(skillItem: SkillItem) {
        skills[skillItem.id] = skillItem
    }

    fun getTotalXpCost(): Int {
        return getAllAboveZero()
            .sumOf { it.getTotalXpCostFromRankZeroToCurrent() } - TOTAL_XP_NECESSARY_FOR_MOZES_STARTING_SKILLS
    }

    private fun hasPositiveQuantity(skillItem: SkillItem): Boolean {
        return skillItem.rank > 0
    }

}

private class SkillItemMap<K : Enum<K>, V> {
    private val map: MutableMap<String, V> = HashMap()
    operator fun get(key: Enum<K>): V? = map[key.name]
    operator fun set(key: Enum<K>, value: V) {
        map[key.name] = value
    }
}
