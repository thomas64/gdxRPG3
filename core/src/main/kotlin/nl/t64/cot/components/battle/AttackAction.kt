package nl.t64.cot.components.battle

import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.skills.SkillItemId
import kotlin.math.roundToInt
import kotlin.random.Random


class AttackAction(
    private val currentParticipant: Participant,
    private val target: Character,
    private val selectedAttack: BattleAbilityItem,
) {
    private val attacker: Character = currentParticipant.character

    private val hitPercentage: Int = calculateHitPercentage()
    private val cappedHitPercentage: Int = hitPercentage.coerceAtMost(100)
    private var isHit: Boolean = hitPercentage > Random.nextInt(0, 100)
    private val isBlock: Boolean = target.getCalculatedTotalDefense() > Random.nextInt(0, 100)
    private val damage: Int = calculateDamage()
    private val cappedDamage: Int = damage.coerceAtMost(target.currentHp)
    private val criticalHitPercentage: Int = calculateCriticalHitPercentage()
    private val isCriticalHit: Boolean = criticalHitPercentage > Random.nextInt(0, 100)
    private val criticalDamage: Int = (damage * 1.51f).roundToInt()
    private val cappedCriticalDamage: Int = criticalDamage.coerceAtMost(target.currentHp)

    companion object {
        fun createForEnemy(currentEnemy: Participant, targetHero: Participant, battleId: String): AttackAction {

            val ability: BattleAbilityItem = currentEnemy.getBattleAbilities()
                .filter { it.ap <= currentEnemy.currentAP }
                .maxByOrNull { it.ap }
                ?: currentEnemy.getBattleAbilities().first()

            return AttackAction(currentEnemy, targetHero.character, ability)
                .specialCasesWorkaround(battleId, targetHero)
        }

        private fun AttackAction.specialCasesWorkaround(battleId: String, targetHero: Participant): AttackAction {
            if (battleId == "farm_battle" && targetHero.character.id == "luana") {
                if (this.isHit && this.damage >= targetHero.character.currentHp) {
                    this.isHit = false
                }
            }
            return this
        }
    }

    fun isCostingTooMuchAp(): String? {
        return when {
            selectedAttack.ap > currentParticipant.currentAP -> {
                createPreviewMessage() +
                    System.lineSeparator() +
                    "_________________" +
                    System.lineSeparator() +
                    System.lineSeparator() +
                    "    Not enough AP!"
            }
            else -> null
        }
    }

    fun createConfirmationMessage(): String {
        return createPreviewMessage() +
            System.lineSeparator() +
            "_________________" + """

            Attack?"""
    }

    fun createPreviewMessage(): String {
        return selectedAttack.currentWeapon?.let {
            """
                ${selectedAttack.name} (${selectedAttack.ap} AP)

                Target: ${target.name}
                Weapon: ${it.name}
                Durability: ${it.durability}
                ${it.getRangeText()}
                ${createPossibleEffectiveMessage()}
                Chance to hit: $cappedHitPercentage%
                Damage: $damage
                Critical chance: $criticalHitPercentage%
                Critical damage: $criticalDamage
            """.trimIndent()
        } ?: """
            ${selectedAttack.name} (${selectedAttack.ap} AP)

            Target: ${target.name}

            No weapon equipped!
        """.trimIndent()
    }

    private fun createPossibleEffectiveMessage(): String {
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

    fun handle(): ArrayDeque<String> {
        if (currentParticipant.currentAP < selectedAttack.ap) {
            return ArrayDeque(listOf("${attacker.name} ended ${attacker.gender} turn."))
        }

        currentParticipant.currentAP -= selectedAttack.ap
        val messages = ArrayDeque<String>()
        messages.add("${attacker.name} used ${selectedAttack.name} on ${target.name}.")

        if (isHit) {
            handleSuccess(messages)
        } else {
            handleFailure(messages)
        }
        createDebugMessage()
        return messages
    }

    private fun handleSuccess(messages: ArrayDeque<String>) {
        val weapon: InventoryItem = selectedAttack.currentWeapon!!
        weapon.durability--
        if (isBlock) {
            messages.add("${target.name} blocked the attack.")
        } else {
            val damageDone: Int = if (isCriticalHit) criticalDamage else damage
            target.takeDamage(damageDone)
            val critMessage: String = if (isCriticalHit) "A critical hit!  " else ""
            messages.add("$critMessage${selectedAttack.name} successfully did $damageDone damage.")

            if (hasWeaponTriangleAdvantage()) {
                messages.add("It's super effective!")
            } else if (hasWeaponTriangleDisadvantage()) {
                messages.add("It's not very effective...")
            }

            if (currentParticipant.isHero && damage <= 1) {
                messages.add("${target.name} ${target.gender} protection is too strong!")
            }
        }

        if (attacker is HeroItem && weapon.durability <= 0) {
            messages.add("${weapon.name} broke!")
            attacker.clearInventoryItemFor(InventoryGroup.WEAPON)
        }
        if (target.isDead) {
            messages.add("${target.name} is defeated.")
        }
    }

    private fun handleFailure(messages: ArrayDeque<String>) {
        messages.add("${attacker.name}'s attack failed.")
    }

    private fun calculateHitPercentage(): Int {
        val attackerHitPercentage: Int = attacker.getCalculatedTotalHit()

        val weaponTriangle = when {
            hasWeaponTriangleAdvantage() -> 10
            hasWeaponTriangleDisadvantage() -> -10
            else -> 0
        }

        return (attackerHitPercentage + weaponTriangle).coerceAtLeast(0)
    }

    private fun calculateCriticalHitPercentage(): Int {
        val attackerCriticalHitPercentage: Int = attacker.getCalculatedTotalSkillOf(SkillItemId.WARRIOR) * 4

        val weaponTriangle = when {
            hasWeaponTriangleAdvantage() -> 20
            hasWeaponTriangleDisadvantage() -> -20
            else -> 0
        }
        return (attackerCriticalHitPercentage + weaponTriangle).coerceAtLeast(0)
    }

    private fun hasWeaponTriangleAdvantage(): Boolean {
        val attackSkill: SkillItemId = selectedAttack.currentWeapon!!.skill!!
        val targetSkill: SkillItemId? = target.getInventoryItem(InventoryGroup.WEAPON)?.skill
        return attackSkill.hasAdvantageOver(targetSkill)
    }

    private fun hasWeaponTriangleDisadvantage(): Boolean {
        val attackSkill: SkillItemId = selectedAttack.currentWeapon!!.skill!!
        val targetSkill: SkillItemId? = target.getInventoryItem(InventoryGroup.WEAPON)?.skill
        return attackSkill.hasDisadvantageFrom(targetSkill)
    }

    private fun calculateDamage(): Int {
        val attack: Int = (attacker.getCalculatedTotalDamage() * selectedAttack.id.multiplier).toInt()
        val protection: Int = target.getCalculatedTotalProtection()
        return (attack - protection).coerceAtLeast(1)
    }

    private fun createDebugMessage() {
        if (preferenceManager.isInDebugMode) {
            println("${attacker.name}: ${hitPercentage}% hit, ${criticalHitPercentage}% critHit, ${selectedAttack.ap} AP.")
        }
    }

}
