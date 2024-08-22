package nl.t64.cot.components.battle

import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.components.party.skills.SkillItemId
import kotlin.random.Random


class AttackAction(
    private val attacker: Character,
    private val target: Character,
    private val selectedAttack: String
) {
    private val hitPercentage: Int = attacker.getCalculatedTotalHit()
    private val cappedHitPercentage: Int = hitPercentage.coerceAtMost(100)
    private val isHit: Boolean = hitPercentage > Random.nextInt(0, 100)
    private val damage: Int = calculateDamage()
    private val criticalHitPercentage: Int = attacker.getCalculatedTotalSkillOf(SkillItemId.WARRIOR) * 4
    private val isCriticalHit: Boolean = criticalHitPercentage > Random.nextInt(0, 100)
    private val criticalDamage: Int = (damage * 1.5f).toInt()


    fun createConfirmationMessage(): String {
        if (criticalHitPercentage <= 0) {
            return """
                Do you want to $selectedAttack ${target.name} ?

                Chance to hit:  $cappedHitPercentage%   |   Damage:  $damage""".trimIndent()
        } else {
            return """
                Do you want to $selectedAttack ${target.name} ?

                Chance to hit (Damage):  $cappedHitPercentage%  ($damage)
                Critical hit (Damage):      $criticalHitPercentage%  ($criticalDamage)""".trimIndent()
        }
    }

    fun handle(): ArrayDeque<String> {
        val messages = ArrayDeque<String>()
        messages.add("${attacker.name} used $selectedAttack on ${target.name}.")

        if (isHit) {
            handleSuccess(messages)
        } else {
            handleFailure(messages)
        }
        return messages
    }

    private fun handleSuccess(messages: ArrayDeque<String>) {
        val damageDone = if (isCriticalHit) criticalDamage else damage
        target.takeDamage(damageDone)
        val damageTypeMessage = if (isCriticalHit) "A critical hit! " else ""
        messages.add("$damageTypeMessage${selectedAttack} successfully did $damageDone damage.${createDebugMessage()}")

        // "(It's super effective!)"
        // "(It's not very effective...)"
        if (!target.isAlive) {
            messages.add("${target.name} is defeated.")
        }
    }

    private fun handleFailure(messages: ArrayDeque<String>) {
        messages.add("${attacker.name}'s attack failed.${createDebugMessage()}")
    }

    private fun calculateDamage(): Int {
        val attack: Int = attacker.getCalculatedTotalDamage()
        val protection: Int = target.getCalculatedTotalProtection()
        return (attack - protection).coerceAtLeast(1).coerceAtMost(target.currentHp)
    }

    private fun createDebugMessage(): String {
        if (preferenceManager.isInDebugMode) {
            return " (${hitPercentage}% hit, ${criticalHitPercentage}% critHit)"
        } else {
            return ""
        }
    }

}
