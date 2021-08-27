package nl.t64.cot.components.party.spells

import nl.t64.cot.components.party.SuperEnum


enum class SchoolType(override val title: String) : SuperEnum {

    NONE("No"),
    UNKNOWN("Unknown"),
    NEUTRAL("Neutral"),
    ELEMENTAL("Elemental"),
    NAMING("Naming"),
    NECROMANCY("Necromancy"),
    STAR("Star");

}
