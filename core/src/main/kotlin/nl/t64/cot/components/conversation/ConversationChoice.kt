package nl.t64.cot.components.conversation

import com.fasterxml.jackson.annotation.JsonProperty
import nl.t64.cot.components.condition.ConditionDatabase


private const val DEFAULT_ANSWER_TEXT = "->"
private const val DEFAULT_NEXT_ID = "1"
private val DEFAULT_CONVERSATION_COMMAND = ConversationCommand.NONE
private const val INVISIBLE_PREFIX = "i_"
private const val INVERSE_INVISIBLE_PREFIX = "ii_"

data class ConversationChoice(
    val text: String = DEFAULT_ANSWER_TEXT,
    val nextId: String = DEFAULT_NEXT_ID,
    val altNextId: String? = null,
    val setJumpToAltEnabled: Boolean? = null,
    val setHeard: Boolean? = null,
    val command: ConversationCommand = DEFAULT_CONVERSATION_COMMAND,
    @JsonProperty("condition")
    val conditionIds: List<String> = emptyList()
) {
    private lateinit var conversationId: String
    var hasBeenSelectedEarlier: Boolean = false

    fun initId(id: String) {
        conversationId = id
    }

    override fun toString(): String {
        return text
    }

    fun isVisible(): Boolean {
        return isNotMeetingConditionWithDoubleII() || isMeetingConditionOrHasNoSingleI()
    }

    fun isMeetingCondition(): Boolean {
        return ConditionDatabase.isMeetingConditions(conditionIds, conversationId)
    }

    fun isDefault(): Boolean {
        return text == DEFAULT_ANSWER_TEXT
    }

    fun getCopyOfChoiceWithAltNextId(): ConversationChoice {
        val copy = copy(nextId = altNextId!!)
        copy.initId(conversationId)
        return copy
    }

    private fun isNotMeetingConditionWithDoubleII(): Boolean {
        return conditionIds.any { it.startsWith(INVERSE_INVISIBLE_PREFIX) } && !isMeetingCondition()
    }

    private fun isMeetingConditionOrHasNoSingleI(): Boolean {
        return conditionIds.none { it.startsWith(INVERSE_INVISIBLE_PREFIX) } &&
            (isMeetingCondition() || conditionIds.none { it.startsWith(INVISIBLE_PREFIX) })
    }

}
