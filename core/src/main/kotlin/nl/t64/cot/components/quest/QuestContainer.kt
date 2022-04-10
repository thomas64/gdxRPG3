package nl.t64.cot.components.quest

import nl.t64.cot.ConfigDataLoader


class QuestContainer {

    private val quests: Map<String, QuestGraph> = ConfigDataLoader.createQuests()

    fun getAllKnownQuestsForVisual(): Array<QuestGraph> = quests.values
        .filter { !it.isHidden }
        .filter { it.isCurrentStateEqualOrHigherThan(QuestState.KNOWN) }
        .sortedWith(compareBy({ it.isFailed }, { it.currentState }, { it.id }))
        .toTypedArray()

    fun update() = possibleSetCompleteFindItemTask()

    fun contains(questId: String): Boolean = quests.containsKey(questId)
    fun getQuestById(questId: String): QuestGraph = quests[questId]!!

    private fun possibleSetCompleteFindItemTask() = quests.values
        .filter { it.isCurrentStateEqualOrLowerThan(QuestState.ACCEPTED) }
        .forEach { it.possibleSetFindItemTaskComplete() }

}
