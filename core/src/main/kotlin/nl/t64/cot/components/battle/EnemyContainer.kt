package nl.t64.cot.components.battle

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.loot.Loot


private const val LEVEL_DIFFERENCE_FOR_WANTING_BATTLE = 5

class EnemyContainer(battleId: String) {

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
        val averageEnemies = enemies.map { it.getLevel() }.average()
        val averageHeroes = gameData.party.getAverageLevel()
        return averageEnemies > averageHeroes - LEVEL_DIFFERENCE_FOR_WANTING_BATTLE
    }

    private fun createEnemies(battleId: String): List<EnemyItem> {
        return gameData.battles.getBattlers(battleId)
            .map { it.createEnemyList() }
            .flatten()
    }

}
