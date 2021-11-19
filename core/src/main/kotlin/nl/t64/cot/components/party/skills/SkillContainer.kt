package nl.t64.cot.components.party.skills

import com.fasterxml.jackson.annotation.JsonCreator


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

private class SkillItemMap<K : Enum<K>, V> {
    private val map: MutableMap<String, V> = HashMap()
    operator fun get(key: Enum<K>): V? = map[key.name]
    operator fun set(key: Enum<K>, value: V) {
        map[key.name] = value
    }
}
