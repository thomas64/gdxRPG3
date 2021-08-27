package nl.t64.cot.subjects

import nl.t64.cot.components.loot.Loot


interface BattleObserver {

    fun onNotifyBattleWon(battleId: String, spoils: Loot, levelUpMessage: String?)

}
