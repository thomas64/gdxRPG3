package nl.t64.cot.components.party.stats

import com.fasterxml.jackson.annotation.JsonCreator
import java.util.*


class StatContainer() {

    private val stats: MutableMap<StatItemId, Int> = EnumMap(StatItemId::class.java)
    val maximumHp: Int get() = (40f * (getById(StatItemId.CONSTITUTION).rank * 10f / 100f)).toInt()
    val maximumSp: Int get() = (20f * (getById(StatItemId.STAMINA).rank * 10f / 100f)).toInt()

    @JsonCreator
    constructor(startingStats: Map<String, Int>) : this() {
        startingStats.forEach { (statId, rank) ->
            stats[StatItemId.valueOf(statId.uppercase())] = rank
        }
    }

    fun toProgress(): Map<String, Int> {
        return stats.entries.associate { (statItemId, rank) -> statItemId.name to rank }
    }

    fun applyProgress(ranks: Map<String, Int>) {
        ranks.forEach { (statId, rank) ->
            val statItemId = StatItemId.valueOf(statId)
            stats[statItemId] = rank
        }
    }

    fun getAll(): List<StatItem> {
        return StatItemId.entries.map { getById(it) }
    }

    fun getById(statItemId: StatItemId): StatItem {
        return StatDatabase.createStatItem(statItemId, stats[statItemId]!!)
    }

    fun getTotalXpCost(): Int {
        return getAll().sumOf { it.getTotalXpCostFromRankOneToCurrent() }
    }

    fun setRank(statItemId: StatItemId, rank: Int) {
        stats[statItemId] = rank
    }

}
