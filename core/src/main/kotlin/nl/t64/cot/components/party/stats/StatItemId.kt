package nl.t64.cot.components.party.stats

import nl.t64.cot.components.party.SuperEnum


enum class StatItemId(override val title: String) : SuperEnum {

    INTELLIGENCE("Intelligence"),
    WILLPOWER("Willpower"),
    STRENGTH("Strength"),
    DEXTERITY("Dexterity"),
    ENDURANCE("Endurance"),
    STAMINA("Stamina");

}
