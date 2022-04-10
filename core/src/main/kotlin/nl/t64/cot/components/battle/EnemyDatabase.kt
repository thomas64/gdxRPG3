package nl.t64.cot.components.battle

import nl.t64.cot.ConfigDataLoader


object EnemyDatabase {

    private val enemies: Map<String, EnemyItem> = ConfigDataLoader.createEnemies()

    fun createEnemy(enemyId: String): EnemyItem {
        val enemyItem = enemies[enemyId]!!
        return enemyItem.copy()
    }

}
