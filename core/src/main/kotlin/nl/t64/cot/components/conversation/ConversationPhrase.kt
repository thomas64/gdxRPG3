package nl.t64.cot.components.conversation

import nl.t64.cot.constants.Constant


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
        } else if (choices.none { it.isMeetingCondition() }) {
            createArrowChoiceThatPointsToNoConditionsPhraseId()
        } else {
            getVisibleChoices()
        }
    }

    fun resetChoiceHistory() {
        choices.forEach { it.hasBeenSelectedEarlier = false }
    }

    private fun createArrowChoiceThatPointsToNextPhraseId(currentPhraseId: String): List<ConversationChoice> {
        val nextId = (currentPhraseId.toInt() + 1).toString()
        val choice = ConversationChoice(nextId = nextId).apply { initId(conversationId) }
        return listOf(choice)
    }

    private fun createArrowChoiceThatPointsToNoConditionsPhraseId(): List<ConversationChoice> {
        val choice = ConversationChoice(nextId = Constant.PHRASE_ID_NO_CONDITIONS).apply { initId(conversationId) }
        return listOf(choice)
    }

    private fun getVisibleChoices(): List<ConversationChoice> = choices.filter { it.isVisible() }

}
