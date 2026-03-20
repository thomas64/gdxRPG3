package nl.t64.cot.screens.battle

import nl.t64.cot.components.loot.Loot


class BattleSubject(private val observer: BattleObserver) {

    fun notifyBattleWon(battleId: String, spoils: Loot) {
        observer.onNotifyBattleWon(battleId, spoils)
    }

    fun notifyBattleLost(battleId: String) {
        observer.onNotifyBattleLost(battleId)
    }

    fun notifyBattleFled() {
        observer.onNotifyBattleFled()
    }

}
