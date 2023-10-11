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
        val amount: Int = getTaskIdOrAmount("_n_", conditionId)
        val inventoryItemId: String = conditionId.substringAfter("_item_")
        return doesInventoryContain(conditionId, inventoryItemId, amount)
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
        val startIndex: Int = conditionId.indexOf(prefix) + prefix.length
        val endIndex: Int = conditionId.substring(startIndex).indexOf("_") + startIndex
        return conditionId.substring(startIndex, endIndex).toInt()
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

    private fun doesInventoryContain(conditionId: String, inventoryItemId: String, amount: Int): Boolean {
        return when {
            conditionId.contains("_==_") -> gameData.inventory.hasExactlyAmountOfItem(inventoryItemId, amount)
            conditionId.contains("_>=_") -> gameData.inventory.hasEnoughOfItem(inventoryItemId, amount)
            else -> throw IllegalArgumentException("No defined operator found.")
        }
    }

}
