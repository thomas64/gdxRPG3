package nl.t64.cot.components.party.stats

import nl.t64.cot.components.party.PersonalityItem
import kotlin.math.roundToInt


data class StatItem(
    val id: StatItemId = StatItemId.INTELLIGENCE,   // Value will be replaced when constructed.
    val name: String = "",
    var rank: Int = 0,
    private val maximum: Int = 0,                   // Constant value for maximum rank possible.
    private val upgrade: Float = 0f,                // Constant value for upgrading formula.
    private val description: List<String> = emptyList()
) : PersonalityItem {

    var bonus: Int = 0

    fun createCopy(rank: Int): StatItem {
        return copy(rank = rank)
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
        return nextRank.getXpCost()
    }

    fun doUpgrade() {
        rank += 1
    }

    fun getTotalXpCostFromRankSixToCurrent(): Int {
        // 7 is the first rank that will cost any xp. no character has lower than 6.
        return (7..rank).sumOf { it.getXpCost() }
    }

    private fun Int.getXpCost(): Int {
        return (upgrade * (this * this)).roundToInt()
    }

}
