package nl.t64.cot.components.conversation

import nl.t64.cot.resources.ConfigDataLoader


// the phrase ids are wrapped in this class on purpose. libGDX writes a bare map without the type of
// its values, and a phrase id like "10" then reads back as a number. as a field it does keep its type.
data class ConversationProgress(
    val phraseIds: Map<String, String> = emptyMap()
)

class ConversationContainer {

    private val conversations: Map<String, ConversationGraph> = ConfigDataLoader.createConversations()

    fun getConversationById(conversationId: String): ConversationGraph {
        return conversations[conversationId]!!
    }

    // a conversation that is still at its starting phrase is not stored. a fresh container already
    // has all of them at that phrase, so only the ones in the save file have to be set.
    fun toProgress(): ConversationProgress {
        return ConversationProgress(conversations
                                        .filterNot { (_, conversation) -> conversation.isAtDefaultStartingPhrase() }
                                        .mapValues { (_, conversation) -> conversation.currentPhraseId })
    }

    fun applyProgress(progress: ConversationProgress) {
        conversations.forEach { (conversationId, conversation) ->
            progress.phraseIds[conversationId]?.let { conversation.currentPhraseId = it }
        }
    }

    fun reset() {
        conversations
            //.filterNot { it.key == "fairy_welcome" } // Example of not resetting a conversation.
            // edit: do not use it this way, use a property boolean in a graph to determine if it should be reset or not.
            .forEach { it.value.reset() }
    }

}
