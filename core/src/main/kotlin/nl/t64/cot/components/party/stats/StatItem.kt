package nl.t64.cot.components.party.stats

import nl.t64.cot.components.party.PersonalityItem
import kotlin.math.roundToInt


abstract class StatItem(
    val id: StatItemId,     // Constant value
    val name: String,       // Constant value
    val maximum: Int,       // Constant value for maximum rank possible.
    val upgrade: Float,     // Constant value for upgrading formula.
    var rank: Int,
    var variable: Int = 0,
    var bonus: Int = 0,
) : PersonalityItem {

    override fun getDescription(totalLoremaster: Int): String {
        return (getDescription() + System.lineSeparator() + System.lineSeparator()
                + "No trainer is needed to upgrade a stat." + System.lineSeparator()
                + getNeededXpForNextRank())
    }

    abstract fun getDescription(): String

    private fun getNeededXpForNextRank(): String {
        val xpNeeded = getXpCostForNextRank().toString().takeIf { it != "0" } ?: "Max"
        return "XP needed for next rank: $xpNeeded"
    }

    fun getXpCostForNextRank(): Int {
        if (rank >= maximum) return 0
        val nextRank = rank + 1
        return (upgrade * (nextRank * nextRank)).roundToInt()
    }

    open fun doUpgrade() {
        rank += 1
    }

}
