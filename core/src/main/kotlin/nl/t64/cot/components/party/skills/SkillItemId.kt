package nl.t64.cot.components.party.skills

import nl.t64.cot.components.party.SuperEnum
import kotlin.reflect.KClass


enum class SkillItemId(override val title: String, val skillClass: KClass<out SkillItem>) : SuperEnum {

    ALCHEMIST("Alchemist", Alchemist::class),
    BARBARIAN("Barbarian", Barbarian::class),
    DIPLOMAT("Diplomat", Diplomat::class),
    DRUID("Druid", Druid::class),
    GAMBLER("Gambler", Gambler::class),
    HEALER("Healer", Healer::class),
    JESTER("Jester", Jester::class),
    LOREMASTER("Loremaster", Loremaster::class),
    MECHANIC("Mechanic", Mechanic::class),
    MERCHANT("Merchant", Merchant::class),
    RANGER("Ranger", Ranger::class),
    SCHOLAR("Scholar", Scholar::class),
    STEALTH("Stealth", Stealth::class),
    THIEF("Thief", Thief::class),
    TROUBADOUR("Troubadour", Troubadour::class),
    WARRIOR("Warrior", Warrior::class),
    WIZARD("Wizard", Wizard::class),

    HAFTED("Hafted", Hafted::class),
    MISSILE("Missile", Missile::class),
    POLE("Pole", Pole::class),
    SHIELD("Shield", Shield::class),
    SWORD("Sword", Sword::class),
    THROWN("Thrown", Thrown::class),

    BITE("Bite", Bite::class);

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
            else -> throw IllegalArgumentException("Only possible to ask a player skill.")
        }
    }

    fun isCombatSkill(): Boolean {
        return when (this) {
            HAFTED, MISSILE, POLE, SHIELD, SWORD, THROWN -> false
            GAMBLER, HEALER, STEALTH, THIEF, TROUBADOUR, WARRIOR, WIZARD -> true
            ALCHEMIST, BARBARIAN, DIPLOMAT, DRUID, JESTER, LOREMASTER, MECHANIC, MERCHANT, RANGER, SCHOLAR -> false
            else -> throw IllegalArgumentException("Only possible to ask a player skill.")
        }
    }

    fun isCivilSkill(): Boolean {
        return when (this) {
            HAFTED, MISSILE, POLE, SHIELD, SWORD, THROWN -> false
            GAMBLER, HEALER, STEALTH, THIEF, TROUBADOUR, WARRIOR, WIZARD -> false
            ALCHEMIST, BARBARIAN, DIPLOMAT, DRUID, JESTER, LOREMASTER, MECHANIC, MERCHANT, RANGER, SCHOLAR -> true
            else -> throw IllegalArgumentException("Only possible to ask a player skill.")
        }
    }

}

fun String.toSkillItemClass(): KClass<SkillItem> {
    return this.toSkillItemId().skillClass as KClass<SkillItem>
}

fun String.toSkillItemId(): SkillItemId {
    return SkillItemId.values().first { this.equals(it.name, true) }
}
