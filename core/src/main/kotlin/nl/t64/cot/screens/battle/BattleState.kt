package nl.t64.cot.screens.battle

import nl.t64.cot.components.battle.TurnManager


class BattleState(
    val turnManager: TurnManager
) {

    var phase: BattlePhase = BattlePhase.PRE_BATTLE

    @Volatile
    var isDelayingTurn: Boolean = false
    var isBgmFading: Boolean = false
    var hasChosenToContinuePerforming: Boolean = false

}
