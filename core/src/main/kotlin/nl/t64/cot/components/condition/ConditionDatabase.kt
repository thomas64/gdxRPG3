package nl.t64.cot.components.condition

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.portal.Portal
import nl.t64.cot.components.quest.QuestState


object ConditionDatabase {

    private val conditions: Map<String, () -> Boolean> = mapOf(
        // @formatter:off
        "diplomat1"                 to { hasEnoughOfSkill(SkillItemId.DIPLOMAT,  1) },
        "barbarian1"                to { hasEnoughOfSkill(SkillItemId.BARBARIAN, 1) },
        "barbarian4"                to { hasEnoughOfSkill(SkillItemId.BARBARIAN, 4) },
        "warrior4"                  to { hasEnoughOfSkill(SkillItemId.WARRIOR,   4) },
        "druid1"                    to { hasEnoughOfSkill(SkillItemId.DRUID,     1) },
        "i_druid1"                  to { hasEnoughOfSkill(SkillItemId.DRUID,     1) },
        "ii_druid1"                 to { hasEnoughOfSkill(SkillItemId.DRUID,     1) },

        "!been_in_fairy_town"       to { !hasBeenInFairyTown },
        "been_in_fairy_town"        to { hasBeenInFairyTown },
        "defeated_orc_guards"       to { hasDefeatedOrcGuards },
        "i_!starting_spells"        to { !hasStartingSpells },
        "i_starting_spells"         to { hasStartingSpells },
        "!talked_to_lennor"         to { hasNotYetTalkedToLennorFirstCycle },
        "!black_asked_four"         to { !blackAskedFour },
        "black_asked_four"          to { blackAskedFour },
        "alone_in_party"            to { isAloneInParty },
        "fairy_portal_active"       to { gameData.portals.isActivated(Portal.HONEYWOOD_GREAT_TREE.name) },
        "i_!fairy_portal_active"    to { isPortalFairyInactiveAndPortalHoneywoodActive }
        // @formatter:on
    )

    fun isMeetingConditions(conditionIds: List<String?>, questId: String? = null): Boolean {
        return when {
            conditionIds.isEmpty() -> true
            else -> conditionIds.map { isMeetingCondition(it!!, questId) }.all { it }
        }
    }

    private fun isMeetingCondition(conditionId: String, questId: String?): Boolean {
        return if (conditionId.contains("_q_")) {
            ConditionConverter.isMeetingQuestCondition(conditionId, questId)
        } else if (conditionId.contains("_item_") && conditionId.contains("_n_")) {
            ConditionConverter.isMeetingItemCondition(conditionId)
        } else if (conditionId.contains("_time_")) {
            ConditionConverter.isMeetingTimeCondition(conditionId)
        } else if (conditionId.contains("heard")) {
            ConditionConverter.isMeetingConversationCondition(conditionId, questId!!)
        } else {
            conditions[conditionId]!!.invoke()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private val hasBeenInFairyTown get() = hasEventPlayed("enter_great_tree")
    private val hasDefeatedOrcGuards get() = isBattleWon("quest_orc_guards")
    private val hasStartingSpells get() = hasAnySpell("mozes")
    private val hasNotYetTalkedToLennorFirstCycle
        get() = "quest_helping_horse" hasResetState QuestState.UNKNOWN
            && "quest_helping_horse" hasCurrentPhraseId "1"
    private val blackAskedFour
        get() = isTargetAlternateUsed("quest_get_tow_rope", "13") // "_13_"
            || "quest_get_horseshoes" hasCurrentPhraseId "200"
    private val isAloneInParty get() = hasAmountOfPartyMembers(1)
    private val isPortalFairyInactiveAndPortalHoneywoodActive
        get() = !gameData.portals.isActivated(Portal.HONEYWOOD_GREAT_TREE.name)
            && gameData.portals.isActivated(Portal.HONEYWOOD_HOUSE_ELDER_B2.name)

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun hasEnoughOfSkill(skillItemId: SkillItemId, rank: Int): Boolean =
        gameData.party.hasEnoughOfSkill(skillItemId, rank)

    private fun hasAnySpell(heroId: String): Boolean =
        gameData.party.getCertainHero(heroId).getAllSpells().isNotEmpty()

    private fun hasAverageXpOf(requestedXp: Int): Boolean =
        gameData.party.getAverageXp() >= requestedXp

    private fun isTargetAlternateUsed(questId: String, questTaskId: String): Boolean =
        gameData.quests.getQuestById(questId).tasks[questTaskId]!!.isTargetAlternateUsed

    private fun hasEventPlayed(eventId: String): Boolean =
        gameData.events.hasEventPlayed(eventId)

    private fun isBattleWon(battleId: String): Boolean =
        gameData.battles.isBattleWon(battleId)

    private fun hasAmountOfPartyMembers(amount: Int): Boolean =
        gameData.party.size == amount

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private infix fun String.hasOneOfBothStatesEqualOrHigherThan(questState: QuestState): Boolean =
        gameData.quests.getQuestById(this).isOneOfBothStatesEqualOrHigherThan(questState)

    private infix fun String.hasResetState(questState: QuestState): Boolean =
        gameData.quests.getQuestById(this).resetState == questState

    private infix fun String.hasCurrentPhraseId(currentPhraseId: String): Boolean =
        gameData.conversations.getConversationById(this).currentPhraseId == currentPhraseId

}
