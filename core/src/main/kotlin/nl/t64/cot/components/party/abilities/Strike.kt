package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.Character
import nl.t64.cot.components.party.HeroItem
import kotlin.random.Random

class Strike(
    abilityItem: AbilityItem,
    attacker: Character
) : BattleAbilityItem(
    abilityItem,
    attacker
) {

    override fun toString(): String {
        return "$name ($ap AP)"
    }

    override fun createPreviewMessage(): String {
        return currentWeapon?.let {
            """
                $this

                Target: ${target.name}
                Weapon: ${it.name}
                Durability: ${it.durability}
                ${it.getRangeText()}
                ${possibleCreateEffectiveMessage()}
                Chance to hit: ${calculateHitPercentageCapped()}%
                Damage: ${calculateDamage()}
                Critical chance: ${calculateCriticalHitPercentage()}%
                Critical damage: ${calculateCriticalDamage()}
            """.trimIndent()
        } ?: """
            $this

            Target: ${target.name}

            No weapon equipped!
        """.trimIndent()
    }

    override fun handleSuccess(messages: ArrayDeque<String>) {
        val isCriticalHit: Boolean = calculateCriticalHitPercentage() > Random.nextInt(0, 100)
        val damage: Int = calculateDamage()
        val criticalDamage: Int = calculateCriticalDamage()
        val damageDone: Int = if (isCriticalHit) criticalDamage else damage

        target.takeDamage(damageDone)

        val critMessage: String = if (isCriticalHit) "A critical hit!  " else ""
        messages.add("$critMessage$name successfully did $damageDone damage.")

        possibleAddEffectiveMessage(messages)

        if (attacker is HeroItem && damage <= 1) {
            messages.add("${target.name} ${target.gender} protection is too strong!")
        }
    }

}
