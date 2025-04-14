package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.Participant
import kotlin.random.Random

open class Strike(
    abilityItem: AbilityItem,
    attacker: Participant
) : BattleAbilityItem(
    abilityItem,
    attacker
) {

    override fun toString(): String {
        if (sp > 0) {
            return "$name ($ap AP, $sp SP)"
        }
        return "$name ($ap AP)"
    }

    override fun possibleCreateCopyWithGrayName(): BattleAbilityItem {
        return Strike(possibleCreateGrayName(), attacker)
    }

    override fun createPreviewMessage(): String {
        return currentWeapon?.let {
            """
                $this

                Target: ${target.character.name}
                Weapon: ${it.name}
                ${it.getDurabilityText()}
                ${it.getRangeText()}
                ${possibleCreateEffectiveMessage()}
                Chance to hit: ${calculateHitPercentageCapped()}%
                Damage: ${calculateDamage()}
                Critical chance: ${calculateCriticalHitPercentage()}%
                Critical damage: ${calculateCriticalDamage()}
            """.trimIndent()
        } ?: """
            $this

            Target: ${target.character.name}

            No weapon equipped!
        """.trimIndent()
    }

    override fun handleSuccess(messages: ArrayDeque<String>) {
        val isCriticalHit: Boolean = calculateCriticalHitPercentage() > Random.nextInt(0, 100)
        val damage: Int = calculateDamage()
        val criticalDamage: Int = calculateCriticalDamage()
        val damageDone: Int = if (isCriticalHit) criticalDamage else damage

        target.character.takeDamage(damageDone)

        val critMessage: String = if (isCriticalHit) "A critical hit!  " else ""
        messages.add("""
            ${possibleAddEffectiveMessage()}
            $critMessage$name did $damageDone damage.
            """.trimIndent().trimMargin())

        if (attacker.isHero && damage <= 1) {
            messages.add("${target.character.name} ${target.character.gender} protection is too strong!")
        }
    }

}
