package nl.t64.cot.components.battle

import nl.t64.cot.resources.ConfigDataLoader


class BattleContainer {

    private val battles: Map<String, Battle> = ConfigDataLoader.createBattles()

    fun getBattlers(battleId: String): List<Battler> = battles[battleId]!!.battlers
    fun getBackground(battleId: String): String = battles[battleId]!!.background
    fun isBattleEscapable(battleId: String): Boolean = battles[battleId]!!.isEscapable
    fun isBattleWon(battleId: String): Boolean = battles[battleId]?.isDefeated ?: false
    fun doEnemiesWantToFight(battleId: String): Boolean = battles[battleId]!!.wantsToFight

    fun setBattleWon(battleId: String) {
        battles[battleId]!!.isDefeated = true
    }

    fun reset() {
        battles.values.forEach { it.reset() }
    }

    fun toProgress(): Map<String, BattleProgress> {
        return battles.mapValues { it.value.toProgress() }
    }

    fun applyProgress(progress: Map<String, BattleProgress>) {
        progress.forEach { (id, p) -> battles[id]!!.applyProgress(p) }
    }

}
