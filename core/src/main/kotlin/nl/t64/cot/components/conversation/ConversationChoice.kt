package nl.t64.cot.components.conversation

import com.fasterxml.jackson.annotation.JsonProperty
import nl.t64.cot.components.condition.ConditionDatabase
import nl.t64.cot.constants.Constant


private const val DEFAULT_ANSWER_TEXT = "->"
private const val DEFAULT_NEXT_ID = "1"
private val DEFAULT_CONVERSATION_COMMAND = ConversationCommand.NONE
private const val INVISIBLE_PREFIX = "i_"
private const val INVERSE_INVISIBLE_PREFIX = "ii_"

class ConversationChoice(
    val text: String = DEFAULT_ANSWER_TEXT,
    val nextId: String = DEFAULT_NEXT_ID,
    val command: ConversationCommand = DEFAULT_CONVERSATION_COMMAND,
    val questId: String = "",
    @JsonProperty("condition")
    val conditions: List<String> = emptyList(),
    private val orElseId: String = ""
) {
    private lateinit var conversationId: String
    var hasBeenSelectedEarlier: Boolean = false

    fun initId(id: String) {
        conversationId = id
    }

    override fun toString(): String {
        return text
    }

    fun resetHistory() {
        hasBeenSelectedEarlier = false
    }

    fun isVisible(): Boolean {
        return isNotMeetingConditionWithDoubleII() || isMeetingConditionOrHasNoSingleI()
    }

    fun isMeetingCondition(): Boolean {
        return ConditionDatabase.isMeetingConditions(conditions, conversationId)
    }

    fun isDefault(): Boolean {
        return text == DEFAULT_ANSWER_TEXT
    }

    fun getAlternativeNextId(): String {
        return orElseId.ifEmpty { Constant.PHRASE_ID_NO_CONDITIONS }
    }

    private fun isNotMeetingConditionWithDoubleII(): Boolean {
        return conditions.any { it.startsWith(INVERSE_INVISIBLE_PREFIX) }
            && !isMeetingCondition()
    }

    private fun isMeetingConditionOrHasNoSingleI(): Boolean {
        return conditions.none { it.startsWith(INVERSE_INVISIBLE_PREFIX) }
            && (isMeetingCondition() || conditions.none { it.startsWith(INVISIBLE_PREFIX) })
    }

}
