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
            .map { it.possibleSetJump() }
            .map { it.possibleSetHeard() }
            .toTypedArray()
    }

    fun resetChoiceHistory() {
        phrases.values
            .flatMap { it.choices }
            .forEach { it.resetHistory() }
    }

    private fun ConversationChoice.possibleSetJump(): ConversationChoice {
        if (this.containsAlternativeNextId()) {
            return this.getCopyOfChoiceWithAltNextId()
        }
        return this.apply { this.setJumpToAltEnabled?.let { isJumpToAltEnabled = it } }
    }

    private fun ConversationChoice.containsAlternativeNextId(): Boolean {
        return isJumpToAltEnabled && this.altNextId != null
    }

    private fun ConversationChoice.possibleSetHeard(): ConversationChoice {
        return this.apply { this.setHeard?.let { isPartHeard = it } }
    }

}
