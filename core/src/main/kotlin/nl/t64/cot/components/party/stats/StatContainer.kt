package nl.t64.cot.components.party.stats

import com.fasterxml.jackson.annotation.JsonCreator


class StatContainer() {

    private val stats: StatItemMap<StatItemId, Int> = StatItemMap()
    val maximumHp: Int get() = (40f * (getById(StatItemId.CONSTITUTION).rank * 10f / 100f)).toInt()
    val maximumSp: Int get() = (20f * (getById(StatItemId.STAMINA).rank * 10f / 100f)).toInt()

    @JsonCreator
    constructor(startingStats: Map<String, Int>) : this() {
        startingStats.forEach { (statId, rank) ->
            stats[StatItemId.valueOf(statId.uppercase())] = rank
        }
    }

    fun getById(statItemId: StatItemId): StatItem {
        return StatDatabase.createStatItem(statItemId, stats[statItemId])
    }

    fun getAll(): List<StatItem> {
        return StatItemId.entries.map { StatDatabase.createStatItem(it, stats[it]) }
    }

    fun getTotalXpCost(): Int {
        return getAll().sumOf { it.getTotalXpCostFromRankOneToCurrent() }
    }

    fun setRank(statItemId: StatItemId, rank: Int) {
        stats[statItemId] = rank
    }

}

private class StatItemMap<K : Enum<K>, V> {
    private val map: MutableMap<String, V> = HashMap(StatItemId.entries.size)
    fun contains(key: Enum<K>): Boolean = map.containsKey(key.name)
    operator fun get(key: Enum<K>): V = map[key.name]!!
    operator fun set(key: Enum<K>, value: V) {
        map[key.name] = value
    }
}
