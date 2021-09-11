package nl.t64.cot.components.party.skills

import nl.t64.cot.components.party.SuperEnum


enum class SkillItemId(override val title: String) : SuperEnum {

    ALCHEMIST("Alchemist"),
    DIPLOMAT("Diplomat"),
    HEALER("Healer"),
    MECHANIC("Mechanic"),
    MERCHANT("Merchant"),
    RANGER("Ranger"),
    SCHOLAR("Scholar"),
    STEALTH("Stealth"),
    THIEF("Thief"),
    TROUBADOUR("Troubadour"),
    WARRIOR("Warrior"),
    WIZARD("Wizard"),

    HAFTED("Hafted"),
    MISSILE("Missile"),
    POLE("Pole"),
    SHIELD("Shield"),
    SWORD("Sword"),
    THROWN("Thrown"),

    BITE("Bite");

    fun isHandToHandWeaponSkill(): Boolean {
        return when (this) {
            SWORD, HAFTED, POLE -> true
            MISSILE, THROWN -> false
            else -> throw IllegalArgumentException("Only possible to ask a Weapon Skill.")
        }
    }

}

fun String.toSkillItemId(): SkillItemId {
    return SkillItemId.values().first { this.equals(it.name, true) }
}
