package nl.t64.cot.components.event

import com.fasterxml.jackson.annotation.JsonProperty
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.components.condition.ConditionDatabase


class Event(
    private val type: String = "",
    @JsonProperty(value = "condition")
    val conditionIds: List<String> = emptyList(),
    val conversationId: String? = null,
    val entityId: String? = null,
    val text: List<String> = emptyList(),
    private val doesRepeat: Boolean = false
) {
    var hasPlayed: Boolean = false

    fun possibleStart() {
        if ((!hasPlayed && isMeetingCondition())
            || (hasPlayed && doesRepeat && isMeetingCondition())
        ) {
            hasPlayed = true
            start()
        }
    }

    private fun isMeetingCondition(): Boolean {
        return ConditionDatabase.isMeetingConditions(conditionIds)
    }

    private fun start() {
        when (type) {
            "conversation" -> brokerManager.componentObservers.notifyShowConversationDialog(conversationId!!, entityId!!)
            "messagebox" -> brokerManager.componentObservers.notifyShowMessageDialog(TextReplacer.replace(text))
            else -> throw IllegalArgumentException("Event does not recognize type: '$type'.")
        }
    }

}
