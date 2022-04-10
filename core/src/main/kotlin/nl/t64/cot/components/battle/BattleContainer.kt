package nl.t64.cot.components.battle

import nl.t64.cot.ConfigDataLoader


class BattleContainer {

    private val battles: Map<String, Battle> = ConfigDataLoader.createBattles()

    fun getBattlers(battleId: String): List<Battler> = battles[battleId]!!.battlers
    fun isBattleEscapable(battleId: String): Boolean = battles[battleId]!!.isEscapable
    fun isBattleWon(battleId: String): Boolean = battles[battleId]?.hasWon ?: false
    fun setBattleWon(battleId: String) {
        battles[battleId]!!.hasWon = true
    }

}
