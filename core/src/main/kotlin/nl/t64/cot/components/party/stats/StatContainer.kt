package nl.t64.cot.components.party.stats

import com.fasterxml.jackson.annotation.JsonCreator


class StatContainer() {

    private lateinit var level: Level
    private val stats: StatItemMap<StatItemId, StatItem> = StatItemMap()

    @JsonCreator
    constructor(startingStats: Map<String, Int>) : this() {
        this.level = Level(startingStats["level"]!!)
        startingStats
            .filterKeys { it != "level" }
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

    fun gainXp(amount: Int, gainLevel: (Int) -> Unit) {
        level.gainXp(amount, gainLevel)
    }

    fun getAllHpStats(): Map<String, Int> {
        return mapOf(Pair("lvlRank", level.rank),
                     Pair("lvlVari", level.variable),
                     Pair("staRank", stats[StatItemId.STAMINA].rank),
                     Pair("staVari", stats[StatItemId.STAMINA].variable),
                     Pair("conRank", stats[StatItemId.CONSTITUTION].rank),
                     Pair("conVari", stats[StatItemId.CONSTITUTION].variable),
                     Pair("conBon", stats[StatItemId.CONSTITUTION].bonus))
    }

    fun getMaximumHp(): Int {
        return (level.rank
                + stats[StatItemId.STAMINA].rank
                + stats[StatItemId.CONSTITUTION].rank
                + stats[StatItemId.CONSTITUTION].bonus)
    }

    fun getCurrentHp(): Int {
        return (level.variable
                + stats[StatItemId.STAMINA].variable
                + stats[StatItemId.CONSTITUTION].variable
                + stats[StatItemId.CONSTITUTION].bonus)
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
        level.takeDamage(damage)?.let { remainingDamage ->
            stats[StatItemId.STAMINA].takeDamage(remainingDamage)?.let { leftoverDamage ->
                stats[StatItemId.CONSTITUTION].takeDamage(leftoverDamage)
            }
        }
    }

    fun recoverFullHp() {
        stats[StatItemId.CONSTITUTION].restore()
        stats[StatItemId.STAMINA].restore()
        level.restore()
    }

    fun recoverPartHp(healPoints: Int) {
        stats[StatItemId.CONSTITUTION].restorePart(healPoints)?.let { remainingHealPoints ->
            stats[StatItemId.STAMINA].restorePart(remainingHealPoints)?.let { leftoverHealPoints ->
                level.restorePart(leftoverHealPoints)
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

private class StatItemMap<K : Enum<K>, V> {
    private val map: MutableMap<String, V> = HashMap(StatItemId.values().size)
    operator fun get(key: Enum<K>): V = map[key.name]!!
    operator fun set(key: Enum<K>, value: V) {
        map[key.name] = value
    }
}
