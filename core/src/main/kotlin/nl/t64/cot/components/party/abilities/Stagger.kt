package nl.t64.cot.components.party.abilities

import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.screens.battle.BattleUtils
import kotlin.math.roundToInt
import kotlin.random.Random


class Stagger(
    abilityItem: AbilityItem,
    attacker: Participant
) : BattleAbilityItem(
    abilityItem,
    attacker
) {

    override fun createCopyForPreview(): BattleAbilityItem {
        return Stagger(abilityItem.copy(isPreview = true), attacker)
    }

    override fun createPreviewMessage(): String {
        if (preferenceManager.isCombatDetailsOn) {

            return currentWeapon?.let {
                val weaponHit: Int = it.getAttributeOfCalcAttributeId(CalcAttributeId.BASE_HIT)
                val weaponDamage: Int = it.getAttributeOfCalcAttributeId(CalcAttributeId.DAMAGE)
                """
                $name
                ${it.name} ${it.getDurabilityText()}
                ${createEffectiveMessage()}
                Chance to stagger:                  ${String.format("%3d", calculateStaggerPercentage())} %

                -------------------------------------------
                Weapon base hit chance:             ${String.format("%3d", weaponHit)} %
                Character modifier:                 ${String.format("%3d", attacker.character.getCalculatedTotalHit() - weaponHit)}
                Troubadour modifier:                ${String.format("%3d", attacker.character.bonus.getHitBonus())}
                Attack type multiplier:             ${getMultiplierForVisual(abilityItem.hitMultiplier)}
                Gambler modifier (+/-):             ${String.format("%3d", getGamblerBonusForVisual())}
                (Dis)advantage modifier:            ${String.format("%3d", getAdvantageBonusHit())}
                -------------------------------------------
                Total hit chance:                   ${String.format("%3d", calculateHitPercentageForVisual())} %
                Critical hit chance:                ${String.format("%3d", calculateCriticalHitPercentage())} %
                -------------------------------------------

                -------------------------------------------
                Weapon base damage:                 ${String.format("%3d", weaponDamage)}
                Character modifier:                 ${String.format("%3d", attacker.character.getCalculatedTotalDamage() - weaponDamage)}
                Attack type multiplier:             ${getMultiplierForVisual(abilityItem.damageMultiplier)}
                Gambler modifier (+/-):             ${String.format("%3d", getGamblerBonusForVisual())}
                Disadvantage multiplier:            ${getDisadvantagePenaltyDamageForVisual()}
                -------------------------------------------
                Total damage:                       ${String.format("%3.0f", calculateDamageForVisual())}
                Critical hit damage:                ${String.format("%3.0f", calculateCriticalDamageForVisual())}
                -------------------------------------------

                Enemy protection:                   ${String.format("%3d", target.character.getCalculatedTotalProtection())}
                Damage to inflict:                  ${String.format("%3d", calculateDamageForVisual().minusProtection())}
                Damage to inflict if critical hit:  ${String.format("%3d", calculateCriticalDamageForVisual().minusProtection())}

            """.trimIndent().trimMargin()
            } ?: createNoWeaponMessage()

        } else {

            return currentWeapon?.let {
                """
                $name
                ${it.name} ${it.getDurabilityText()}
                ${createEffectiveMessage()}
                Mod hit: ${String.format("%3d", calculateHitPercentageForVisual())} %
                Stagger: ${String.format("%3d", calculateStaggerPercentage())} %
                Damage:  ${String.format("%3d", calculateDamageForVisual().minusProtection())}
                Crit:    ${String.format("%3d", calculateCriticalHitPercentage())} %
            """.trimIndent().trimMargin()
            } ?: createNoWeaponMessage()

        }
    }

    override fun handleSuccess(attackData: AttackData) {
        val isCriticalHit: Boolean = calculateCriticalHitPercentage() > Random.nextInt(0, 100)
        val damageDone: Int = if (isCriticalHit) calculateCriticalDamageMinusProtection() else calculateDamageMinusProtection()
        val isStaggered: Boolean = calculateStaggerPercentage() > Random.nextInt(0, 100)

        if (isStaggered) BattleUtils.staggerTarget(target)
        target.character.takeDamage(damageDone)

        attackData.hasAdvantage = hasWeaponTriangleAdvantage()
        attackData.hasDisadvantage = hasWeaponTriangleDisadvantage()
        attackData.isCriticalHit = isCriticalHit
        attackData.damage = damageDone
        attackData.isStaggered = isStaggered
    }

    private fun calculateStaggerPercentage(): Int {
        val attackerWarriorRank: Int = attacker.character.getCalculatedTotalSkillOf(SkillItemId.WARRIOR)
        if (attackerWarriorRank == 0) return 0
        val warriorChance: Float = (target.staggerChance / 100f) * (5f * attackerWarriorRank)
        return (target.staggerChance + warriorChance).roundToInt().coerceAtMost(100)
    }

}
