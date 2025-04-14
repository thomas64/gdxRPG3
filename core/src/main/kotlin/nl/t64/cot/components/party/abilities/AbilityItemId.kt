package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.party.SuperEnum

enum class AbilityItemId(
    val multiplier: Float
) : SuperEnum {

    STRIKE_2((2f / 3f) * 0.9f),
    STRIKE_3((3f / 3f) * 1.0f),
    STRIKE_4((4f / 3f) * 1.1f),

    BITE_3((3f / 4f) * 0.9f),
    BITE_4((4f / 4f) * 1.0f),

    BODY_SLAM_2((2f / 2f) * 1.0f),

    STAGGER(1f),

    DOUBLE_THROW(1f);

    override val title: String = name.lowercase().replaceFirstChar { it.uppercase() }

}
