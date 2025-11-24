package nl.t64.cot.components.battle

import nl.t64.cot.resources.ConfigDataLoader


object EnemyDatabase {

    private val enemies: Map<String, EnemyItem> = ConfigDataLoader.createEnemies()

    fun createStandardEnemy(enemyId: String): EnemyItem {
        val enemyItem = enemies[enemyId]!!
        return enemyItem.createCopy()
    }

    fun createStandardEnemyWithIndex(enemyId: String, index: Int): EnemyItem {
        val enemyItem = enemies[enemyId]!!
        return enemyItem.createCopy(name = "${enemyItem.name} ${index + 1}")
    }

    fun createVariantEnemy(enemyId: String, baseId: String): EnemyItem {
        val enemyItem = enemies[enemyId]!!
        return enemyItem.createCopy(id = baseId)
    }

    fun createVariantEnemyWithIndex(enemyId: String, baseId: String, index: Int): EnemyItem {
        val enemyItem = enemies[enemyId]!!
        return enemyItem.createCopy(id = baseId, name = "${enemyItem.name} ${index + 1}")
    }

}
