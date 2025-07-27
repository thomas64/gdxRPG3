package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.party.SuperEnum

enum class AbilityItemId : SuperEnum {

    STRIKE_2,
    STRIKE_3,
    STRIKE_3F,
    STRIKE_4,

    BITE_3,
    BITE_4,

    BODY_SLAM_2,

    STAGGER,

    SNIPER_ARROW,
    BRUTE_FORCE,
    SHIELD_BASH,
    DOUBLE_THROW,
    GHOST_TOUCH,
    BACKSTAB,

    FIRE,
    ELFIRE,
    ARCFIRE,
    REXFIRE,

    WIND,
    ELWIND,
    ARCWIND,
    REXWIND,

    THUNDER,
    ELTHUNDER,
    ARCTHUNDER,
    REXTHUNDER,

    MAGIC_SHIELD,
    RESISTANCE,
    TELEPORTATION,

    BRILLIANCE,
    STUPIDITY,
    FINESSE,
    CLUMSINESS,
    MIGHT,
    DEBILITATION,
    HASTE,
    SLUGGISHNESS;

    override val title: String = name.lowercase().replaceFirstChar { it.uppercase() }

}
