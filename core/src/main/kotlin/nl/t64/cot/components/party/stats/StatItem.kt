package nl.t64.cot.components.party.stats

import nl.t64.cot.components.party.PersonalityItem
import kotlin.math.abs
import kotlin.math.roundToInt


open class StatItem(
    private val maximum: Int = 0,       // Constant value for maximum rank possible.
    private val upgrade: Float = 0f,    // Constant value for upgrading formula.
    private val description: List<String> = emptyList()
) : PersonalityItem {

    lateinit var id: StatItemId
    lateinit var name: String
    var rank: Int = 0
    var variable: Int = 0
    var bonus: Int = 0

    fun createCopy(rank: Int): StatItem {
        val statCopy = StatItem(maximum, upgrade, description)
        statCopy.id = id
        statCopy.name = name
        statCopy.rank = rank
        statCopy.variable = rank
        return statCopy
    }

    override fun getTotalDescription(): String {
        return (description.joinToString(System.lineSeparator()) + System.lineSeparator()
                + System.lineSeparator()
                + getNeededXpForNextRank())
    }

    private fun getNeededXpForNextRank(): String {
        val xpNeeded = getXpCostForNextRank().toString().takeIf { it != "0" } ?: "Max"
        return "'XP to Invest' needed for next rank: $xpNeeded"
    }

    fun getXpCostForNextRank(): Int {
        if (rank >= maximum) return 0
        val nextRank = rank + 1
        return (upgrade * (nextRank * nextRank)).roundToInt()
    }

    fun doUpgrade() {
        rank += 1
        variable += 1
    }

    // todo, speciale bonus toepassen inventoryItem: epic_ring_of_healing, die 1 edu geeft om je leven 1malig te redden.
    fun takeDamage(damage: Int): Int? {
        variable -= damage
        if (variable < 0) {
            val remainingDamage = abs(variable)
            variable = 0
            return remainingDamage
        }
        return null
    }

    fun restorePart(healPoints: Int): Int? {
        variable += healPoints
        if (variable > rank) {
            val remainingHealPoints = variable - rank
            variable = rank
            return remainingHealPoints
        }
        return null
    }

    fun restore() {
        variable = rank
    }

    fun getInflictDamagePenalty(): Int {
        return if (variable <= 0) 5 else 1
    }

    fun getDefensePenalty(): Int {
        return if (variable <= 0) 50 else 0
    }

    fun getChanceToHitPenalty(): Int {
        return if (variable <= 0) 25 else 0
    }

}
