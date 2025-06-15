package nl.t64.cot.components.party.skills

import nl.t64.cot.components.party.PersonalityItem
import kotlin.math.roundToInt


private val TRAINING_COSTS = listOf(20, 8, 12, 16, 20, 24, 28, 32, 36, 40)
private const val MAXIMUM = 10

data class SkillItem(
    override val id: SkillItemId = SkillItemId.NONE,
    override val name: String = "",
    override val description: List<String> = emptyList(),
    var rank: Int = 0,
    private val upgrade: Float = 0f,                        // Constant value for upgrading formula.
) : PersonalityItem() {

    var bonus: Int = 0

    fun createCopy(rank: Int): SkillItem {
        return copy(rank = rank)
    }

    override fun getTotalDescription(): String {
        return (description.joinToString(System.lineSeparator()) + System.lineSeparator()
            + System.lineSeparator()
            + "- A trainer is needed to upgrade a skill.")
    }

    fun getTrainerDescription(trainerSkill: SkillItem): String {
        return (description.joinToString(System.lineSeparator()) + System.lineSeparator()
            + System.lineSeparator()
            + getNeededXpForNextRank(trainerSkill) + System.lineSeparator()
            + getNeededGoldForNextRank(trainerSkill))
    }

    fun doUpgrade() {
        rank += 1
    }

    private fun getNeededXpForNextRank(trainerSkill: SkillItem): String {
        val xpNeeded = when (val cost = getXpCostForNextRank(trainerSkill).toString()) {
            "0" -> "Max"
            "-1",
            "-2" -> "N/A"
            else -> cost
        }
        return "- [GOLD]XP needed for ${getFirstOrNext()} rank: $xpNeeded"
    }

    private fun getNeededGoldForNextRank(trainerSkill: SkillItem): String {
        val goldNeeded = when (val cost = getGoldCostForNextRank(trainerSkill).toString()) {
            "0" -> "Max"
            "-1",
            "-2" -> "N/A"
            else -> cost
        }
        return "- [GOLD]Gold needed for ${getFirstOrNext()} rank: $goldNeeded"
    }

    private fun getFirstOrNext(): String {
        return if (rank <= 0) "first" else "next"
    }

    fun getXpCostForNextRank(trainerSkill: SkillItem): Int {
        return when {
            rank == -1 -> -1
            rank >= MAXIMUM -> 0
            rank >= trainerSkill.rank -> -2
            else -> getXpCostForNextRank().roundToInt()
        }
    }

    fun getGoldCostForNextRank(trainerSkill: SkillItem): Int {
        val nextRank = rank + 1
        return when {
            rank == -1 -> -1
            rank >= MAXIMUM -> 0
            rank >= trainerSkill.rank -> -2
            else -> TRAINING_COSTS[nextRank - 1]
        }
    }

    fun getTotalXpCostFromRankZeroToCurrent(): Int {
        return (1..rank).sumOf { it.getXpCost().roundToInt() }
    }

    fun getXpCostForNextRank(): Float {
        val nextRank = rank + 1
        return nextRank.getXpCost()
    }

    private fun Int.getXpCost(): Float {
        return (upgrade * (this * this))
    }

}
