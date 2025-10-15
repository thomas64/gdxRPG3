package nl.t64.cot.components.party.stats

import nl.t64.cot.resources.ConfigDataLoader


object StatDatabase {

    private val statItems: Map<String, StatItem> = ConfigDataLoader.createStats()

    fun createStatItem(statItemId: StatItemId, rank: Int): StatItem {
        val statItem = statItems[statItemId.name.lowercase()]!!
        return statItem.createCopy(rank)
    }

}
