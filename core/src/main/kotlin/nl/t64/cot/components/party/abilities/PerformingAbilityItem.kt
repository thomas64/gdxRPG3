package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.Participant


abstract class PerformingAbilityItem(
    abilityItem: AbilityItem,
    attacker: Participant
) : BattleAbilityItem(
    abilityItem,
    attacker
) {

    override fun createCopyForPreview(): BattleAbilityItem {
        throw IllegalStateException("PerformingAbilityItem does not support preview copies.")
    }

    override fun handleSuccess(attackData: AttackData) {
        throw IllegalStateException("PerformingAbilityItem does not support handleSuccess.")
    }

}
