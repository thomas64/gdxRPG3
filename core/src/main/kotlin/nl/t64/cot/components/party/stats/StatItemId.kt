package nl.t64.cot.components.party.stats

import nl.t64.cot.components.party.SuperEnum


enum class StatItemId : SuperEnum {

    INTELLIGENCE,
    WILLPOWER,
    STRENGTH,
    DEXTERITY,
    CONSTITUTION,
    STAMINA,
    SPEED;

    override val title: String = name.lowercase().replaceFirstChar { it.uppercase() }

}
