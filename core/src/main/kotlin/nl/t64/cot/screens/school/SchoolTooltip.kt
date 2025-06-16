package nl.t64.cot.screens.school

import nl.t64.cot.components.party.PersonalityItem
import nl.t64.cot.components.party.abilities.AbilityItem
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


class SchoolTooltip : PersonalityTooltip() {

    override fun getDescription(personalityItem: PersonalityItem): String {
        val teacherSpell = personalityItem as AbilityItem
        val totalXp = InventoryUtils.getSelectedHero().totalXp
        return teacherSpell.getTeacherDescription(totalXp)
    }

}
