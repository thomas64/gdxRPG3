package nl.t64.cot.components.party.skills

import nl.t64.cot.components.party.PersonalityItem
import kotlin.math.roundToInt


private val TRAINING_COSTS = listOf(20, 8, 12, 16, 20, 24, 28, 32, 36, 40)
private const val MAXIMUM = 10

abstract class SkillItem(
    val id: SkillItemId,    // Constant value
    val name: String,       // Constant value
    val upgrade: Float,     // Constant value for upgrading formula.
    var rank: Int
) : PersonalityItem {
    var bonus: Int = 0

    override fun getDescription(totalScholar: Int): String { // todo, nagaan of totalScholar meegegeven moet worden
        return (getDescription() + System.lineSeparator() + System.lineSeparator()
                + "A trainer is needed to upgrade a skill.")
    }

    fun getTrainerDescription(trainerSkill: SkillItem, totalScholar: Int): String {
        return (getDescription() + System.lineSeparator() + System.lineSeparator()
                + getNeededXpForNextLevel(trainerSkill, totalScholar) + System.lineSeparator()
                + getNeededGoldForNextLevel(trainerSkill))
    }

    abstract fun getDescription(): String

    fun doUpgrade() {
        rank += 1
    }

    private fun getNeededXpForNextLevel(trainerSkill: SkillItem, totalScholar: Int): String {
        val xpNeeded = when (val cost = getXpCostForNextLevel(trainerSkill, totalScholar).toString()) {
            "0" -> "Max"
            "-1" -> "N/A"
            "-2" -> "0"
            else -> cost
        }
        return "'XP to Invest' needed for next level: $xpNeeded"
    }

    private fun getNeededGoldForNextLevel(trainerSkill: SkillItem): String {
        val goldNeeded = when (val cost = getGoldCostForNextLevel(trainerSkill).toString()) {
            "0" -> "Max"
            "-1" -> "N/A"
            "-2" -> "0"
            else -> cost
        }
        return "Gold needed for next level: $goldNeeded"
    }

    fun getXpCostForNextLevel(trainerSkill: SkillItem, totalScholar: Int): Int {
        return when {
            rank == -1 -> -1
            rank >= MAXIMUM -> 0
            rank >= trainerSkill.rank -> -2
            else -> (getUpgradeFormula() - ((getUpgradeFormula() / 100f) * totalScholar)).roundToInt()
        }
    }

    fun getGoldCostForNextLevel(trainerSkill: SkillItem): Int {
        val nextLevel = rank + 1
        return when {
            rank == -1 -> -1
            rank >= MAXIMUM -> 0
            rank >= trainerSkill.rank -> -2
            else -> TRAINING_COSTS[nextLevel - 1]
        }
    }

    private fun getUpgradeFormula(): Float {
        val nextLevel = rank + 1
        return upgrade * (nextLevel * nextLevel)
    }

}
