package nl.t64.cot.components.party.skills

import nl.t64.cot.components.party.SuperEnum


enum class SkillItemId : SuperEnum {

    ALCHEMIST,
    BARBARIAN,
    DIPLOMAT,
    DRUID,
    JESTER,
    LOREMASTER,
    MECHANIC,
    MERCHANT,
    RANGER,
    SCHOLAR,

    GAMBLER,
    HEALER,
    STEALTH,
    THIEF,
    TROUBADOUR,
    WARRIOR,
    WIZARD,

    HAFTED,
    MISSILE,
    POLE,
    SHIELD,
    SWORD,
    THROWN,

    BITE;

    override val title: String get() = this.name.lowercase().replaceFirstChar { it.uppercase() }

    fun isHandToHandWeaponSkill(): Boolean {
        return when (this) {
            SWORD, HAFTED, POLE -> true
            MISSILE, THROWN -> false
            else -> throw IllegalArgumentException("Only possible to ask a Weapon Skill.")
        }
    }

    fun isWeaponSkill(): Boolean {
        return when (this) {
            HAFTED, MISSILE, POLE, SHIELD, SWORD, THROWN -> true
            GAMBLER, HEALER, STEALTH, THIEF, TROUBADOUR, WARRIOR, WIZARD -> false
            ALCHEMIST, BARBARIAN, DIPLOMAT, DRUID, JESTER, LOREMASTER, MECHANIC, MERCHANT, RANGER, SCHOLAR -> false
            BITE -> throw IllegalArgumentException("Only possible to ask a player skill.")
        }
    }

    fun isCombatSkill(): Boolean {
        return when (this) {
            HAFTED, MISSILE, POLE, SHIELD, SWORD, THROWN -> false
            GAMBLER, HEALER, STEALTH, THIEF, TROUBADOUR, WARRIOR, WIZARD -> true
            ALCHEMIST, BARBARIAN, DIPLOMAT, DRUID, JESTER, LOREMASTER, MECHANIC, MERCHANT, RANGER, SCHOLAR -> false
            BITE -> throw IllegalArgumentException("Only possible to ask a player skill.")
        }
    }

    fun isCivilSkill(): Boolean {
        return when (this) {
            HAFTED, MISSILE, POLE, SHIELD, SWORD, THROWN -> false
            GAMBLER, HEALER, STEALTH, THIEF, TROUBADOUR, WARRIOR, WIZARD -> false
            ALCHEMIST, BARBARIAN, DIPLOMAT, DRUID, JESTER, LOREMASTER, MECHANIC, MERCHANT, RANGER, SCHOLAR -> true
            BITE -> throw IllegalArgumentException("Only possible to ask a player skill.")
        }
    }

}
