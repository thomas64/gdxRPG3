package nl.t64.cot.components.party.stats

import nl.t64.cot.components.party.SuperEnum


enum class StatItemId : SuperEnum {

    INTELLIGENCE,
    DEXTERITY,
    STRENGTH,
    SPEED,
    WILLPOWER,
    CONSTITUTION,
    STAMINA;

    override val title: String = name.lowercase().replaceFirstChar { it.uppercase() }

}
