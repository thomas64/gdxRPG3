package nl.t64.cot.screens.battle

import nl.t64.cot.components.loot.Loot


interface BattleObserver {

    fun onNotifyBattleWon(battleId: String, spoils: Loot)
    fun onNotifyBattleLost()
    fun onNotifyBattleFled()

}
