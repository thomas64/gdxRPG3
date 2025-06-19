package nl.t64.cot.screens.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playBgm
import nl.t64.cot.audio.stopAllBgm
import nl.t64.cot.components.battle.EnemyContainer
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.dialog.MessageDialog


class BattleResultManager(
    private val stage: Stage,
    private val battleObserver: BattleSubject,
    private val battleId: String,
    private val enemies: EnemyContainer,
    private val setBgmFading: (Boolean) -> Unit
) {

    fun winBattle() {
        stage.addAction(Actions.sequence(
            Actions.run { setBgmFading.invoke(true) },
            Actions.delay(Constant.FADE_DURATION),
            Actions.run { setBgmFading.invoke(false) },
            Actions.run { stopAllBgm() },
            Actions.run { playBgm(AudioEvent.BGM_WIN_BATTLE, false) },
            Actions.run {
                gameData.battles.setBattleWon(battleId)

                val totalXpWon = enemies.getTotalXp()
                gameData.party.gainXp(totalXpWon)
                val winMessage = """
                    The enemy is defeated!
                    Party gained [OLIVE]$totalXpWon XP[BLACK].""".trimIndent()

                val messageDialog = MessageDialog(winMessage)
                messageDialog.setActionAfterHide { battleWonExitScreen() }
                messageDialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT)
            }
        ))
    }

    private fun battleWonExitScreen() {
        gameData.clock.takeHalfHour()
        exitScreen { battleObserver.notifyBattleWon(battleId, enemies.getSpoils()) }
    }

    fun battleFledExitScreen() {
        gameData.clock.takeQuarterHour()
        exitScreen { battleObserver.notifyBattleFled() }
    }

    fun gameOver() {
        stage.addAction(Actions.sequence(
            Actions.run { setBgmFading.invoke(true) },
            Actions.delay(Constant.FADE_DURATION),
            Actions.run { setBgmFading.invoke(false) },
            Actions.run { stopAllBgm() },
            Actions.run { playBgm(AudioEvent.BGM_LOSE_BATTLE, false) },
            Actions.run {
                val messageDialog = MessageDialog(createDeathMessage())
                messageDialog.setActionAfterHide { gameOverExitScreen() }
                messageDialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT)
            }
        ))
    }

    private fun createDeathMessage(): String {
        val currentCycle = gameData.numberOfCycles
        val isFacingArdorOrOrcGenerals = enemies.getAll().all { it.id in listOf("orc_general", "ardor") }

        return when {

            currentCycle in 1..3 && isFacingArdorOrOrcGenerals -> """
                Mozes is knocked down.

                The fight is over.""".trimIndent()

            else -> """
                Mozes took a fatal blow.

                Game Over.""".trimIndent()
        }
    }

    private fun gameOverExitScreen() {
        exitScreen { battleObserver.notifyBattleLost() }
    }

    private fun exitScreen(actionAfterExit: () -> Unit) {
        stage.addAction(Actions.sequence(
            Actions.run {
                Gdx.input.inputProcessor = null
                Utils.setGamepadInputProcessor(null)
            },
            Actions.run { setBgmFading.invoke(true) },
            Actions.fadeOut(Constant.FADE_DURATION),
            Actions.run { setBgmFading.invoke(false) },
            Actions.run { stopAllBgm() },
            Actions.run { actionAfterExit.invoke() }
        ))
    }

}
