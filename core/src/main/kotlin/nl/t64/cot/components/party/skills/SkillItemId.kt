package nl.t64.cot.components.party.skills

import nl.t64.cot.components.party.SuperEnum


enum class SkillItemId : SuperEnum {

    // Communication Skills
    BARBARIAN,
    DIPLOMAT,
    JESTER,
    DRUID,
    LOREMASTER,

    // Civil Skills
    ALCHEMIST,
    MECHANIC,
    RANGER,
    MERCHANT,
    SCHOLAR,

    // Combat Skills
    STEALTH,
    GAMBLER,
    HEALER,
    TROUBADOUR,
    THIEF,
    WARRIOR,

    // Magic Skills
    WIZARD, // todo, toevoegen alle ideeën van magic skills uit excel.

    // Weapon Skills
    SWORD,
    HAFTED,
    POLE,
    MISSILE,
    THROWN,
    SHIELD,

    // Enemy Skills
    BITE;

    override val title: String = name.lowercase().replaceFirstChar { it.uppercase() }

    fun isHandToHandWeaponSkill(): Boolean {
        return when (this) {
            SWORD, HAFTED, POLE -> true
            MISSILE, THROWN -> false
            BITE -> true
            else -> throw IllegalArgumentException("Only possible to ask a Weapon Skill.")
        }
    }

    fun isWeaponSkill(): Boolean {
        return when (this) {
            SWORD, HAFTED, POLE, MISSILE, THROWN, SHIELD -> true
            BITE -> throw IllegalArgumentException("Only possible to ask a player skill.")
            else -> false
        }
    }

    fun isMagicSkill(): Boolean {
        return when (this) {
            WIZARD -> true
            BITE -> throw IllegalArgumentException("Only possible to ask a player skill.")
            else -> false
        }
    }

    fun isCombatSkill(): Boolean {
        return when (this) {
            STEALTH, GAMBLER, HEALER, TROUBADOUR, THIEF, WARRIOR -> true
            BITE -> throw IllegalArgumentException("Only possible to ask a player skill.")
            else -> false
        }
    }

    fun isCivilSkill(): Boolean {
        return when (this) {
            ALCHEMIST, MECHANIC, RANGER, MERCHANT, SCHOLAR -> true
            BITE -> throw IllegalArgumentException("Only possible to ask a player skill.")
            else -> false
        }
    }

    fun isCommunicationSkill(): Boolean {
        return when (this) {
            BARBARIAN, DIPLOMAT, JESTER, DRUID, LOREMASTER -> true
            BITE -> throw IllegalArgumentException("Only possible to ask a player skill.")
            else -> false
        }
    }

}
