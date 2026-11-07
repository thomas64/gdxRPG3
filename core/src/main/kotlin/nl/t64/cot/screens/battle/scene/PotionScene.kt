package nl.t64.cot.screens.battle.scene

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.battle.Character
import nl.t64.cot.components.battle.StatChange


private const val DRINK_MOMENT = HERO_TIME_UNTIL_LAST_FRAME
private const val DRINK_HOLD_DURATION = 0.5f
private const val STAT_CHANGE_INTERVAL = 0.3f
private const val STAT_CHANGE_SPACING = 180f

class PotionScene(
    heroCharacter: Character
) : BattleScene(heroCharacter) {

    fun play(stage: Stage, statChanges: List<StatChange>, onFinished: () -> Unit) {
        fadeInAndPlay(stage, createDrink(statChanges), onFinished)
    }

    private fun createDrink(statChanges: List<StatChange>): Action {
        return Actions.sequence(
            Actions.run { heroCombatant.playMotion(CombatantMotion.ITEM) },
            Actions.delay(DRINK_MOMENT),
            Actions.run { playSe(AudioEvent.SE_POTION) },
            Actions.parallel(createShowStatChanges(statChanges),
                             Actions.delay(DRINK_HOLD_DURATION, createHeroWait()),
                             createWaitForNumber(numberDelay = statChanges.lastIndex * STAT_CHANGE_INTERVAL))
        )
    }

    // every stat change gets its own number, one shortly after the other, so that each keeps its own color and name.
    private fun createShowStatChanges(statChanges: List<StatChange>): Action {
        val shownStatChanges: List<Action> = statChanges.mapIndexed { index, statChange ->
            val offsetX: Float = getSpreadOffsetX(index, statChanges.size)
            Actions.delay(index * STAT_CHANGE_INTERVAL, Actions.run { showFloatingStatChange(statChange, offsetX) })
        }
        return Actions.parallel(*shownStatChanges.toTypedArray())
    }

    // the numbers are spread out side by side, with the hero in the middle of them.
    private fun getSpreadOffsetX(index: Int, amountOfStatChanges: Int): Float {
        val middleIndex: Float = (amountOfStatChanges - 1) / 2f
        return (index - middleIndex) * STAT_CHANGE_SPACING
    }

}
