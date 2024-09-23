package nl.t64.cot.components.conversation

import nl.t64.cot.resources.ConfigDataLoader


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

    fun reset() {
        conversations
            //.filterNot { it.key == "fairy_welcome" } // Example of not resetting a conversation.
            .forEach { it.value.reset() }
    }

}
