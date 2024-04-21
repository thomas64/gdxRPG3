package nl.t64.cot.components.event

import com.fasterxml.jackson.annotation.JsonProperty
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.components.condition.ConditionDatabase


class Event(
    private val type: String = "",
    @JsonProperty(value = "condition")
    val conditionIds: List<String> = emptyList(),
    val conversationId: String? = null,
    val entityId: String? = null,
    val text: List<String> = emptyList(),
    private val doesRepeat: Boolean = false,
    private var isRepeated: Boolean = false
) {
    var hasPlayed: Boolean = false

    fun possibleStart() {
        if ((!hasPlayed && isMeetingCondition())
            || (hasPlayed && doesRepeat && !isRepeated && isMeetingCondition())
        ) {
            isRepeated = true
            hasPlayed = true
            start()
        }
    }

    fun resetRepeat() {
        isRepeated = false
    }

    private fun isMeetingCondition(): Boolean {
        return ConditionDatabase.isMeetingConditions(conditionIds, conversationId)
    }

    private fun start() {
        when (type) {
            "conversation" -> worldScreen.showConversationDialogFromEvent(conversationId!!, entityId!!)
            "messagebox" -> worldScreen.showMessageDialog(TextReplacer.replace(text))
            else -> throw IllegalArgumentException("Event does not recognize type: '$type'.")
        }
    }

}
