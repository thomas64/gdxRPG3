package nl.t64.cot.components.condition

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.quest.QuestState


object ConditionDatabase {

    private val conditions: Map<String, () -> Boolean> = mapOf(

        // @formatter:off
        "3_starting_potions"        to { hasStartingPotions },
        "grace_ribbon"              to { hasGraceRibbon },
        "first_equipment_item"      to { gotFirstEquipmentItem },
        "i_!know_about_grace"       to { !doesKnowAboutGrace },
        "i_know_about_grace"        to { doesKnowAboutGrace },
        "!been_in_fairy_town"       to { !hasBeenInFairyTown },
        "been_in_fairy_town"        to { hasBeenInFairyTown },
        "i_scroll_of_orc_obedience" to { hasScrollOfOrcObedience },
        "diplomat4"                 to { hasDiplomat4 },
        "i_druid1"                  to { hasDruid1 },
        "i_!druid1"                 to { !hasDruid1 },
        "level10"                   to { hasLevel10 },
        "defeated_orc_guards"       to { hasDefeatedOrcGuards },
        "!talked_to_lennor"         to { hasNotYetTalkedToLennorFirstCycle },
        "herb6"                     to { herb6 },
        "blue_jelly1"               to { blueJelly1 },
        "horse_medicine1"           to { horseMedicine1 },
        "metal2"                    to { metal2 },
        "horseshoe4"                to { horseshoe4 },
        "alone_in_party"            to { isAloneInParty }
        // @formatter:on

    )

    fun isMeetingConditions(conditionIds: List<String?>, questId: String? = null): Boolean {
        return when {
            conditionIds.isEmpty() -> true
            conditionIds.all { it!!.contains("_q_") } -> conditionIds.all { ConditionConverter.isMeetingQuestCondition(it!!, questId) }
            else -> conditionIds.all { conditions[it]!!.invoke() }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private val hasStartingPotions get() = hasEnoughOfItem("healing_potion", 3)
    private val hasGraceRibbon get() = hasEnoughOfItem("grace_ribbon", 1)
    private val gotFirstEquipmentItem get() = hasEnoughOfOneOfTheseItems("basic_light_helmet")
    private val doesKnowAboutGrace
        get() = isOneOfBothStatesEqualOrHigher("quest_orc_guards", QuestState.ACCEPTED)
                || isOneOfBothStatesEqualOrHigher("quest_mother_fairy", QuestState.ACCEPTED)
    private val hasBeenInFairyTown get() = hasEventPlayed("find_great_tree")
    private val hasScrollOfOrcObedience get() = hasEnoughOfItem("scroll_of_orc_obedience", 1)
    private val hasDiplomat4 get() = hasEnoughOfSkill(SkillItemId.DIPLOMAT, 4)
    private val hasDruid1 get() = hasEnoughOfSkill(SkillItemId.DRUID, 1)
    private val hasLevel10 get() = hasAverageLevelOf(10)
    private val hasDefeatedOrcGuards get() = isBattleWon("quest_orc_guards")
    private val hasNotYetTalkedToLennorFirstCycle
        get() = isQuestResetStateEqual("quest_a_helping_horse", QuestState.UNKNOWN)
                && isCurrentPhraseId("quest_a_helping_horse", "1")
    private val herb6 get() = hasEnoughOfItem("herb", 6)
    private val blueJelly1 get() = hasEnoughOfItem("blue_jelly", 1)
    private val horseMedicine1 get() = hasEnoughOfItem("horse_medicine", 1)
    private val metal2 get() = hasEnoughOfItem("metal", 2)
    private val horseshoe4 get() = hasEnoughOfItem("horseshoe", 4)
    private val isAloneInParty get() = hasAmountOfPartyMembers(1)

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun hasEnoughOfSkill(skillItemId: SkillItemId, rank: Int): Boolean =
        gameData.party.hasEnoughOfSkill(skillItemId, rank)

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
