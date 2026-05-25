package nl.t64.cot.components.party.abilities

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.skills.SkillItemId


class ElementalAttack(
    abilityItem: AbilityItem,
    attacker: Participant
) : Strike(
    abilityItem,
    attacker
) {

    override fun handle(): List<AttackData> {
        val attackDataList = super.handle()
        attackDataList.forEach { it.magicAbility = abilityItem.id }
        return attackDataList
    }

    override fun createCopyForPreview(): BattleAbilityItem {
        return ElementalAttack(abilityItem.copy(isPreview = true), attacker)
    }

    override fun createPreviewMessage(): String {
        if (preferenceManager.isCombatDetailsOn) {

            return currentWeapon?.let {
                val weaponHit: Int = it.getAttributeOfCalcAttributeId(CalcAttributeId.BASE_HIT)
                val weaponDamage: Int = it.getAttributeOfCalcAttributeId(CalcAttributeId.DAMAGE)
                """
                $name (${abilityItem.requiredResource.title}: ${gameData.inventory.getTotalOfItem(abilityItem.requiredResource.title)})
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

                Enemy magic protection:             ${String.format("%3d", target.character.getCalculatedTotalMagicProtection())} %
                Damage to inflict:                  ${String.format("%3d", calculateDamageForVisual().minusProtection())}
                Damage to inflict if critical hit:  ${String.format("%3d", calculateCriticalDamageForVisual().minusProtection())}

            """.trimIndent().trimMargin()
            } ?: createNoWeaponMessageWithResource()

        } else {

            return currentWeapon?.let {
                """
                $name (${abilityItem.requiredResource.title}: ${gameData.inventory.getTotalOfItem(abilityItem.requiredResource.title)})
                ${it.name} ${it.getDurabilityText()}
                ${createEffectiveMessage()}
                Mod hit: ${String.format("%3d", calculateHitPercentageForVisual())} %
                Damage:  ${String.format("%3d", calculateDamageForVisual().minusProtection())}
                Crit:    ${String.format("%3d", calculateCriticalHitPercentage())} %
            """.trimIndent().trimMargin()
            } ?: createNoWeaponMessageWithResource()

        }
    }

    private fun createNoWeaponMessageWithResource(): String {
        return """
            $name (${abilityItem.requiredResource.title}: ${gameData.inventory.getTotalOfItem(abilityItem.requiredResource.title)})

            No weapon equipped.
            [FIREBRICK]Disadvantage![BLACK]
        """.trimIndent()
    }

    override fun isBlock(): Boolean {
        return false
    }

    override fun getDamageReductionPercentage(): Float {
        return target.character.getCalculatedTotalMagicProtection().toFloat()
    }

    override fun getCriticalHitSkillId(): SkillItemId {
        return SkillItemId.WIZARD
    }

}
