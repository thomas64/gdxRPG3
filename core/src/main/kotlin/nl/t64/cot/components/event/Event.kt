package nl.t64.cot.components.event

import com.badlogic.gdx.scenes.scene2d.Stage
import com.fasterxml.jackson.annotation.JsonProperty
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playBgm
import nl.t64.cot.components.condition.ConditionDatabase
import nl.t64.cot.screens.inventory.messagedialog.MessageDialog


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

    fun possibleStart(stage: Stage? = null) {
        if ((!hasPlayed && isMeetingCondition())
            || (hasPlayed && doesRepeat && !isRepeated && isMeetingCondition())
        ) {
            isRepeated = true
            hasPlayed = true
            start(stage)
        }
    }

    fun resetRepeat() {
        isRepeated = false
    }

    private fun isMeetingCondition(): Boolean {
        return ConditionDatabase.isMeetingConditions(conditionIds, conversationId)
    }

    private fun start(stage: Stage?) {
        when {
            stage != null -> MessageDialog(TextReplacer.replace(text)).show(stage, AudioEvent.SE_CONVERSATION_NEXT)
            type == "conversation" -> worldScreen.showConversationDialogFromEvent(conversationId!!, entityId!!)
            type == "messagebox" -> worldScreen.showMessageDialog(TextReplacer.replace(text))
            type == "stop_bgm" -> audioManager.fadeBgmInThread()
            type == "start_bgm" -> playBgm(mapManager.currentMap.bgm)
            else -> throw IllegalArgumentException("Event does not recognize type: '$type'.")
        }
    }

}
