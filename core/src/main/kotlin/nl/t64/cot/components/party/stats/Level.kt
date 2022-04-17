package nl.t64.cot.components.party.stats

import kotlin.math.abs
import kotlin.math.roundToInt


private const val MAXIMUM = 40

class Level(
    var rank: Int = 1,
) {
    var variable: Int = rank
    var totalXp: Int = getTotalXpForLevel(rank.toFloat())
    var xpToInvest: Int = 0

    fun getXpDeltaBetweenLevels(): Int {
        val nextLevel = rank + 1
        return getTotalXpForLevel(nextLevel.toFloat()) - getTotalXpForLevel(rank.toFloat())
    }

    fun getXpNeededForNextLevel(): Int {
        return when {
            rank >= MAXIMUM -> 1
            else -> {
                val nextLevel = rank + 1
                getTotalXpForLevel(nextLevel.toFloat()) - totalXp
            }
        }
    }

    fun gainXp(amount: Int, gainLevel: (Int) -> Unit) {
        if (rank < MAXIMUM) {
            xpToInvest += amount
            totalXp += amount
        }
        var amountOfLevelsGained = 0
        while (getXpNeededForNextLevel() <= 0) {
            rank += 1
            variable += 1
            amountOfLevelsGained += 1
        }
        if (amountOfLevelsGained >= 1) gainLevel.invoke(amountOfLevelsGained)
    }

    fun takeXpToInvest(xpCost: Int) {
        xpToInvest -= xpCost
    }

    fun takeDamage(damage: Int): Int? {
        variable -= damage
        if (variable < 0) {
            val remainingDamage = abs(variable)
            variable = 0
            return remainingDamage
        }
        return null
    }

    fun restorePart(healPoints: Int) {
        variable += healPoints
        if (variable > rank) restore()
    }

    fun restore() {
        variable = rank
    }

    private fun getTotalXpForLevel(level: Float): Int {
        return ((5f / 6f) * ((2f * level) + 1f) * ((level * level) + level)).roundToInt()
    }

}
