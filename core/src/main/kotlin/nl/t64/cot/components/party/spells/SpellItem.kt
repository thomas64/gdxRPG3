package nl.t64.cot.components.party.spells

import com.fasterxml.jackson.annotation.JsonProperty
import nl.t64.cot.components.party.PersonalityItem
import kotlin.math.roundToInt


private val LEARNING_COSTS = listOf(20, 8, 12, 16, 20, 24, 28, 32, 36, 40)
private const val MAXIMUM = 10

/* todo: moeten alle stats en skills ipv in individuele classes in json komen net zoals spells?
    wellicht komt er nog een hoop verschillende logica in de verschillende classes. dan is json niet handig. */

class SpellItem(
    val name: String = "",
    val school: SchoolType = SchoolType.UNKNOWN,
    val sort: Int = 0,
    private val upgrade: Float = 0f,
    @JsonProperty("min_wizard") val minWizard: Int = 0,
    @JsonProperty("resource") private val requiredResource: ResourceType = ResourceType.GOLD,
    @JsonProperty("stamina_cost") private val staminaCost: Int = 0,
    private val range: Int = 0,
    @JsonProperty("number_of_targets") private val numberOfTargets: NumberOfTargets = NumberOfTargets.ONE,
    private val target: Target = Target.EVERYONE,
    private val damage: Int = 0,
    private val description: List<String> = emptyList()
) : PersonalityItem {

    lateinit var id: String
    var rank: Int = 0
    private var bonus: Int = 0

    fun createCopy(rank: Int): SpellItem {
        val spellCopy = SpellItem(
            name, school, sort, upgrade, minWizard, requiredResource,
            staminaCost, range, numberOfTargets, target, damage, description)
        spellCopy.id = id
        spellCopy.rank = rank
        return spellCopy
    }

    override fun getDescription(totalScholar: Int): String {
        return (getDescription()
                + "A teacher is needed to upgrade a spell.")
    }

    fun getTeacherDescription(teacherSpell: SpellItem, wizardRank: Int, totalScholar: Int): String {
        return (getDescription()
                + getNeededXpForNextRank(teacherSpell, wizardRank, totalScholar) + System.lineSeparator()
                + getNeededGoldForNextRank(teacherSpell, wizardRank))
    }

    private fun getDescription(): String {
        return (description.joinToString(System.lineSeparator()) + System.lineSeparator()
                + System.lineSeparator()
                + "School: " + school.title + System.lineSeparator()
                + "Requires: " + requiredResource.title + System.lineSeparator()
                + "Stamina cost: " + staminaCost + System.lineSeparator()
                + System.lineSeparator())
    }

    fun doUpgrade() {
        rank += 1
    }

    private fun getNeededXpForNextRank(teacherSpell: SpellItem, wizardRank: Int, totalScholar: Int): String {
        val xpNeeded = when (val cost = getXpCostForNextRank(teacherSpell, wizardRank, totalScholar).toString()) {
            "0" -> "Max"
            "-1" -> "N/A"
            "-2" -> "0"
            else -> cost
        }
        return "'XP to Invest' needed for next level: $xpNeeded"
    }

    private fun getNeededGoldForNextRank(teacherSpell: SpellItem, wizardRank: Int): String {
        val goldNeeded = when (val cost = getGoldCostForNextRank(teacherSpell, wizardRank).toString()) {
            "0" -> "Max"
            "-1" -> "N/A"
            "-2" -> "0"
            else -> cost
        }
        return "Gold needed for next level: $goldNeeded"
    }

    fun getXpCostForNextRank(teacherSpell: SpellItem, wizardRank: Int, totalScholar: Int): Int {
        return when {
            rank >= MAXIMUM -> 0
            wizardRank < 1 -> -1
            rank >= teacherSpell.rank -> -2
            else -> (getUpgradeFormula() - ((getUpgradeFormula() / 100f) * totalScholar)).roundToInt()
        }
    }

    fun getGoldCostForNextRank(teacherSpell: SpellItem, wizardRank: Int): Int {
        val nextRank = rank + 1
        return when {
            rank >= MAXIMUM -> 0
            wizardRank < 1 -> -1
            rank >= teacherSpell.rank -> -2
            else -> LEARNING_COSTS[nextRank - 1]
        }
    }

    private fun getUpgradeFormula(): Float {
        val nextRank = rank + 1
        return upgrade * (nextRank * nextRank)
    }

}
