package nl.t64.cot.components.party.skills

import com.fasterxml.jackson.annotation.JsonCreator


private const val TOTAL_XP_NECESSARY_FOR_MOZES_STARTING_SKILLS = 38

class SkillContainer() {

    private val skills: SkillItemMap<SkillItemId, Int> = SkillItemMap()

    @JsonCreator
    constructor(startingSkills: Map<String, Int>) : this() {
        startingSkills.forEach { (skillId, rank) ->
            skills[SkillItemId.valueOf(skillId.uppercase())] = rank
        }
    }

    fun getById(skillItemId: SkillItemId): SkillItem {
        val staffIds: Set<SkillItemId> = setOf(SkillItemId.STAFF_FIRE, SkillItemId.STAFF_WIND, SkillItemId.STAFF_THUNDER)
        val key: SkillItemId = if (skillItemId in staffIds) SkillItemId.STAFF else skillItemId
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
        return getAllAboveZero()
            .sumOf { it.getTotalXpCostFromRankZeroToCurrent() } - TOTAL_XP_NECESSARY_FOR_MOZES_STARTING_SKILLS
    }

}

private class SkillItemMap<K : Enum<K>, V> {
    private val map: MutableMap<String, V> = HashMap()
    operator fun get(key: Enum<K>): V? = map[key.name]
    operator fun set(key: Enum<K>, value: V) {
        map[key.name] = value
    }
}
