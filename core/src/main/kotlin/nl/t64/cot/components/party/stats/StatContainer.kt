package nl.t64.cot.components.party.stats

import com.fasterxml.jackson.annotation.JsonCreator
import java.util.*


class StatContainer() {

    private lateinit var level: Level
    private val stats: EnumMap<StatItemId, StatItem> = EnumMap(StatItemId::class.java)

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
                     Pair("eduRank", stats.getValue(StatItemId.ENDURANCE).rank),
                     Pair("eduVari", stats.getValue(StatItemId.ENDURANCE).variable),
                     Pair("eduBon", stats.getValue(StatItemId.ENDURANCE).bonus))
    }

    fun getMaximumHp(): Int {
        return (level.rank
                + stats.getValue(StatItemId.STAMINA).rank
                + stats.getValue(StatItemId.ENDURANCE).rank
                + stats.getValue(StatItemId.ENDURANCE).bonus)
    }

    fun getCurrentHp(): Int {
        return (level.variable
                + stats.getValue(StatItemId.STAMINA).variable
                + stats.getValue(StatItemId.ENDURANCE).variable
                + stats.getValue(StatItemId.ENDURANCE).bonus)
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
                stats.getValue(StatItemId.ENDURANCE).takeDamage(it2)
            }
        }
    }

    fun recoverFullHp() {
        stats.getValue(StatItemId.ENDURANCE).restore()
        stats.getValue(StatItemId.STAMINA).restore()
        level.restore()
    }

    fun recoverPartHp(healPoints: Int) {
        stats.getValue(StatItemId.ENDURANCE).restorePart(healPoints)?.let { it1 ->
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
