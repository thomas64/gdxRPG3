package nl.t64.cot.components.party.abilities

import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.components.party.CalcAttributeId


class DoubleThrow(
    abilityItem: AbilityItem,
    attacker: Participant
) : Strike(
    abilityItem,
    attacker
) {

    override fun createCopyForPreview(): BattleAbilityItem {
        return DoubleThrow(abilityItem.copy(isPreview = true), attacker)
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

                Enemy protection:                   ${String.format("%3d", target.character.getCalculatedTotalProtection())} %
                Damage to inflict:                  ${String.format("%3d", calculateDamageForVisual().minusProtection())} [BLUE]x2[BLACK]
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
                Damage:  ${String.format("%3d", calculateDamageForVisual().minusProtection())} [BLUE]x2[BLACK]
                Crit:    ${String.format("%3d", calculateCriticalHitPercentage())} %
            """.trimIndent().trimMargin()
            } ?: createNoWeaponMessage()

        }
    }

    override fun handle(): List<AttackData> {
        val attackDataList = mutableListOf<AttackData>()

        handleSingleAttack(attackDataList)

        if (attackDataList[0].isTargetDead) return attackDataList
        if (attackDataList[0].attackerWeaponBrokeMessage != null) return attackDataList

        handleSingleAttack(attackDataList)

        return attackDataList
    }

}
