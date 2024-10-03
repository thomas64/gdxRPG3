package nl.t64.cot.components.battle

import nl.t64.cot.resources.ConfigDataLoader


class BattleContainer {

    private val battles: Map<String, Battle> = ConfigDataLoader.createBattles()
    val wonBattles: MutableSet<String> = mutableSetOf()

    fun getBattlers(battleId: String): List<Battler> = battles[battleId]!!.battlers
    fun getBackground(battleId: String): String = battles[battleId]!!.background
    fun isBattleEscapable(battleId: String): Boolean = battles[battleId]!!.isEscapable
    fun isBattleWon(battleId: String): Boolean = battles[battleId]?.hasWon ?: false

    fun setBattleWon(battleId: String) {
        battles[battleId]!!.hasWon = true
        wonBattles.add(battleId)
    }

    fun reset() {
        battles.values.forEach { it.reset() }
    }

}
