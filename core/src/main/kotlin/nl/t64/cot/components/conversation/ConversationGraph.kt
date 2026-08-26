package nl.t64.cot.components.conversation

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import nl.t64.cot.components.condition.areAllTrue


private const val DEFAULT_STARTING_PHRASE_ID = "1"

@JsonIgnoreProperties(ignoreUnknown = true)
data class ConversationGraph(
    private val id: String = "",
    @JsonProperty("name")
    val npcName: String = "",
    private val doesReset: Boolean = true,
    val phrases: Map<String, ConversationPhrase> = emptyMap(),
    @JsonProperty("startAt")
    private val alternateStarts: List<AlternateStart> = emptyList()
) {
    var currentPhraseId: String = DEFAULT_STARTING_PHRASE_ID

    fun isAtDefaultStartingPhrase(): Boolean {
        return currentPhraseId == DEFAULT_STARTING_PHRASE_ID
    }

    fun reset() {
        if (doesReset) {
            currentPhraseId = DEFAULT_STARTING_PHRASE_ID
        }
    }

    fun initId() {
        phrases.forEach { it.value.initId(id) }
    }

    fun possibleSetAlternateStartingPhraseId() {
        if (currentPhraseId != DEFAULT_STARTING_PHRASE_ID) return

        currentPhraseId = alternateStarts
            .firstOrNull { it.conditions.areAllTrue(id) }
            ?.startId
            ?: DEFAULT_STARTING_PHRASE_ID
    }

    fun getCurrentFace(): String {
        return phrases[currentPhraseId]!!.face
    }

    fun getCurrentName(): String {
        return phrases[currentPhraseId]!!.name
    }

    fun getCurrentPhrase(): List<String> {
        return phrases[currentPhraseId]!!.text
    }

    fun getAssociatedChoices(): Array<ConversationChoice> {
        return phrases[currentPhraseId]!!.getChoices(currentPhraseId).toTypedArray()
    }

    fun clearChosenAnswersHistory() {
        phrases.values
            .flatMap { it.choices }
            .forEach { it.resetHistory() }
    }

}
