package nl.t64.cot.components.party.skills

import com.fasterxml.jackson.annotation.JsonCreator


class SkillContainer() {

    private val skills: SkillItemMap<SkillItemId, Int> = SkillItemMap()

    @JsonCreator
    constructor(startingSkills: Map<String, Int>) : this() {
        startingSkills.forEach { (skillId, rank) ->
            skills[SkillItemId.valueOf(skillId.uppercase())] = rank
        }
    }

    fun getById(skillItemId: SkillItemId): SkillItem {
        val key: SkillItemId = skillItemId.toTrainableSkill()
        return SkillDatabase.createSkillItem(key.name, skills[key] ?: 0)
    }

    fun getAllAboveZero(): List<SkillItem> {
        return SkillItemId.entries.mapNotNull { id ->
            skills[id]
                ?.takeIf { rank -> rank > 0 }
                ?.let { rank -> SkillDatabase.createSkillItem(id.name, rank) }
        }
    }

    fun setRank(skillItemId: SkillItemId, rank: Int) {
        skills[skillItemId] = rank
    }

    fun getTotalXpCost(): Int {
        return getAllAboveZero().sumOf { it.getTotalXpCostFromRankZeroToCurrent() }
    }

}

private class SkillItemMap<K : Enum<K>, V> {
    private val map: MutableMap<String, V> = HashMap()
    operator fun get(key: Enum<K>): V? = map[key.name]
    operator fun set(key: Enum<K>, value: V) {
        map[key.name] = value
    }
}
