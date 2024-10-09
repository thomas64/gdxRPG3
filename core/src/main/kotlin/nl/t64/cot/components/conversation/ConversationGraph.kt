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

    fun reset() {
        currentPhraseId = DEFAULT_STARTING_PHRASE_ID
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
        return phrases[currentPhraseId]!!.getChoices(currentPhraseId).toTypedArray()
    }

    fun clearChosenAnswersHistory() {
        phrases.values
            .flatMap { it.choices }
            .forEach { it.resetHistory() }
    }

}
