package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.Participant
import kotlin.math.roundToInt


class DoubleThrow(
    abilityItem: AbilityItem,
    attacker: Participant
) : Strike(
    abilityItem,
    attacker
) {

    override fun toString(): String {
        return "$name ($ap AP, $sp SP)"
    }

    override fun possibleCreateCopyWithGrayName(): BattleAbilityItem {
        return DoubleThrow(possibleCreateGrayName(), attacker)
    }

    override fun createPreviewMessage(): String {
        return currentWeapon?.let {
            """
                $this

                Weapon: ${it.name}
                ${it.getDurabilityText()}

                Target: ${target.character.name}
                Skill: ${it.skill?.title}
                ${possibleCreateEffectiveMessage()}

                Chance to hit: ${calculateHitPercentageCapped()}%
                Damage: ${calculateDamage()} (x2)
                Critical chance: ${calculateCriticalHitPercentage()}%
                Critical damage: ${calculateCriticalDamage()} (x2)
            """.trimIndent()
        } ?: """
            $this

            Target: ${target.character.name}

            No weapon equipped!
        """.trimIndent()
    }

    override fun handle(messages: ArrayDeque<String>) {
        super.handle(messages)

        if (target.character.isDead) return

        messages.add("${attacker.character.name} used $name on ${target.character.name}.")

        super.handle(messages)
    }

    override fun calculateHitPercentage(): Int {
        return (super.calculateHitPercentage() * (8f / 11f)).roundToInt().coerceAtLeast(0)
        // double throw:
        // Verwachte hits: 2 × 8/11 = 1.45
        // Verwachte damage output: 1.45 × 1.0 (multiplier) = 1.45

        // strong strike:
        // Verwachte hits: 1 × 1 = 1
        // Verwachte damage output: 1.0 × 1.47 (multiplier) = 1.47
    }

}
