package nl.t64.cot.components.quest

import nl.t64.cot.ConfigDataLoader


class QuestContainer {

    private val quests: Map<String, QuestGraph> = ConfigDataLoader.createQuests()

    fun getAllKnownQuestsForVisual(): Array<QuestGraph> = quests.values
        .filter { !it.isHidden }
        .filter { !it.isSubQuest }
        .filter { it.isOneOfBothStatesEqualOrHigherThan(QuestState.KNOWN) }
        .sortedWith(compareBy({ it.isFailed }, { it.resetState }, { it.currentState }, { it.id }))
        .toTypedArray()

    fun reset() {
        quests.values.forEach { it.reset() }
    }

    fun updateFindItem() {
        quests.values.forEach { it.possibleSetFindItemTaskComplete() }
    }

    fun updateDeliverItem(conversationId: String) {
        quests.values.forEach { it.possibleSetDeliverItemTaskComplete(conversationId) }
    }

    fun contains(questId: String): Boolean = quests.containsKey(questId)
    fun getQuestById(questId: String): QuestGraph = quests[questId]!!

}
