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

    fun getTotalCombatPower(): Float {
        return enemies.map { it.getCombatPower() }.sum()
    }

    fun getTotalXp(): Int {
        val baseXp: Int = enemies.sumOf { it.xp }
        val bonusMultiplier: Float = 1f + ((enemies.size - 1f) * 0.1f)
        return (baseXp * bonusMultiplier).roundToInt()
    }

    fun getSpoils(): Loot {
        val spoils = mutableMapOf<String, Int>()
        enemies.forEach { it.addDropsTo(spoils) }
        return Loot(spoils)
    }

    private fun createEnemies(battleId: String): List<EnemyItem> {
        val battlers: List<Battler> = gameData.battles.getBattlers(battleId)
        val (variantEnemies, standardEnemies) = battlers.partition { it.isVariantEnemy() }

        return standardEnemies.toStandardEnemies() +
            variantEnemies.toVariantEnemiesWithGlobalIndexPerBaseType()
    }

    private fun Battler.isVariantEnemy(): Boolean {
        return this.id.count { it == '_' } == 2
    }

    private fun List<Battler>.toStandardEnemies(): List<EnemyItem> {
        return this.flatMap { battler ->
            List(battler.amount) { indexInGroup ->
                if (battler.amount > 1) {
                    EnemyDatabase.createStandardEnemyWithIndex(battler.id, indexInGroup)
                } else {
                    EnemyDatabase.createStandardEnemy(battler.id)
                }
            }
        }
    }

    private fun List<Battler>.toVariantEnemiesWithGlobalIndexPerBaseType(): List<EnemyItem> {
        return this
            .groupBy { it.extractBaseType() }
            .flatMap { (baseType, variantsOfSameBaseType) ->
                var globalIndexForCurrentBaseType = 0
                val totalAmount = variantsOfSameBaseType.sumOf { it.amount }

                variantsOfSameBaseType.flatMap { variant ->
                    List(variant.amount) {
                        if (totalAmount > 1) {
                            EnemyDatabase.createVariantEnemyWithIndex(
                                variant.id,
                                baseType,
                                globalIndexForCurrentBaseType++
                            )
                        } else {
                            EnemyDatabase.createVariantEnemy(variant.id, baseType)
                        }
                    }
                }
            }
    }

    private fun Battler.extractBaseType(): String {
        return this.id.substringBeforeLast('_')
    }

}
