package nl.t64.cot.components.party.skills

import nl.t64.cot.components.party.SuperEnum


private fun String.toTitleCase(): String {
    return lowercase().replaceFirstChar { it.uppercase() }
}

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

    // Combat Skills
    STEALTH,
    GAMBLER,
    HEALER,
    TROUBADOUR,
    THIEF,
    WARRIOR,
    WIZARD,

    // Weapon Skills
    SWORD,
    AXE,
    SPEAR,
    DAGGER,
    THROW,
    BOW,
    STAFF,
    STAFF_FIRE,
    STAFF_WIND,
    STAFF_THUNDER,
    SHIELD;

    override val title: String = name.substringBefore("_").toTitleCase()
    val elementalTitle: String = name.substringAfter("_").toTitleCase()

    companion object {
        private val advantages: Map<SkillItemId, SkillItemId> = mapOf(
            SWORD to AXE,
            AXE to SPEAR,
            SPEAR to SWORD,
            DAGGER to BOW,
            BOW to THROW,
            THROW to DAGGER,
            STAFF_FIRE to STAFF_WIND,
            STAFF_WIND to STAFF_THUNDER,
            STAFF_THUNDER to STAFF_FIRE
        )
    }

    /**
     * De elementaire staf-ids bestaan alleen voor de wapendriehoek; getraind wordt er enkel [STAFF].
     * Een wapen levert dus een id op waar geen rank bij hoort, en die moet je hierlangs halen
     * voordat je hem als skill opzoekt.
     */
    fun toTrainableSkill(): SkillItemId {
        return when {
            isElementalStaff() -> STAFF
            else -> this
        }
    }

    fun isElementalStaff(): Boolean {
        return when (this) {
            STAFF_FIRE, STAFF_WIND, STAFF_THUNDER -> true
            else -> false
        }
    }

    fun isWeaponSkill(): Boolean {
        return when (this) {
            SWORD, AXE, SPEAR, DAGGER, THROW, BOW, STAFF, SHIELD -> true
            else -> false
        }
    }

    fun isCombatSkill(): Boolean {
        return when (this) {
            STEALTH, GAMBLER, HEALER, TROUBADOUR, THIEF, WARRIOR, WIZARD -> true
            else -> false
        }
    }

    fun isCivilSkill(): Boolean {
        return when (this) {
            ALCHEMIST, MECHANIC, RANGER, MERCHANT -> true
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
        if (other == null) return true
        if (this == STAFF) throw IllegalStateException("STAFF itself can't have advantage.")
        return advantages[this] == other
    }

    fun hasDisadvantageFrom(other: SkillItemId?): Boolean {
        if (other == null) return false
        if (this == STAFF) throw IllegalStateException("STAFF itself can't have disadvantage.")
        return advantages[other] == this
    }

}
