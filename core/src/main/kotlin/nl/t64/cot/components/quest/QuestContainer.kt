package nl.t64.cot.components.quest

import nl.t64.cot.ConfigDataLoader


class QuestContainer {

    private val quests: Map<String, QuestGraph> = ConfigDataLoader.createQuests()

    fun getAllKnownQuestsForVisual(): Array<QuestGraph> = quests.values
        .filter { !it.isHidden }
        .filter { it.isOneOfBothStatesEqualOrHigherThan(QuestState.KNOWN) }
        .sortedWith(compareBy({ it.isFailed }, { it.currentState }, { it.id }))
        .toTypedArray()

    fun reset() {
        quests.values.forEach { it.reset() }
    }

    fun update() {
        quests.values.forEach { it.possibleSetFindItemTaskComplete() }
    }

    fun contains(questId: String): Boolean = quests.containsKey(questId)
    fun getQuestById(questId: String): QuestGraph = quests[questId]!!

}
