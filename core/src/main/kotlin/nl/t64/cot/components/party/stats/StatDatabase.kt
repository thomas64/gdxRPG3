package nl.t64.cot.components.party.stats

import nl.t64.cot.ConfigDataLoader


object StatDatabase {

    private val statItems: Map<String, StatItem> = ConfigDataLoader.createStats()

    fun createStatItem(statId: String, rank: Int): StatItem {
        val statItem = statItems[statId]!!
        return statItem.createCopy(rank)
    }

}
