package nl.t64.cot.screens.battle


class BattleMenuHandlers(
    val showInventoryScreenPreBattle: () -> Unit,
    val startBattle: () -> Unit,
    val showInventoryScreen: () -> Unit,
    val showFleeDialog: () -> Unit,
    val showDelayTurnDialog: () -> Unit,
    val showPushOnDialog: () -> Unit,
    val showConfirmRestDialog: () -> Unit,
    val endTurn: () -> Unit
)
