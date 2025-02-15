package nl.t64.cot.components.conversation


class ConversationPhrase(
    val face: String = "",
    val name: String = "",
    val text: List<String> = listOf(""),
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
        } else if (choices.size == 1 && !choices[0].isMeetingCondition()) {
            createArrowChoiceThatPointsToAlternativeNextIdFrom(choices[0])
        } else {
            getVisibleChoices()
        }
    }

    private fun createArrowChoiceThatPointsToNextPhraseId(currentPhraseId: String): List<ConversationChoice> {
        val nextId = (currentPhraseId.toInt() + 1).toString()
        val choice = ConversationChoice(nextId = nextId).apply { initId(conversationId) }
        return listOf(choice)
    }

    private fun createArrowChoiceThatPointsToAlternativeNextIdFrom(originalChoice: ConversationChoice): List<ConversationChoice> {
        val nextId: String = originalChoice.getAlternativeNextId()
        val newChoice = ConversationChoice(nextId = nextId).apply { initId(conversationId) }
        return listOf(newChoice)
    }

    private fun getVisibleChoices(): List<ConversationChoice> {
        return choices.filter { it.isVisible() }
    }

}
