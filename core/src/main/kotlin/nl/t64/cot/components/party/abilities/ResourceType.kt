package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.party.SuperEnum


enum class ResourceType(
    override val title: String
) : SuperEnum {

    NONE(""),
    GOLD("Gold"),
    HERB("Herb"),
    SPICE("Spice"),
    GEMSTONE("Gemstone"),
    LEATHER("Leather"),
    WOOD("Wood"),
    METAL("Metal");

}
