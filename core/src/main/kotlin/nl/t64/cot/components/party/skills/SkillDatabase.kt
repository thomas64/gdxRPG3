package nl.t64.cot.components.party.skills

import nl.t64.cot.resources.ConfigDataLoader


object SkillDatabase {

    private val skillItems: Map<String, SkillItem> = ConfigDataLoader.createSkills()

    fun createSkillItem(skillId: String, rank: Int): SkillItem {
        val skillItem = skillItems[skillId.lowercase()]!!
        return skillItem.createCopy(rank)
    }

}
