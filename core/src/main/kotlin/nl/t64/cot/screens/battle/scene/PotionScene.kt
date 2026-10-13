package nl.t64.cot.screens.battle.scene

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.battle.Character
import nl.t64.cot.components.battle.PotionEffect


private const val DRINK_MOMENT = HERO_TIME_UNTIL_LAST_FRAME
private const val DRINK_HOLD_DURATION = 0.5f
private const val EFFECT_INTERVAL = 0.3f
private const val EFFECT_SPACING = 180f

class PotionScene(
    heroCharacter: Character
) : BattleScene(heroCharacter) {

    fun play(stage: Stage, effects: List<PotionEffect>, onFinished: () -> Unit) {
        fadeInAndPlay(stage, createDrink(effects), onFinished)
    }

    private fun createDrink(effects: List<PotionEffect>): Action {
        return Actions.sequence(
            Actions.run { heroCombatant.playMotion(CombatantMotion.ITEM) },
            Actions.delay(DRINK_MOMENT),
            Actions.run { playSe(AudioEvent.SE_POTION) },
            Actions.parallel(createShowEffects(effects),
                             Actions.delay(DRINK_HOLD_DURATION, createHeroWait()),
                             createWaitForNumber(numberDelay = effects.lastIndex * EFFECT_INTERVAL))
        )
    }

    // every effect gets its own number, one shortly after the other, so that each keeps its own color and name.
    private fun createShowEffects(effects: List<PotionEffect>): Action {
        val shownEffects: List<Action> = effects.mapIndexed { index, effect ->
            val offsetX: Float = getSpreadOffsetX(index, effects.size)
            Actions.delay(index * EFFECT_INTERVAL, Actions.run { showEffect(effect, offsetX) })
        }
        return Actions.parallel(*shownEffects.toTypedArray())
    }

    // the numbers are spread out side by side, with the hero in the middle of them.
    private fun getSpreadOffsetX(index: Int, amountOfEffects: Int): Float {
        val middleIndex: Float = (amountOfEffects - 1) / 2f
        return (index - middleIndex) * EFFECT_SPACING
    }

    // the effect comes over the hero, so its number sinks onto the hero, unlike damage that springs off.
    private fun showEffect(effect: PotionEffect, offsetX: Float) {
        val number: Table = createNumber(effect.amount,
                                         NumberStyle.NORMAL.getFont(),
                                         effect.color,
                                         effect.affects)
        showNumberSinkingOnto(heroCombatant, number, offsetX)
    }

}
