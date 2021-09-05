package nl.t64.cot.components.conversation


class ConversationPhrase(
    val face: String = "",
    val text: List<String> = emptyList(),   // mandatory in Json file
    val choices: List<ConversationChoice> = emptyList()
) {
    fun getChoices(currentPhraseId: String): List<ConversationChoice> {
        return if (choices.isEmpty()) {
            createArrowChoiceThatPointsToNextPhraseId(currentPhraseId)
        } else {
            getVisibleChoices()
        }
    }

    private fun createArrowChoiceThatPointsToNextPhraseId(currentPhraseId: String): List<ConversationChoice> {
        val nextId = (currentPhraseId.toInt() + 1).toString()
        return listOf(ConversationChoice(nextId = nextId))
    }

    private fun getVisibleChoices(): List<ConversationChoice> = choices.filter { it.isVisible() }

}
