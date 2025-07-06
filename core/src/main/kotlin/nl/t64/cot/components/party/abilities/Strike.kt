package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.Participant
import kotlin.random.Random

open class Strike(
    abilityItem: AbilityItem,
    attacker: Participant
) : BattleAbilityItem(
    abilityItem,
    attacker
) {

    override fun createCopyForPreview(): BattleAbilityItem {
        return Strike(abilityItem.copy(isPreview = true), attacker)
    }

    override fun createPreviewMessage(): String {
        return currentWeapon?.let {
            """
                $name
                ${it.name} ${it.getDurabilityText()}
                ${createEffectiveMessage()}
                Mod hit: ${String.format("%3d", calculateHitPercentageCapped())} %
                Damage:  ${String.format("%3d", calculateDamage())}
                Crit:    ${String.format("%3d", calculateCriticalHitPercentage())} %
            """.trimIndent().trimMargin()
        } ?: createNoWeaponMessage()
    }

    override fun handleSuccess(attackData: AttackData) {
        val isCriticalHit: Boolean = calculateCriticalHitPercentage() > Random.nextInt(0, 100)
        val damageDone: Int = if (isCriticalHit) calculateCriticalDamage() else calculateDamage()

        target.character.takeDamage(damageDone)

        attackData.hasAdvantage = hasWeaponTriangleAdvantage()
        attackData.hasDisadvantage = hasWeaponTriangleDisadvantage()
        attackData.isCriticalHit = isCriticalHit
        attackData.damage = damageDone
    }

}
