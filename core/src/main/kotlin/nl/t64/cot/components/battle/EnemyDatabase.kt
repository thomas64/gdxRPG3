package nl.t64.cot.components.battle

import nl.t64.cot.resources.ConfigDataLoader


object EnemyDatabase {

    private val enemies: Map<String, EnemyItem> = ConfigDataLoader.createEnemies()

    fun createEnemy(enemyId: String): EnemyItem {
        val enemyItem = enemies[enemyId]!!
        return enemyItem.createCopy()
    }

    fun createEnemyWithIndexAfterName(enemyId: String, index: Int): EnemyItem {
        val enemyItem = enemies[enemyId]!!
        return enemyItem.createCopy(name = "${enemyItem.name} ${index + 1}")
    }

    fun createEnemyWithIndexAfterNameAndPrefixId(enemyId: String, prefixOfEnemyId: String, index: Int): EnemyItem {
        val enemyItem = enemies[enemyId]!!
        return enemyItem.createCopy(id = prefixOfEnemyId, name = "${enemyItem.name} ${index + 1}")
    }

}
