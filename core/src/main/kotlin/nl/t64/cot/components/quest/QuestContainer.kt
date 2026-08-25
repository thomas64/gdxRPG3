package nl.t64.cot.components.quest

import nl.t64.cot.resources.ConfigDataLoader


class QuestContainer {

    private val quests: MutableMap<String, QuestGraph> = ConfigDataLoader.createQuests().toMutableMap()

    fun updateOutdatedData() {
        val currentQuests: Map<String, QuestGraph> = ConfigDataLoader.createQuests()
        currentQuests
            .filterKeys { it !in quests }
            .forEach { quests[it.key] = it.value }
        quests.keys.retainAll(currentQuests.keys)
    }

    fun getAllKnownQuestsForVisual(): Array<QuestGraph> = quests.values
        .asSequence()
        .filterNot { it.isHidden }
        .filterNot { it.isSubQuest }
        .filterNot { it.isHiddenInQuestLog }
        .filter { it.isOneOfBothStatesEqualOrHigherThan(QuestState.KNOWN) }
        .sortedWith(compareBy({ it.resetState }, { it.isFailed }, { it.currentState }, { it.id }))
        .toList()
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

    fun updateDeliverItemAlternate(conversationId: String) {
        quests.values.forEach { it.possibleSetDeliverItemAlternateTaskComplete(conversationId) }
    }

    fun updateDeliverItemToShow(conversationId: String) {
        quests.values.forEach { it.possibleSetDeliverItemToShowTaskComplete(conversationId) }
    }

    fun updateTalkToPerson(conversationId: String) {
        quests.values.forEach { it.setTalkToPersonTaskComplete(conversationId) }
    }

    fun updateDeliverMessage(conversationId: String) {
        quests.values.forEach { it.possibleSetDeliverMessageTaskComplete(conversationId) }
    }

    fun contains(questId: String): Boolean {
        return quests.containsKey(questId)
    }

    fun getSingleQuestByTaskWithConversationId(conversationId: String): QuestGraph {
        return quests.values.single { it.hasTaskWithConversationId(conversationId) }
    }

    fun getQuestById(questId: String): QuestGraph {
        return quests[questId]!!
    }

    fun getParentsOf(questId: String): List<QuestGraph> {
        return quests.values
            .filterNot { it.isSubQuest }
            .filter { it.linkedWith.contains(questId) }
    }

}
