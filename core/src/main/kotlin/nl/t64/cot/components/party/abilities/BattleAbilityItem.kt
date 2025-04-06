package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.Character
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.skills.SkillItemId
import kotlin.math.roundToInt
import kotlin.random.Random


abstract class BattleAbilityItem(
    val abilityItem: AbilityItem,
    val attacker: Participant
) {
    val id: AbilityItemId = abilityItem.id
    val name: String = abilityItem.name
    val ap: Int = abilityItem.ap
    val sp: Int = abilityItem.sp

    val currentWeapon: InventoryItem? get() = attacker.character.getInventoryItem(InventoryGroup.WEAPON)

    lateinit var target: Character

    override fun toString(): String {
        return name
    }

    abstract fun possibleCreateCopyWithGrayName(): BattleAbilityItem
    abstract fun createPreviewMessage(): String
    abstract fun handleSuccess(messages: ArrayDeque<String>)

    open fun isHit(): Boolean {
        return calculateHitPercentage() > Random.nextInt(0, 100)
    }

    fun calculateHitPercentageCapped(): Int {
        return calculateHitPercentage().coerceAtMost(100)
    }

    fun calculateHitPercentage(): Int {
        val attackerHitPercentage: Int = attacker.character.getCalculatedTotalHit()

        val weaponTriangle = when {
            hasWeaponTriangleAdvantage() -> 10
            hasWeaponTriangleDisadvantage() -> -10
            else -> 0
        }

        return (attackerHitPercentage + weaponTriangle).coerceAtLeast(0)
    }

    fun calculateCriticalHitPercentage(): Int {
        val attackerCriticalHitPercentage: Int = attacker.character.getCalculatedTotalSkillOf(SkillItemId.WARRIOR) * 4

        val weaponTriangle = when {
            hasWeaponTriangleAdvantage() -> 20
            hasWeaponTriangleDisadvantage() -> -20
            else -> 0
        }
        return (attackerCriticalHitPercentage + weaponTriangle).coerceAtLeast(0)
    }

    fun calculateCriticalDamage(): Int {
        return (calculateDamage() * 1.51f).roundToInt()
    }

    fun calculateDamage(): Int {
        val attack: Int = (attacker.character.getCalculatedTotalDamage() * id.multiplier).toInt()
        val protection: Int = target.getCalculatedTotalProtection()
        return (attack - protection).coerceAtLeast(1)
    }

    protected fun possibleCreateEffectiveMessage(): String {
        return when {
            hasWeaponTriangleAdvantage() -> """
                Super effective!
                """
            hasWeaponTriangleDisadvantage() -> """
                Not very effective...
                """
            else -> ""
        }
    }

    protected fun possibleAddEffectiveMessage(messages: ArrayDeque<String>) {
        if (hasWeaponTriangleAdvantage()) {
            messages.add("It's super effective!")
        } else if (hasWeaponTriangleDisadvantage()) {
            messages.add("It's not very effective...")
        }
    }

    private fun hasWeaponTriangleAdvantage(): Boolean {
        val attackSkill: SkillItemId = currentWeapon!!.skill!!
        val targetSkill: SkillItemId? = target.getInventoryItem(InventoryGroup.WEAPON)?.skill
        return attackSkill.hasAdvantageOver(targetSkill)
    }

    private fun hasWeaponTriangleDisadvantage(): Boolean {
        val attackSkill: SkillItemId = currentWeapon!!.skill!!
        val targetSkill: SkillItemId? = target.getInventoryItem(InventoryGroup.WEAPON)?.skill
        return attackSkill.hasDisadvantageFrom(targetSkill)
    }

}
