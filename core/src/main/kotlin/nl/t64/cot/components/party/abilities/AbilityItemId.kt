package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.party.SuperEnum

enum class AbilityItemId : SuperEnum {

    STRIKE_2,
    STRIKE_3,
    STRIKE_3F,
    STRIKE_4,

    STRIKE_2E,
    BITE_3,
    BITE_4,
    BODY_SLAM_2,
    SCRATCH_2,

    STAGGER,

    SNIPER_ARROW,
    BRUTE_FORCE,
    SHIELD_BASH,
    DOUBLE_THROW,
    GHOST_TOUCH,
    BACKSTAB,

    LAY_ON_HANDS,
    HEAL_WITH_HERBS,
    PERFORM_BEAUTY,
    PERFORM_CHAOS,

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
    TELEPORTATION,

    BRILLIANCE,
    STUPIDITY,
    FINESSE,
    CLUMSINESS,
    MIGHT,
    DEBILITATION,
    HASTE,
    SLUGGISHNESS,
    RESISTANCE;

    override val title: String = name.lowercase().replaceFirstChar { it.uppercase() }

    fun isScratch(): Boolean {
        return this == SCRATCH_2
    }

    fun isBite(): Boolean {
        return this in listOf(BITE_3, BITE_4)
    }

    fun isBodySlam(): Boolean {
        return this == BODY_SLAM_2
    }

    fun isFireSpell(): Boolean {
        return this in listOf(FIRE, ELFIRE, ARCFIRE, REXFIRE)
    }

    fun isWindSpell(): Boolean {
        return this in listOf(WIND, ELWIND, ARCWIND, REXWIND)
    }

    fun isThunderSpell(): Boolean {
        return this in listOf(THUNDER, ELTHUNDER, ARCTHUNDER, REXTHUNDER)
    }

}
