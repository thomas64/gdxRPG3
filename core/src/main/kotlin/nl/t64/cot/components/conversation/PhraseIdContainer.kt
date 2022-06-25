package nl.t64.cot.components.conversation


class PhraseIdContainer {

    private val currentPhraseIds: MutableMap<String, String> = HashMap()
    val size: Int get() = currentPhraseIds.size

    fun getPhraseId(conversationId: String): String {
        return currentPhraseIds[conversationId] ?: "1"
    }

    fun setPhraseId(conversationId: String, phraseId: String) {
        currentPhraseIds[conversationId] = phraseId
    }

}
