package nl.t64.cot.components.battle

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.loot.Loot
import kotlin.random.Random


class EnemyContainer(battleId: String) {

    private val enemies: List<EnemyItem> = createEnemies(battleId)

    private fun createEnemies(battleId: String): List<EnemyItem> {
        val enemies: MutableList<EnemyItem> = ArrayList()
        gameData.battles.getBattlers(battleId).forEach {
            (0 until it.amount).forEach { _ ->
                val enemy = EnemyDatabase.createEnemy(it.id)
                enemies.add(enemy)
            }
        }
        return enemies
    }

    fun getAll(): List<EnemyItem> {
        return enemies
    }

    fun getTotalXp(): Int {
        return enemies.sumOf { it.xp }
    }

    fun getSpoils(): Loot {
        val spoils = mutableMapOf<String, Int>()
        enemies.forEach { enemy ->
            enemy.drops.forEach { drop ->
                if (drop.value >= Random.nextInt(0, 100)) {
                    spoils[drop.key] = (spoils[drop.key] ?: 0) + 1
                }
            }
        }
        return Loot(spoils)
    }

}
