package nl.t64.cot.components.party.stats

import com.fasterxml.jackson.annotation.JsonCreator


private const val NUMBER_OF_STAT_SLOTS = 7

class StatContainer() {

    private lateinit var level: Level
    private val stats: StatItemMap<StatItemId, StatItem> = StatItemMap()

    @JsonCreator
    constructor(startingStats: Map<String, Int>) : this() {
        this.level = Level(startingStats["level"]!!)
        startingStats
            .filter { it.key != "level" }
            .map { StatDatabase.createStatItem(it.key, it.value) }
            .forEach { this.stats[it.id] = it }
    }

    fun getXpNeededForNextLevel(): Int = level.getXpNeededForNextLevel()
    fun getXpDeltaBetweenLevels(): Int = level.getXpDeltaBetweenLevels()
    val totalXp: Int get() = level.totalXp
    val xpToInvest: Int get() = level.xpToInvest
    val levelRank: Int get() = level.rank

    fun hasEnoughXpFor(xpCost: Int): Boolean {
        return xpToInvest >= xpCost
    }

    fun takeXpToInvest(xpCost: Int) {
        level.takeXpToInvest(xpCost)
    }

    fun gainXp(amount: Int, hasGainedLevel: (Boolean) -> Unit) {
        level.gainXp(amount, hasGainedLevel)
    }

    fun getAllHpStats(): Map<String, Int> {
        return mapOf(Pair("lvlRank", level.rank),
                     Pair("lvlVari", level.variable),
                     Pair("staRank", stats[StatItemId.STAMINA].rank),
                     Pair("staVari", stats[StatItemId.STAMINA].variable),
                     Pair("eduRank", stats[StatItemId.ENDURANCE].rank),
                     Pair("eduVari", stats[StatItemId.ENDURANCE].variable),
                     Pair("eduBon", stats[StatItemId.ENDURANCE].bonus))
    }

    fun getMaximumHp(): Int {
        return (level.rank
                + stats[StatItemId.STAMINA].rank
                + stats[StatItemId.ENDURANCE].rank
                + stats[StatItemId.ENDURANCE].bonus)
    }

    fun getCurrentHp(): Int {
        return (level.variable
                + stats[StatItemId.STAMINA].variable
                + stats[StatItemId.ENDURANCE].variable
                + stats[StatItemId.ENDURANCE].bonus)
    }

    fun getMaximumStamina(): Int = stats[StatItemId.STAMINA].rank
    fun getCurrentStamina(): Int = stats[StatItemId.STAMINA].variable

    fun getById(statItemId: StatItemId): StatItem {
        return stats[statItemId]
    }

    fun getAll(): List<StatItem> {
        return StatItemId.values().map { stats[it] }
    }

    fun takeDamage(damage: Int) {
        level.takeDamage(damage)?.let { it1 ->
            stats[StatItemId.STAMINA].takeDamage(it1)?.let { it2 ->
                stats[StatItemId.ENDURANCE].takeDamage(it2)
            }
        }
    }

    fun recoverFullHp() {
        stats[StatItemId.ENDURANCE].restore()
        stats[StatItemId.STAMINA].restore()
        level.restore()
    }

    fun recoverPartHp(healPoints: Int) {
        stats[StatItemId.ENDURANCE].restorePart(healPoints)?.let { it1 ->
            stats[StatItemId.STAMINA].restorePart(it1)?.let { it2 ->
                level.restorePart(it2)
            }
        }
    }

    fun recoverFullStamina() {
        stats[StatItemId.STAMINA].restore()
    }

    fun getInflictDamageStaminaPenalty(): Int {
        return stats[StatItemId.STAMINA].getInflictDamagePenalty()
    }

    fun getDefenseStaminaPenalty(): Int {
        return stats[StatItemId.STAMINA].getDefensePenalty()
    }

    fun getChanceToHitStaminaPenalty(): Int {
        return stats[StatItemId.STAMINA].getChanceToHitPenalty()
    }

}

private class StatItemMap<K : Enum<K>, V>(initialCapacity: Int = NUMBER_OF_STAT_SLOTS) {
    private val map: MutableMap<String, V> = HashMap(initialCapacity)
    operator fun get(key: Enum<K>): V = map[key.name]!!
    operator fun set(key: Enum<K>, value: V) {
        map[key.name] = value
    }
}
