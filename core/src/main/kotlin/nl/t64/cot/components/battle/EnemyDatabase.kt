package nl.t64.cot.components.battle

import com.badlogic.gdx.Gdx
import nl.t64.cot.Utils


private const val ENEMY_CONFIGS = "configs/characters/enemy1.json"

object EnemyDatabase {

    private val enemies: Map<String, EnemyItem> = Utils.readValue<EnemyItem>(
        Gdx.files.internal(ENEMY_CONFIGS).readString()).onEach { it.value.id = it.key }

    fun createEnemy(enemyId: String): EnemyItem {
        val enemyItem = enemies[enemyId]!!
        return enemyItem.createCopy()
    }

}
