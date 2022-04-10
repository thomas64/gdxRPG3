package nl.t64.cot.components.conversation

import nl.t64.cot.ConfigDataLoader


class ConversationContainer {

    private val conversations: Map<String, ConversationGraph> = ConfigDataLoader.createConversations()

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
