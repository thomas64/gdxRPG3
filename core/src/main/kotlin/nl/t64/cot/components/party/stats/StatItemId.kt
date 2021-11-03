package nl.t64.cot.components.party.stats

import nl.t64.cot.components.party.SuperEnum


enum class StatItemId : SuperEnum {

    INTELLIGENCE,
    WILLPOWER,
    STRENGTH,
    DEXTERITY,
    ENDURANCE,
    STAMINA;

    override val title: String get() = this.name.lowercase().replaceFirstChar { it.uppercase() }

}
