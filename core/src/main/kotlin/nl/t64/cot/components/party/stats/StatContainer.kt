package nl.t64.cot.components.party.stats

import com.fasterxml.jackson.annotation.JsonCreator


private const val TOTAL_XP_NECESSARY_FOR_MOZES_STARTING_ATTRIBUTES = 319

class StatContainer() {

    private val stats: StatItemMap<StatItemId, StatItem> = StatItemMap()
    val maximumHp: Int get() = 20 + (20f * (getById(StatItemId.CONSTITUTION).rank * 10f / 100f)).toInt()
    val maximumSp: Int get() = 20 + (20f * (getById(StatItemId.STAMINA).rank * 10f / 100f)).toInt()

    @JsonCreator
    constructor(startingStats: Map<String, Int>) : this() {
        startingStats
            .map { StatDatabase.createStatItem(it.key, it.value) }
            .forEach { this.stats[it.id] = it }
    }

    fun getById(statItemId: StatItemId): StatItem {
        return stats[statItemId]
    }

    fun getAll(): List<StatItem> {
        return StatItemId.entries.map { stats[it] }
    }

    fun getTotalXpCost(): Int {
        return getAll().sumOf { it.getTotalXpCostFromRankOneToCurrent() } - TOTAL_XP_NECESSARY_FOR_MOZES_STARTING_ATTRIBUTES
    }

}

private class StatItemMap<K : Enum<K>, V> {
    private val map: MutableMap<String, V> = HashMap(StatItemId.entries.size)
    operator fun get(key: Enum<K>): V = map[key.name]!!
    operator fun set(key: Enum<K>, value: V) {
        map[key.name] = value
    }
}
