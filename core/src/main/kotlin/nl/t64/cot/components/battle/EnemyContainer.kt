package nl.t64.cot.components.battle

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.loot.Loot
import kotlin.math.roundToInt


class EnemyContainer(
    private val battleId: String
) {
    private val enemies: List<EnemyItem> = createEnemies(battleId)

    fun getEnemy(index: Int): EnemyItem {
        return enemies[index]
    }

    fun getEnemy(name: String): EnemyItem {
        return enemies.first { it.name == name }
    }

    fun getAll(): List<EnemyItem> {
        return enemies
    }

    fun getTotalXp(): Int {
        val baseXp: Int = enemies.sumOf { it.xp }
        val bonusMultiplier: Float = 1f + ((enemies.size - 1f) * 0.2f)
        return (baseXp * bonusMultiplier).roundToInt()
    }

    fun getSpoils(): Loot {
        val spoils = mutableMapOf<String, Int>()
        enemies.forEach { it.addDropsTo(spoils) }
        return Loot(spoils)
    }

    fun doEnemiesWantToBattle(): Boolean {
        return !gameData.battles.wonBattles.contains(battleId)
    }

    private fun createEnemies(battleId: String): List<EnemyItem> {
        return gameData.battles.getBattlers(battleId)
            .map { it.createEnemyList() }
            .flatten()
    }

}
