package nl.t64.cot.components.conversation

import com.fasterxml.jackson.annotation.JsonProperty


private const val DEFAULT_STARTING_PHRASE_ID = "1"

data class ConversationGraph(
    private val id: String = "",
    @JsonProperty("name")
    val npcName: String = "",
    val phrases: Map<String, ConversationPhrase> = emptyMap()
) {
    var currentPhraseId: String = DEFAULT_STARTING_PHRASE_ID
    private var isJumpToAltEnabled: Boolean = false
    var isPartHeard: Boolean = false

    fun reset() {
        currentPhraseId = DEFAULT_STARTING_PHRASE_ID
        isJumpToAltEnabled = false
        isPartHeard = false
    }

    fun initId() {
        phrases.forEach { it.value.initId(id) }
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
        return phrases[currentPhraseId]!!
            .getChoices(currentPhraseId)
            .map { it.possibleConvert() }
            .map { it.possibleSetHeard() }
            .toTypedArray()
    }

    fun resetChoiceHistory() {
        phrases.values.forEach { it.resetChoiceHistory() }
    }

    private fun ConversationChoice.possibleConvert(): ConversationChoice {
        if (this.containsAlternativeNextId()) {
            return this.getCopyOfChoiceWithAltNextId()
        }
        this.setJumpToAltEnabled?.let { isJumpToAltEnabled = it }
        return this
    }

    private fun ConversationChoice.containsAlternativeNextId(): Boolean {
        return isJumpToAltEnabled && this.altNextId != null
    }

    private fun ConversationChoice.possibleSetHeard(): ConversationChoice {
        if (this.setHeard == true) {
            isPartHeard = true
        }
        return this
    }

}
