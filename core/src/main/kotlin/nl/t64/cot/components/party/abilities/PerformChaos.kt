package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.screens.battle.BattleUtils


class PerformChaos(
    abilityItem: AbilityItem,
    attacker: Participant
) : PerformingAbilityItem(
    abilityItem,
    attacker
) {

    override fun createPreviewMessage(): String {
        return """
            $name

            Effect:
            Penalty to hit to all enemies, as long as you are performing.
            (Exact penalty depends on their own chance to hit.)
        """.trimIndent().trimMargin()
    }

    override fun handle(): List<AttackData> {
        val specialData = AttackData()
        specialData.attacker = attacker.character.name
        specialData.perform = AbilityItemId.PERFORM_CHAOS

        attacker.startPerforming(id)
        BattleUtils.applyEffectsOfPerformance()

        return listOf(specialData)
    }

}
