package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.party.SuperEnum

enum class AbilityItemId : SuperEnum {

    STRIKE,
    BITE,
    BODY_SLAM;

    override val title: String = name.lowercase().replaceFirstChar { it.uppercase() }

}
