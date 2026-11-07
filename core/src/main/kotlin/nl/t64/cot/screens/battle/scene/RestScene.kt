package nl.t64.cot.screens.battle.scene

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.battle.Character
import nl.t64.cot.components.battle.StatChange


private const val SLEEP_BEFORE_RECOVERY = 0.5f

class RestScene(
    heroCharacter: Character
) : BattleScene(heroCharacter) {

    fun play(stage: Stage, statChange: StatChange, onFinished: () -> Unit) {
        fadeInAndPlay(stage, createRest(statChange), onFinished)
    }

    // the hero is not woken up afterwards, so the scene fades out on a sleeping hero.
    private fun createRest(statChange: StatChange): Action {
        return Actions.sequence(
            Actions.run { heroCombatant.playMotion(CombatantMotion.SLEEP) },
            Actions.delay(SLEEP_BEFORE_RECOVERY),
            Actions.run {
                playSe(AudioEvent.SE_POTION)
                showFloatingStatChange(statChange, offsetX = 0f)
            },
            createWaitForNumber(numberDelay = 0f)
        )
    }

}
