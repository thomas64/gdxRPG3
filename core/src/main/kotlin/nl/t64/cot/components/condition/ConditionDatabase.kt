package nl.t64.cot.components.condition

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.components.quest.QuestState


object ConditionDatabase {

    private val conditions: Map<String, () -> Boolean> = mapOf(

        Pair("3_starting_potions") { hasStartingPotions },
        Pair("grace_ribbon") { hasGraceRibbon },
        Pair("first_equipment_item") { gotFirstEquipmentItem },
        Pair("i_!know_about_grace") { !doesKnowAboutGrace },
        Pair("i_know_about_grace") { doesKnowAboutGrace },
        Pair("!been_in_fairy_town") { !hasBeenInFairyTown },
        Pair("been_in_fairy_town") { hasBeenInFairyTown },
        Pair("i_scroll_of_orc_obedience") { hasScrollOfOrcObedience },
        Pair("diplomat4") { hasDiplomat4 },
        Pair("i_druid1") { hasDruid1 },
        Pair("i_!druid1") { !hasDruid1 },
        Pair("level10") { hasLevel10 },
        Pair("defeated_orc_guards") { hasDefeatedOrcGuards },
        Pair("!talked_to_lennor") { hasNotYetTalkedToLennorFirstCycle },
        Pair("alone_in_party") { isAloneInParty }

    )

    fun isMeetingConditions(conditionIds: List<String?>, questId: String? = null): Boolean {
        return when {
            conditionIds.isEmpty() -> true
            conditionIds.all { it!!.contains("_q_") } -> conditionIds.all { isMeetingQuestCondition(it!!, questId) }
            else -> conditionIds.all { conditions[it]!!.invoke() }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun isMeetingQuestCondition(conditionId: String, questId: String?): Boolean {
        val questGraph: QuestGraph = getQuestGraph(conditionId, questId)
        if (conditionId.contains("_t_")) {
            return isTaskComplete(conditionId, questGraph)
        }
        val questState: QuestState = getQuestState(conditionId, questGraph)
        val conditionState: QuestState = getConditionState(conditionId)
        return isQuestInState(conditionId, questState, conditionState)
    }

    private fun getQuestGraph(conditionId: String, questId: String?): QuestGraph {
        return when {
            conditionId.contains("_q_this") -> gameData.quests.getQuestById(questId!!)
            conditionId.contains("_q_") -> gameData.quests.getQuestById(conditionId.substringAfter("_q_"))
            else -> throw IllegalArgumentException("No defined quest found.")
        }
    }

    private fun isTaskComplete(conditionId: String, questGraph: QuestGraph): Boolean {
        val startIndex = conditionId.indexOf("_t_")
        val taskId: Int = conditionId.substring(startIndex + 3, startIndex + 4).toInt()
        val nextTaskId: Int = taskId + 1
        return questGraph.isTaskComplete(taskId.toString())
                && !questGraph.isTaskComplete(nextTaskId.toString())
    }

    private fun getQuestState(conditionId: String, questGraph: QuestGraph): QuestState {
        return when {
            conditionId.contains("_c_") -> questGraph.currentState
            conditionId.contains("_r_") -> questGraph.resetState
            else -> throw IllegalArgumentException("No defined state found.")
        }
    }

    private fun getConditionState(conditionId: String): QuestState {
        return when {
            conditionId.contains("_u_") -> QuestState.UNKNOWN
            conditionId.contains("_k_") -> QuestState.KNOWN
            conditionId.contains("_a_") -> QuestState.ACCEPTED
            conditionId.contains("_f_") -> QuestState.FINISHED
            else -> throw IllegalArgumentException("No defined conditionState found.")
        }
    }

    private fun isQuestInState(conditionId: String, questState: QuestState, conditionState: QuestState): Boolean {
        return when {
            conditionId.contains("!=") -> questState != conditionState
            conditionId.contains("==") -> questState == conditionState
            conditionId.contains("<=") -> questState.isEqualOrLowerThan(conditionState)
            conditionId.contains(">=") -> questState.isEqualOrHigherThan(conditionState)
            else -> throw IllegalArgumentException("No defined operator found.")
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
    private val isAloneInParty: Boolean get() = hasAmountOfPartyMembers(1)

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
