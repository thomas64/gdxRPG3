package nl.t64.cot.screens.school

import nl.t64.cot.components.party.PersonalityItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.spells.SpellItem
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


class SchoolTooltip : PersonalityTooltip() {

    override fun getDescription(personalityItem: PersonalityItem, totalScholar: Int): String {
        val teacherSpell = personalityItem as SpellItem
        val heroSpell = InventoryUtils.getSelectedHero().getSpellById(teacherSpell.id)
        val wizardRank = InventoryUtils.getSelectedHero().getSkillById(SkillItemId.WIZARD).rank
        return heroSpell.getTeacherDescription(teacherSpell, wizardRank, totalScholar)
    }

}
