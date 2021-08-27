package nl.t64.cot.components.party.skills

import nl.t64.cot.components.party.PersonalityItem
import kotlin.math.roundToInt


private val TRAINING_COSTS = listOf(20, 8, 12, 16, 20, 24, 28, 32, 36, 40, 0)
private const val MAXIMUM = 10

abstract class SkillItem(
    val id: SkillItemId,    // Constant value
    val name: String,       // Constant value
    val upgrade: Float,     // Constant value for upgrading formula.
    var rank: Int
) : PersonalityItem {
    var bonus: Int = 0

    override fun getDescription(totalLoremaster: Int): String {
        return (getDescription() + System.lineSeparator() + System.lineSeparator()
                + "A trainer is needed to upgrade a skill." + System.lineSeparator()
                + getNeededXpForNextLevel(totalLoremaster) + System.lineSeparator()
                + getNeededGoldForNextLevel())
    }

    abstract fun getDescription(): String

    private fun getNeededXpForNextLevel(totalLoremaster: Int): String {
        val xpNeeded = getXpCostForNextLevel(totalLoremaster).toString().takeIf { it != "0" } ?: "Max"
        return "XP needed for next level: $xpNeeded"
    }

    private fun getNeededGoldForNextLevel(): String {
        val goldNeeded = getGoldCostForNextLevel().toString().takeIf { it != "0" } ?: "Max"
        return "Gold needed for next level: $goldNeeded"
    }

    fun getXpCostForNextLevel(totalLoremaster: Int): Int {
        return when {
            rank >= MAXIMUM -> 0
            else -> (getUpgradeFormula() - ((getUpgradeFormula() / 100) * totalLoremaster)).roundToInt()
        }
    }

    fun getGoldCostForNextLevel(): Int {
        val nextLevel = rank + 1
        return TRAINING_COSTS[nextLevel - 1]
    }

    private fun getUpgradeFormula(): Float {
        val nextLevel = rank + 1
        return upgrade * (nextLevel * nextLevel)
    }

}
