package nl.t64.cot.screens.battle

import nl.t64.cot.Utils.screenManager
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.constants.ScreenType


object BattleUtils {

    fun getAllParticipants(): List<Participant> {
        return getBattleState().turnManager.participants
    }

    fun applyEffectsOfPerformance() {
        val battleState: BattleState = getBattleState()
        battleState.turnManager.troubadourEffects.possibleApply()
        battleState.hasChosenToContinuePerforming = true
    }

    fun reapplyEffectsOfPerformance() {
        getBattleState().turnManager.troubadourEffects.possibleApply()
    }

    fun staggerTarget(target: Participant) {
        getBattleState().turnManager.stagger(target)
    }

    private fun getBattleState(): BattleState {
        val battleScreen = screenManager.getScreen(ScreenType.BATTLE) as BattleScreen
        return battleScreen.battleState
    }

}
