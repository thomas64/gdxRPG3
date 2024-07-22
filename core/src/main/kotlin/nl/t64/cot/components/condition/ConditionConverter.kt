package nl.t64.cot.components.condition

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.components.quest.QuestState


object ConditionConverter {

    fun isMeetingQuestCondition(conditionId: String, questId: String?): Boolean {
        val questGraph: QuestGraph = getQuestGraph(conditionId, questId)
        if (conditionId.contains("""_t[acn]_""".toRegex())) {
            return isTaskInState(conditionId, questGraph)
        }
        val questState: List<QuestState> = getQuestState(conditionId, questGraph)
        val conditionState: QuestState = getConditionState(conditionId)
        return isQuestInState(conditionId, questState, conditionState)
    }

    fun isMeetingItemCondition(conditionId: String): Boolean {
        val requestedAmount: Int = getTaskIdOrAmount("_n_", conditionId)
        val inventoryItemId: String = conditionId.substringAfter("_item_")
        return doesInventoryAndEquipmentContain(conditionId, inventoryItemId, requestedAmount)
    }

    fun isMeetingTimeCondition(conditionId: String): Boolean {
        return when {
            conditionId.contains("_at_") -> {
                val time = getOneTimeUnit("_at_", conditionId)
                gameData.clock.isCurrentTimeAt(time)
            }
            conditionId.contains("_between_") -> {
                val (startTime, endTime) = getTwoTimeUnits("_between_", conditionId)
                gameData.clock.isCurrentTimeInBetween(startTime, endTime)
            }
            conditionId.contains("_before_") -> {
                val time = getOneTimeUnit("_before_", conditionId)
                gameData.clock.isCurrentTimeBefore(time)
            }
            conditionId.contains("_after_") -> {
                val time = getOneTimeUnit("_after_", conditionId)
                gameData.clock.isCurrentTimeAfter(time)
            }
            else -> throw IllegalArgumentException("No defined state found.")
        }
    }

    private fun getQuestGraph(conditionId: String, questId: String?): QuestGraph {
        return when {
            conditionId.contains("_q_this") -> gameData.quests.getQuestById(questId!!)
            conditionId.contains("_q_") -> gameData.quests.getQuestById(conditionId.substringAfter("_q_"))
            else -> throw IllegalArgumentException("No defined quest found.")
        }
    }

    private fun isTaskInState(conditionId: String, questGraph: QuestGraph): Boolean {
        return when {
            conditionId.contains("_ta_") -> isTaskCompleteAndNextTaskNotYet(conditionId, questGraph)
            conditionId.contains("_tc_") -> isTaskComplete("_tc_", conditionId, questGraph)
            conditionId.contains("_tn_") -> !isTaskComplete("_tn_", conditionId, questGraph)
            else -> throw IllegalArgumentException("No defined state found.")
        }
    }

    private fun isTaskCompleteAndNextTaskNotYet(conditionId: String, questGraph: QuestGraph): Boolean {
        val taskId: Int = getTaskIdOrAmount("_ta_", conditionId)
        var nextTaskId: Int = taskId + 1
        while (!questGraph.tasks.containsKey(nextTaskId.toString())) nextTaskId += 1
        return questGraph.isTaskComplete(taskId.toString())
            && !questGraph.isTaskComplete(nextTaskId.toString())
    }

    private fun isTaskComplete(prefix: String, conditionId: String, questGraph: QuestGraph): Boolean {
        val taskId: Int = getTaskIdOrAmount(prefix, conditionId)
        return questGraph.isTaskComplete(taskId.toString())
    }

    private fun getTaskIdOrAmount(prefix: String, conditionId: String): Int {
        val (startIndex, endIndex) = getStartAndEndIndex(prefix, conditionId)
        return conditionId.substring(startIndex, endIndex).toInt()
    }

    private fun getOneTimeUnit(prefix: String, conditionId: String): String {
        return conditionId.substringAfter(prefix)
    }

    private fun getTwoTimeUnits(prefix: String, conditionId: String): Pair<String, String> {
        val (startIndex, endIndex) = getStartAndEndIndex(prefix, conditionId)
        val startTime: String = conditionId.substring(startIndex, endIndex)
        val endTime: String = conditionId.substringAfter("_and_")
        return startTime to endTime
    }

    private fun getStartAndEndIndex(prefix: String, conditionId: String): Pair<Int, Int> {
        val startIndex: Int = conditionId.indexOf(prefix) + prefix.length
        val endIndex: Int = conditionId.substring(startIndex).indexOf("_") + startIndex
        return startIndex to endIndex
    }

    private fun getQuestState(conditionId: String, questGraph: QuestGraph): List<QuestState> {
        return when {
            conditionId.contains("_r_") -> listOf(questGraph.resetState)
            conditionId.contains("_c_") -> listOf(questGraph.currentState)
            conditionId.contains("_rc_") -> listOf(questGraph.resetState, questGraph.currentState)
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

    private fun isQuestInState(conditionId: String, questState: List<QuestState>, conditionState: QuestState): Boolean {
        return when {
            conditionId.contains("_!=_") -> questState.all { it != conditionState }
            conditionId.contains("_==_") -> questState.any { it == conditionState }
            conditionId.contains("_===_") -> questState.all { it == conditionState }
            conditionId.contains("_<=_") -> questState.any { it.isEqualOrLowerThan(conditionState) }
            conditionId.contains("_>=_") -> questState.any { it.isEqualOrHigherThan(conditionState) }
            else -> throw IllegalArgumentException("No defined operator found.")
        }
    }

    private fun doesInventoryAndEquipmentContain(conditionId: String,
                                                 inventoryItemId: String,
                                                 requestedAmount: Int): Boolean {
        val combinedAmount: Int = gameData.inventory.getTotalOfItem(inventoryItemId) + gameData.party.getAmountOfItemInEquipment(inventoryItemId)
        return when {
            conditionId.contains("_==_") -> combinedAmount == requestedAmount
            conditionId.contains("_>=_") -> combinedAmount >= requestedAmount
            conditionId.contains("_<_") -> combinedAmount < requestedAmount
            else -> throw IllegalArgumentException("No defined operator found.")
        }
    }

}
