package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.party.SuperEnum

enum class AbilityItemId(
    val multiplier: Float
) : SuperEnum {

    STRIKE_1((1f / 3f) * 0.8f),
    STRIKE_2((2f / 3f) * 0.9f),
    STRIKE_3((3f / 3f) * 1.0f),
    STRIKE_4((4f / 3f) * 1.1f),

    BITE_1((1f / 4f) * 0.7f),
    BITE_2((2f / 4f) * 0.8f),
    BITE_3((3f / 4f) * 0.9f),
    BITE_4((4f / 4f) * 1.0f),
    BITE_5((5f / 4f) * 1.1f),

    BODY_SLAM_1((1f / 2f) * 0.9f),
    BODY_SLAM_2((2f / 2f) * 1.0f),
    BODY_SLAM_3((3f / 2f) * 1.1f);

    override val title: String = name.lowercase().replaceFirstChar { it.uppercase() }

}
