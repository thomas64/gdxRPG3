package nl.t64.cot.screens.academy

import nl.t64.cot.components.party.PersonalityItem
import nl.t64.cot.components.party.skills.SkillItem
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


class AcademyTooltip : PersonalityTooltip() {

    override fun getDescription(personalityItem: PersonalityItem, totalScholar: Int): String {
        val trainerSkill = personalityItem as SkillItem
        val heroSkill = InventoryUtils.getSelectedHero().getSkillById(trainerSkill.id)
        return heroSkill.getTrainerDescription(trainerSkill, totalScholar)
    }

}
