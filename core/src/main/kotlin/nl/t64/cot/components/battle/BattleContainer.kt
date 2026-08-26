package nl.t64.cot.components.battle

import nl.t64.cot.resources.ConfigDataLoader


class BattleContainer {

    private val battles: Map<String, Battle> = ConfigDataLoader.createBattles()

    // @formatter:off
    fun getBattlers(battleId: String): List<Battler> =      battles[battleId]!!.battlers
    fun getCombatPowerOverride(battleId: String): Float? =  battles[battleId]!!.combatPowerOverride
    fun getBackground(battleId: String): String =           battles[battleId]!!.background
    fun isBattleEscapable(battleId: String): Boolean =      battles[battleId]!!.isEscapable
    fun isBattleWon(battleId: String): Boolean =            battles[battleId]?.isDefeated ?: false
    fun doEnemiesWantToFight(battleId: String): Boolean =   battles[battleId]!!.wantsToFight
    // @formatter:on

    fun setBattleWon(battleId: String) {
        battles[battleId]!!.isDefeated = true
    }

    fun reset() {
        battles.values.forEach { it.reset() }
    }

    fun toProgress(): Map<String, BattleProgress> {
        return battles
            .mapValues { (_, battle) -> battle.toProgress() }
            .filterValues { it.isChanged() }
    }

    fun applyProgress(progress: Map<String, BattleProgress>) {
        progress.forEach { (id, p) -> battles[id]!!.applyProgress(p) }
    }

}
