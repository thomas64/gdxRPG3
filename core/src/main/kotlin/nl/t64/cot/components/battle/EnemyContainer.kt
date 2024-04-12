package nl.t64.cot.components.battle

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.loot.Loot


class EnemyContainer(
    private val battleId: String
) {
    private val enemies: List<EnemyItem> = createEnemies(battleId)

    fun getAll(): List<EnemyItem> {
        return enemies
    }

    fun getTotalXp(): Int {
        return enemies.sumOf { it.xp }
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
