package nl.t64.cot.screens.battle

import nl.t64.cot.Utils.screenManager
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.components.battle.TurnManager
import nl.t64.cot.constants.ScreenType
import java.lang.reflect.Field


object BattleUtils {

    fun getAllParticipants(): List<Participant> {
        val turnManager = getTurnManagerTheUglyWay()
        return turnManager.participants
    }

    fun applyEffectsOfPerformance() {
        val turnManager = getTurnManagerTheUglyWay()
        turnManager.troubadourEffects.possibleApply()
        setIsPerformingTrueInBattleScreenTheUglyWay()
    }

    fun reapplyEffectsOfPerformance() {
        val turnManager = getTurnManagerTheUglyWay()
        turnManager.troubadourEffects.possibleApply()
    }

    fun staggerTarget(target: Participant) {
        val turnManager = getTurnManagerTheUglyWay()
        turnManager.stagger(target)
    }

    private fun getTurnManagerTheUglyWay(): TurnManager {
        val battleScreen = screenManager.getScreen(ScreenType.BATTLE) as BattleScreen
        val turnManager: Field = BattleScreen::class.java.getDeclaredField("turnManager")
        turnManager.isAccessible = true
        return turnManager.get(battleScreen) as TurnManager
    }

    private fun setIsPerformingTrueInBattleScreenTheUglyWay() {
        val battleScreen = screenManager.getScreen(ScreenType.BATTLE) as BattleScreen
        val isPerforming: Field = BattleScreen::class.java.getDeclaredField("hasChosenToContinuePerforming")
        isPerforming.isAccessible = true
        isPerforming.setBoolean(battleScreen, true)
    }

}
