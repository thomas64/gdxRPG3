package nl.t64.cot.components.event

import com.badlogic.gdx.scenes.scene2d.Stage
import com.fasterxml.jackson.annotation.JsonProperty
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playBgm
import nl.t64.cot.audio.playSe
import nl.t64.cot.audio.stopAllSe
import nl.t64.cot.components.condition.ConditionDatabase
import nl.t64.cot.screens.dialog.MessageDialog


data class EventProgress(
    val hasPlayed: Boolean = false,
    val isRepeated: Boolean = false
)

class Event(
    private val type: String = "",
    @JsonProperty("condition")
    private val conditions: List<String> = emptyList(),
    private val conversationId: String? = null,
    private val entityId: String? = null,
    private val text: List<String> = emptyList(),
    private val doesRepeat: Boolean = false,
) {
    private var isRepeated: Boolean = false
    var hasPlayed: Boolean = false

    fun toProgress(): EventProgress {
        return EventProgress(hasPlayed, isRepeated)
    }

    fun applyProgress(progress: EventProgress) {
        hasPlayed = progress.hasPlayed
        isRepeated = progress.isRepeated
    }

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

    fun getReplacedText(): String {
        return TextReplacer.replace(text)
    }

    private fun isMeetingCondition(): Boolean {
        return ConditionDatabase.isMeetingConditions(conditions, conversationId)
    }

    private fun start(stage: Stage?) {
        val replacedText: String = getReplacedText()
        when {
            type == "tutorial" && preferenceManager.isTutorialOn && stage != null -> {
                MessageDialog(replacedText).show(stage, AudioEvent.SE_CONVERSATION_NEXT, 3f)
            }

            type == "tutorial" && preferenceManager.isTutorialOn && stage == null -> {
                worldScreen.showMessageDialog(replacedText)
            }

            type == "tutorial" && preferenceManager.isTutorialOn.not() -> {
                // Do nothing, tutorial is off.
            }

            type == "conversation" -> {
                worldScreen.showConversationDialogFromEvent(conversationId!!, entityId!!)
            }

            type == "messagebox" -> {
                worldScreen.showMessageDialog(replacedText)
            }

            type == "stop_bgm" -> {
                audioManager.fadeBgmInThread()
            }

            type == "start_bgm" -> {
                playBgm(mapManager.currentMap.bgm)
            }

            type == "play_se" -> {
                stopAllSe()
                playSe(AudioEvent.SE_REWARD)
            }

            else -> {
                throw IllegalArgumentException("Event does not recognize type: '$type'.")
            }
        }
    }

}
