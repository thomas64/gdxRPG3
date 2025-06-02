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

    private fun createEnemies(battleId: String): List<EnemyItem> {
        val (battlersWithPostfix, battlers) = gameData.battles.getBattlers(battleId)
            .partition { battler -> battler.id.count { it == '_' } == 2 }
        return battlers.toEnemies() + battlersWithPostfix.toEnemiesWithSameIdAndNameWithIndex()
    }

    private fun List<Battler>.toEnemies(): List<EnemyItem> {
        return this.flatMap { battler ->
            List(battler.amount) { index ->
                if (battler.amount > 1) {
                    EnemyDatabase.createEnemyWithIndexAfterName(battler.id, index)
                } else {
                    EnemyDatabase.createEnemy(battler.id)
                }
            }
        }
    }

    private fun List<Battler>.toEnemiesWithSameIdAndNameWithIndex(): List<EnemyItem> {
        var globalSpecialIndex = 0
        return this
            .groupBy { it.id.substringBeforeLast('_') }
            .flatMap { (prefix, battlersWithSamePrefix) ->
                battlersWithSamePrefix.flatMap { battler ->
                    List(battler.amount) {
                        if (battlersWithSamePrefix.size > 1) {
                            EnemyDatabase.createEnemyWithIndexAfterNameAndPrefixId(battler.id, prefix, globalSpecialIndex++)
                        } else {
                            EnemyDatabase.createEnemyWithPrefixId(battler.id, prefix)
                        }
                    }
                }
            }
    }

}
