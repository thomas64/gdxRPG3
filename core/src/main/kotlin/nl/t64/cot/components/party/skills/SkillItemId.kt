package nl.t64.cot.components.party.skills

import nl.t64.cot.components.party.SuperEnum


enum class SkillItemId : SuperEnum {

    NONE,

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
    AXE,
    SPEAR,
    DAGGER,
    STAFF,
    THROW,
    BOW,
    SHIELD;

    override val title: String = name.lowercase().replaceFirstChar { it.uppercase() }

    fun isHandToHandWeaponSkill(): Boolean {
        return when (this) {
            SWORD, AXE, SPEAR, DAGGER, STAFF -> true
            THROW, BOW -> false
            else -> throw IllegalArgumentException("Only possible to ask a Weapon Skill.")
        }
    }

    fun isWeaponSkill(): Boolean {
        return when (this) {
            SWORD, AXE, SPEAR, DAGGER, STAFF, THROW, BOW, SHIELD -> true
            else -> false
        }
    }

    fun isMagicSkill(): Boolean {
        return when (this) {
            WIZARD -> true
            else -> false
        }
    }

    fun isCombatSkill(): Boolean {
        return when (this) {
            STEALTH, GAMBLER, HEALER, TROUBADOUR, THIEF, WARRIOR -> true
            else -> false
        }
    }

    fun isCivilSkill(): Boolean {
        return when (this) {
            ALCHEMIST, MECHANIC, RANGER, MERCHANT, SCHOLAR -> true
            else -> false
        }
    }

    fun isCommunicationSkill(): Boolean {
        return when (this) {
            BARBARIAN, DIPLOMAT, JESTER, DRUID, LOREMASTER -> true
            else -> false
        }
    }

    fun hasAdvantageOver(other: SkillItemId?): Boolean {
        return when {
            other == null -> true

            this == SWORD -> other == AXE
            this == AXE -> other == SPEAR
            this == SPEAR -> other == SWORD

            this == DAGGER -> other == STAFF
            this == STAFF -> other == THROW
            this == THROW -> other == BOW
            this == BOW -> other == DAGGER

            else -> false
        }
    }

    fun hasDisadvantageFrom(other: SkillItemId?): Boolean {
        return when {
            other == null -> false

            this == SWORD -> other == SPEAR
            this == AXE -> other == SWORD
            this == SPEAR -> other == AXE

            this == DAGGER -> other == BOW
            this == STAFF -> other == DAGGER
            this == THROW -> other == STAFF
            this == BOW -> other == THROW

            else -> false
        }
    }

}
