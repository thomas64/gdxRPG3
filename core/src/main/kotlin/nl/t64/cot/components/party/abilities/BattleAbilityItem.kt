package nl.t64.cot.components.party.abilities

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
    private val id: AbilityItemId = abilityItem.id
    val name: String = abilityItem.name
    val ap: Int = abilityItem.ap
    val sp: Int = abilityItem.sp

    protected val currentWeapon: InventoryItem? get() = attacker.character.getInventoryItem(InventoryGroup.WEAPON)

    lateinit var target: Participant

    override fun toString(): String {
        return name
    }

    abstract fun possibleCreateCopyWithGrayName(): BattleAbilityItem
    abstract fun createPreviewMessage(): String
    abstract fun handleSuccess(messages: ArrayDeque<String>)

    open fun handle(messages: ArrayDeque<String>) {
        if (isHit()) {
            handleHit(messages)
        } else {
            handleFailure(messages)
        }
    }

    protected open fun isHit(): Boolean {
        return calculateHitPercentage() > Random.nextInt(0, 100)
    }

    private fun handleHit(messages: ArrayDeque<String>) {
        if (isBlock()) {
            handleBlock(messages)
        } else {
            handleSuccess(messages)
        }

        handleDurability(messages)

        if (target.character.isDead) {
            messages.add("${target.character.name} is defeated.")
        }
    }

    private fun isBlock(): Boolean {
        return target.character.getCalculatedTotalDefense() > Random.nextInt(0, 100)
    }

    private fun handleFailure(messages: ArrayDeque<String>) {
        messages.add("${attacker.character.name}'s attack failed.")
    }

    private fun handleBlock(messages: ArrayDeque<String>) {
        messages.add("${target.character.name} blocked the attack.")
        val shield: InventoryItem = target.character.getInventoryItem(InventoryGroup.SHIELD)!!
        shield.durability--
        if (shield.durability <= 0) {
            messages.add("${shield.name} broke!")
            target.character.clearInventoryItemFor(InventoryGroup.SHIELD)
        }
    }

    private fun handleDurability(messages: ArrayDeque<String>) {
        val weapon = currentWeapon!!
        weapon.durability--
        if (attacker.isHero && weapon.durability <= 0) {
            messages.add("${weapon.name} broke!")
            attacker.character.clearInventoryItemFor(InventoryGroup.WEAPON)
        }
    }

    fun hasEnoughApSp(): Boolean {
        return attacker.currentAP >= ap
            && attacker.character.currentSp >= sp
    }

    fun isWeaponAllowed(): Boolean {
        return abilityItem.isWeaponAllowed(currentWeapon)
    }

    protected fun calculateHitPercentageCapped(): Int {
        return calculateHitPercentage().coerceAtMost(100)
    }

    open fun calculateHitPercentage(): Int {
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
            hasWeaponTriangleAdvantage() -> 30
            hasWeaponTriangleDisadvantage() -> -30
            else -> 0
        }
        return (attackerCriticalHitPercentage + weaponTriangle).coerceAtLeast(0)
    }

    protected fun calculateCriticalDamage(): Int {
        return (calculateDamage() * 1.75f).roundToInt()
    }

    fun calculateDamage(): Int {
        val attack: Int = (attacker.character.getCalculatedTotalDamage() * id.multiplier).toInt()
        val protection: Int = target.character.getCalculatedTotalProtection()
        val damage: Int = (attack - protection)
        val weaponTriangle: Float = if (hasWeaponTriangleDisadvantage()) 1.5f else 1f
        return (damage / weaponTriangle).toInt().coerceAtLeast(1)
    }

    protected fun possibleCreateGrayName(): AbilityItem {
        if (isWeaponAllowed() && hasEnoughApSp()) {
            return abilityItem
        }
        return abilityItem.copy(name = "[GRAY]$name")
    }

    protected fun possibleCreateEffectiveMessage(): String {
        return when {
            hasWeaponTriangleAdvantage() -> {
                """[BLUE]Advantage![BLACK]
                   |"""
            }
            hasWeaponTriangleDisadvantage() -> {
                """[FIREBRICK]Disadvantage![BLACK]
                   |"""
            }
            else -> ""
        }
    }

    protected fun possibleAddEffectiveMessage(): String {
        val weaponName = currentWeapon!!.name.takeUnless { it.isBlank() } ?: name

        return when {
            hasWeaponTriangleAdvantage() -> {
                """$weaponName is super effective!
                   |"""
            }
            hasWeaponTriangleDisadvantage() -> {
                """$weaponName is not very effective...
                   |"""
            }
            else -> ""
        }
    }

    private fun hasWeaponTriangleAdvantage(): Boolean {
        val attackSkill: SkillItemId = currentWeapon!!.skill!!
        val targetSkill: SkillItemId? = target.character.getInventoryItem(InventoryGroup.WEAPON)?.skill
        return attackSkill.hasAdvantageOver(targetSkill)
    }

    private fun hasWeaponTriangleDisadvantage(): Boolean {
        val attackSkill: SkillItemId = currentWeapon!!.skill!!
        val targetSkill: SkillItemId? = target.character.getInventoryItem(InventoryGroup.WEAPON)?.skill
        return attackSkill.hasDisadvantageFrom(targetSkill)
    }

}
