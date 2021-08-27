package nl.t64.cot.components.conversation

import com.badlogic.gdx.Gdx
import nl.t64.cot.Utils


private const val CONVERSATION_CONFIGS = "configs/conversations/"
private const val FILE_LIST = CONVERSATION_CONFIGS + "_files.txt"

class ConversationContainer {

    private val conversations: Map<String, ConversationGraph> = Gdx.files.internal(FILE_LIST).readString()
        .split(System.lineSeparator())
        .map { Gdx.files.internal(CONVERSATION_CONFIGS + it).readString() }
        .map { Utils.readValue<ConversationGraph>(it) }
        .flatMap { it.toList() }
        .toMap()

    fun getConversationById(conversationId: String): ConversationGraph {
        return conversations[conversationId]!!
    }

    fun createPhraseIdContainer(): PhraseIdContainer {
        val phraseIdContainer = PhraseIdContainer()
        conversations.forEach { phraseIdContainer.setPhraseId(it.key, it.value.currentPhraseId) }
        return phraseIdContainer
    }

    fun setCurrentPhraseIds(phraseIdContainer: PhraseIdContainer) {
        conversations.forEach { it.value.currentPhraseId = phraseIdContainer.getPhraseId(it.key) }
    }

}
