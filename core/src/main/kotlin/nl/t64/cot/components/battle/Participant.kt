package nl.t64.cot.components.battle

import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.stats.StatItemId
import kotlin.random.Random


class Participant(
    val character: Character
) {
    val isHero: Boolean get() = character is HeroItem
    var turnCounter: Float = 0f


    fun attack(attackType: String, target: Character): ArrayDeque<String> {
        val messages = ArrayDeque<String>()
        messages.add("${character.name} used $attackType on ${target.name}.")

        val totalHit: Int = character.getCalculatedTotalHit()
        if (totalHit >= Random.nextInt(0, 100)) {
            val attack: Int = character.getCalculatedTotalDamage()
            val protection: Int = target.getCalculatedTotalProtection()
            val damage: Int = (attack - protection).coerceAtLeast(1).coerceAtMost(target.currentHp)
            target.takeDamage(damage)
            messages.add("$attackType successfully did $damage damage."
                             + if (preferenceManager.isInDebugMode) " (${totalHit}% hit)" else "")
            //  "(It's super effective!)"
            // "(It's not very effective...)"
            if (!target.isAlive) {
                messages.add("${target.name} is defeated.")
            }
        } else {
            messages.add("${character.name}'s attack failed.")
        }
        return messages
    }

    fun rest(): String {
        if (character.currentHp == character.maximumHp) {
            return "${character.name} skipped a turn."
        } else {
            (character as HeroItem).recoverPartHp(1)
            return "${character.name} rested for a turn and recovered 1 HP."
        }
    }

    fun updateTurnCounter() {
        turnCounter += 10f + character.getCalculatedTotalStatOf(StatItemId.SPEED) //* 0.5f
        // if character  mp == 0 -> speed / 3f?
    }

    fun isTurnCounterAtMax(): Boolean {
        return turnCounter >= 200f
    }

    fun resetTurnCounter() {
        turnCounter -= 200f
    }

}
