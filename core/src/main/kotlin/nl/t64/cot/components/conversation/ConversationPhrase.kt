package nl.t64.cot.components.conversation


class ConversationPhrase(
    val face: String = "",
    val text: List<String> = emptyList(),   // mandatory in Json file
    val choices: List<ConversationChoice> = emptyList()
) {
    private lateinit var conversationId: String

    fun initId(id: String) {
        conversationId = id
        choices.forEach { it.initId(id) }
    }

    fun getChoices(currentPhraseId: String): List<ConversationChoice> {
        return if (choices.isEmpty()) {
            createArrowChoiceThatPointsToNextPhraseId(currentPhraseId)
        } else {
            getVisibleChoices()
        }
    }

    private fun createArrowChoiceThatPointsToNextPhraseId(currentPhraseId: String): List<ConversationChoice> {
        val nextId = (currentPhraseId.toInt() + 1).toString()
        val choice = ConversationChoice(nextId = nextId).apply { initId(conversationId) }
        return listOf(choice)
    }

    private fun getVisibleChoices(): List<ConversationChoice> = choices.filter { it.isVisible() }

}
