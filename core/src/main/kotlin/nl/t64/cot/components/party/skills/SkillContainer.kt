package nl.t64.cot.components.party.skills

import com.fasterxml.jackson.annotation.JsonCreator
import java.util.*


class SkillContainer() {

    private val skills: MutableMap<SkillItemId, Int> = EnumMap(SkillItemId::class.java)

    @JsonCreator
    constructor(startingSkills: Map<String, Int>) : this() {
        startingSkills.forEach { (skillId, rank) ->
            skills[SkillItemId.valueOf(skillId.uppercase())] = rank
        }
    }

    fun toProgress(): Map<String, Int> {
        return skills.entries.associate { (skillItemId, rank) -> skillItemId.name to rank }
    }

    fun applyProgress(ranks: Map<String, Int>) {
        ranks.forEach { (skillId, rank) ->
            val skillItemId = SkillItemId.valueOf(skillId)
            skills[skillItemId] = rank
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
