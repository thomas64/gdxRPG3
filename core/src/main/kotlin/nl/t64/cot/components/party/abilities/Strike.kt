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
        val apField = String.format("%3d AP", ap)
        val spField = if (sp > 0) " | $sp SP" else "       "
        val totalWidth = 28
        val leftPart = name
        val rightPart = "$apField$spField"
        val spaces = " ".repeat((totalWidth - leftPart.length - rightPart.length).coerceAtLeast(1))
        return "$leftPart$spaces$rightPart"
    }

    override fun possibleCreateCopyWithGrayName(): BattleAbilityItem {
        return Strike(possibleCreateGrayName(), attacker)
    }

    override fun createPreviewMessage(): String {
        return currentWeapon?.let {
            """
                $name
                ${it.name} ${it.getDurabilityText()}
                ${possibleCreateEffectiveMessage()}
                Hit:      ${String.format("%3d", calculateHitPercentageCapped())} %
                Damage:   ${String.format("%3d", calculateDamage())}
                Crit:     ${String.format("%3d", calculateCriticalHitPercentage())} %
            """.trimIndent().trimMargin()
        } ?: createNoWeaponMessage()
    }

    override fun handleSuccess(messages: ArrayDeque<String>) {
        val isCriticalHit: Boolean = calculateCriticalHitPercentage() > Random.nextInt(0, 100)
        val damageDone: Int = if (isCriticalHit) calculateCriticalDamage() else calculateDamage()

        target.character.takeDamage(damageDone)

        val critMessage: String = if (isCriticalHit) "A critical hit! " else ""
        messages.add("""
            ${possibleAddEffectiveMessage()}
            $critMessage$name did $damageDone damage.
            """.trimIndent().trimMargin())
    }

}
