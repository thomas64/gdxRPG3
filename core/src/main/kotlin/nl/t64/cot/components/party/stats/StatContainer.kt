package nl.t64.cot.components.party.stats

import com.fasterxml.jackson.annotation.JsonCreator


private const val NUMBER_OF_STAT_SLOTS = 7

class StatContainer() {

    private lateinit var level: Level
    private val stats: MutableMap<StatItemId, StatItem> = HashMap(NUMBER_OF_STAT_SLOTS)

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
                     Pair("staRank", stats.getValue(StatItemId.STAMINA).rank),
                     Pair("staVari", stats.getValue(StatItemId.STAMINA).variable),
                     Pair("conRank", stats.getValue(StatItemId.CONSTITUTION).rank),
                     Pair("conVari", stats.getValue(StatItemId.CONSTITUTION).variable),
                     Pair("conBon", stats.getValue(StatItemId.CONSTITUTION).bonus))
    }

    fun getMaximumHp(): Int {
        return (level.rank
                + stats.getValue(StatItemId.STAMINA).rank
                + stats.getValue(StatItemId.CONSTITUTION).rank
                + stats.getValue(StatItemId.CONSTITUTION).bonus)
    }

    fun getCurrentHp(): Int {
        return (level.variable
                + stats.getValue(StatItemId.STAMINA).variable
                + stats.getValue(StatItemId.CONSTITUTION).variable
                + stats.getValue(StatItemId.CONSTITUTION).bonus)
    }

    fun getMaximumStamina(): Int = stats.getValue(StatItemId.STAMINA).rank
    fun getCurrentStamina(): Int = stats.getValue(StatItemId.STAMINA).variable

    fun getById(statItemId: StatItemId): StatItem {
        return stats.getValue(statItemId)
    }

    fun getAll(): List<StatItem> {
        return StatItemId.values().map { stats.getValue(it) }
    }

    fun takeDamage(damage: Int) {
        level.takeDamage(damage)?.let { it1 ->
            stats.getValue(StatItemId.STAMINA).takeDamage(it1)?.let { it2 ->
                stats.getValue(StatItemId.CONSTITUTION).takeDamage(it2)
            }
        }
    }

    fun recoverFullHp() {
        stats.getValue(StatItemId.CONSTITUTION).restore()
        stats.getValue(StatItemId.STAMINA).restore()
        level.restore()
    }

    fun recoverPartHp(healPoints: Int) {
        stats.getValue(StatItemId.CONSTITUTION).restorePart(healPoints)?.let { it1 ->
            stats.getValue(StatItemId.STAMINA).restorePart(it1)?.let { it2 ->
                level.restorePart(it2)
            }
        }
    }

    fun recoverFullStamina() {
        stats.getValue(StatItemId.STAMINA).restore()
    }

    fun getInflictDamageStaminaPenalty(): Int {
        return stats.getValue(StatItemId.STAMINA).getInflictDamagePenalty()
    }

    fun getDefenseStaminaPenalty(): Int {
        return stats.getValue(StatItemId.STAMINA).getDefensePenalty()
    }

    fun getChanceToHitStaminaPenalty(): Int {
        return stats.getValue(StatItemId.STAMINA).getChanceToHitPenalty()
    }

}
