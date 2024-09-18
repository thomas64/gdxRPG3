package nl.t64.cot.components.battle

import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.skills.SkillItemId
import kotlin.random.Random


class AttackAction(
    currentParticipant: Participant,
    private val target: Character,
    private val selectedAttack: String,
) {
    private val attacker: Character = currentParticipant.character

    private val hitPercentage: Int = attacker.getCalculatedTotalHit()
    private val cappedHitPercentage: Int = hitPercentage.coerceAtMost(100)
    private val isHit: Boolean = hitPercentage > Random.nextInt(0, 100)
    private val damage: Int = calculateDamage()
    private val cappedDamage: Int = damage.coerceAtMost(target.currentHp)
    private val criticalHitPercentage: Int = attacker.getCalculatedTotalSkillOf(SkillItemId.WARRIOR) * 4
    private val isCriticalHit: Boolean = criticalHitPercentage > Random.nextInt(0, 100)
    private val criticalDamage: Int = (damage * 1.5f).toInt()
    private val cappedCriticalDamage: Int = criticalDamage.coerceAtMost(target.currentHp)

    companion object {
        fun createForEnemy(currentEnemy: Participant, targetHero: Participant): AttackAction {
            // todo, weaponName is niet de bedoeling, dit moet een 'spell' worden. body slam, bite, etc.
            // de names van die weapons moeten dus ook niet in enemy.json staan.
            val weaponName: String = currentEnemy.character.getInventoryItem(InventoryGroup.WEAPON)!!.name
            return AttackAction(currentEnemy, targetHero.character, weaponName) // ← hier dus
        }
    }


    fun createPreviewMessage(): String {
        val weapon: InventoryItem = attacker.getInventoryItem(InventoryGroup.WEAPON)!!
        if (criticalHitPercentage <= 0) {
            return """
                3 AP
                $selectedAttack ${target.name} with ${weapon.name} (${weapon.durability} uses)

                Chance to hit:  $cappedHitPercentage%   |   Damage:  $damage""".trimIndent()
        } else {
            return """
                3 AP
                $selectedAttack ${target.name} with ${weapon.name} (${weapon.durability} uses)

                Chance to hit:  $cappedHitPercentage%   |   Normal damage:  $damage
                ----------------------------------------------------
                Critical hit:  $criticalHitPercentage%   |   Critical damage:  $criticalDamage""".trimIndent()
        }
    }

    fun createConfirmationMessage(): String {
        val weapon: InventoryItem = attacker.getInventoryItem(InventoryGroup.WEAPON)!!
        if (criticalHitPercentage <= 0) {
            return """
                Do you want to $selectedAttack ${target.name} with
                ${weapon.name} (${weapon.durability} uses) for 3 AP ?

                Chance to hit:  $cappedHitPercentage%   |   Damage:  $damage""".trimIndent()
        } else {
            return """
                Do you want to $selectedAttack ${target.name} with
                ${weapon.name} (${weapon.durability} uses) for 3 AP ?

                Chance to hit:  $cappedHitPercentage%   |   Normal damage:  $damage
                ----------------------------------------------------
                Critical hit:  $criticalHitPercentage%   |   Critical damage:  $criticalDamage""".trimIndent()
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
        val weapon: InventoryItem = attacker.getInventoryItem(InventoryGroup.WEAPON)!!
        weapon.durability--
        val damageDone = if (isCriticalHit) criticalDamage else damage
        target.takeDamage(damageDone)
        val damageTypeMessage = if (isCriticalHit) "A critical hit! " else ""
        messages.add("$damageTypeMessage${selectedAttack} successfully did $damageDone damage.${createDebugMessage()}")

        // "(It's super effective!)"
        // "(It's not very effective...)"

        if (attacker is HeroItem && weapon.durability <= 0) {
            messages.add("${weapon.name} broke!")
            attacker.clearInventoryItemFor(InventoryGroup.WEAPON)
        }
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
        return (attack - protection).coerceAtLeast(1)
    }

    private fun createDebugMessage(): String {
        if (preferenceManager.isInDebugMode) {
            return " (${hitPercentage}% hit, ${criticalHitPercentage}% critHit)"
        } else {
            return ""
        }
    }

}
