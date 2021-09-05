package nl.t64.cot.components.conversation

import com.fasterxml.jackson.annotation.JsonProperty
import nl.t64.cot.components.condition.ConditionDatabase


private const val DEFAULT_ANSWER_TEXT = "->"
private const val DEFAULT_NEXT_ID = "1"
private val DEFAULT_CONVERSATION_COMMAND = ConversationCommand.NONE
private val DEFAULT_CONDITION: List<String> = emptyList()
private const val INVISIBLE_PREFIX = "i_"

class ConversationChoice(
    val text: String = DEFAULT_ANSWER_TEXT,
    val nextId: String = DEFAULT_NEXT_ID,
    val command: ConversationCommand = DEFAULT_CONVERSATION_COMMAND,
    @JsonProperty("condition")
    val conditionIds: List<String> = DEFAULT_CONDITION
) {

    override fun toString(): String {
        return text
    }

    fun isVisible(): Boolean {
        return conditionIds.none { it.startsWith(INVISIBLE_PREFIX) } || isMeetingCondition()
    }

    fun isMeetingCondition(): Boolean {
        return ConditionDatabase.isMeetingConditions(conditionIds)
    }

}
