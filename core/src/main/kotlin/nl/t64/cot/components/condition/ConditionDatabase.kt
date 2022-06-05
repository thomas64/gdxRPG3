package nl.t64.cot.components.condition

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.quest.QuestState


object ConditionDatabase {

    private val conditions: Map<String, () -> Boolean> = mapOf(
        // @formatter:off
        "3_starting_potions"        to { hasEnoughOfItem("healing_potion",          3) },
        "grace_ribbon"              to { hasEnoughOfItem("grace_ribbon",            1) },
        "i_scroll_of_orc_obedience" to { hasEnoughOfItem("scroll_of_orc_obedience", 1) },
        "i_herb6"                   to { hasEnoughOfItem("herb",                    6) },
        "i_blue_jelly1"             to { hasEnoughOfItem("blue_jelly",              1) },
        "i_horse_medicine1"         to { hasEnoughOfItem("horse_medicine",          1) },
        "gold1"                     to { hasEnoughOfItem("gold",                    1) },
        "metal2"                    to { hasEnoughOfItem("metal",                   2) },
        "i_horseshoe4"              to { hasEnoughOfItem("horseshoe",               4) },

        "diplomat4"                 to { hasEnoughOfSkill(SkillItemId.DIPLOMAT,                     4) },
        "i_druid1"                  to { hasEnoughOfSkill(SkillItemId.DRUID,                        1) },
        "i_druid2"                  to { hasEnoughOfSkill(SkillItemId.DRUID,                        2) },

        "level10"                   to { hasAverageLevelOf(                                 10) },

        "first_equipment_item"      to { gotFirstEquipmentItem },
        "i_!know_about_grace"       to { !doesKnowAboutGrace },
        "i_know_about_grace"        to { doesKnowAboutGrace },
        "!been_in_fairy_town"       to { !hasBeenInFairyTown },
        "been_in_fairy_town"        to { hasBeenInFairyTown },
        "defeated_orc_guards"       to { hasDefeatedOrcGuards },
        "i_!starting_spells"        to { !hasStartingSpells },
        "i_starting_spells"         to { hasStartingSpells },
        "!talked_to_lennor"         to { hasNotYetTalkedToLennorFirstCycle },
        "alone_in_party"            to { isAloneInParty }
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
        } else {
            conditions[conditionId]!!.invoke()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private val gotFirstEquipmentItem get() = hasEnoughOfOneOfTheseItems("basic_light_helmet")
    private val doesKnowAboutGrace
        get() = isOneOfBothStatesEqualOrHigher("quest_orc_guards", QuestState.ACCEPTED)
                || isOneOfBothStatesEqualOrHigher("quest_mother_fairy", QuestState.ACCEPTED)
    private val hasBeenInFairyTown get() = hasEventPlayed("find_great_tree")
    private val hasDefeatedOrcGuards get() = isBattleWon("quest_orc_guards")
    private val hasStartingSpells get() = hasAnySpell("mozes")
    private val hasNotYetTalkedToLennorFirstCycle
        get() = isQuestResetStateEqual("quest_a_helping_horse", QuestState.UNKNOWN)
                && isCurrentPhraseId("quest_a_helping_horse", "1")
    private val isAloneInParty get() = hasAmountOfPartyMembers(1)

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun hasEnoughOfSkill(skillItemId: SkillItemId, rank: Int): Boolean =
        gameData.party.hasEnoughOfSkill(skillItemId, rank)

    private fun hasAnySpell(heroId: String): Boolean =
        gameData.party.getCertainHero(heroId).getAllSpells().isNotEmpty()

    private fun hasAverageLevelOf(requestedLevel: Int): Boolean =
        gameData.party.getAverageLevel() >= requestedLevel

    private fun hasEnoughOfOneOfTheseItems(vararg inventoryItemIds: String): Boolean =
        inventoryItemIds.any { hasEnoughOfItem(it, 1) }

    private fun hasEnoughOfItem(inventoryItemId: String, amount: Int): Boolean =
        gameData.inventory.hasEnoughOfItem(inventoryItemId, amount)
                || gameData.party.hasItemInEquipment(inventoryItemId, amount)

    private fun isQuestResetStateEqual(questId: String, questState: QuestState): Boolean =
        gameData.quests.getQuestById(questId).resetState == questState

    private fun isOneOfBothStatesEqualOrHigher(questId: String, questState: QuestState): Boolean =
        gameData.quests.getQuestById(questId).isOneOfBothStatesEqualOrHigherThan(questState)

    private fun hasEventPlayed(eventId: String): Boolean =
        gameData.events.hasEventPlayed(eventId)

    private fun isBattleWon(battleId: String): Boolean =
        gameData.battles.isBattleWon(battleId)

    private fun isCurrentPhraseId(conversationId: String, currentPhraseId: String): Boolean =
        gameData.conversations.getConversationById(conversationId).currentPhraseId == currentPhraseId

    private fun hasAmountOfPartyMembers(amount: Int): Boolean =
        gameData.party.size == amount

}
