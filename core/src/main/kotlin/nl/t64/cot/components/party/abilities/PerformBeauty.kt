package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.screens.battle.BattleUtils


class PerformBeauty(
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
            Bonus to hit for all allies,
            as long as you are performing.
        """.trimIndent().trimMargin()
    }

    override fun handle(): List<AttackData> {
        val specialData = AttackData()
        specialData.attacker = attacker.character.name
        specialData.perform = AbilityItemId.PERFORM_BEAUTY

        attacker.startPerforming(id)
        BattleUtils.applyEffectsOfPerformance()

        return listOf(specialData)
    }

}
