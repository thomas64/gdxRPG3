package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.Participant


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
        return currentWeapon?.let {
            """
                $name
                ${it.name} ${it.getDurabilityText()}
                ${createEffectiveMessage()}
                Mod hit: ${String.format("%3d", calculateHitPercentageCapped())} %
                Damage:  ${String.format("%3d", calculateDamage())} [BLUE]x2[BLACK]
                Crit:    ${String.format("%3d", calculateCriticalHitPercentage())} %
            """.trimIndent().trimMargin()
        } ?: createNoWeaponMessage()
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
