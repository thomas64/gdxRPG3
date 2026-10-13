package nl.t64.cot.screens.battle.scene

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import nl.t64.cot.components.battle.Character
import nl.t64.cot.screens.FontProvider


private const val HERO_SCALE = 3f
private const val GROUND_Y = 300f
private const val HERO_X = 520f

private const val SCENE_FADE_DURATION = 0.2f
private const val PAUSE_AFTER_FADE_IN = 0.7f
private const val PAUSE_BEFORE_FADE_OUT = 0.5f

private const val NUMBER_FLOAT_DISTANCE = 60f
private const val NUMBER_SINK_DEPTH_INTO_HEAD = 30f
private const val NUMBER_DURATION = 1.5f
private const val NUMBER_FADE_DURATION = 0.1f
// a number that starts bigger than its own size, pops back to it in this time.
private const val NUMBER_POP_DURATION = 0.15f

abstract class BattleScene(
    heroCharacter: Character
) : Group() {

    protected val heroCombatant = HeroCombatantActor(heroCharacter.id, HERO_SCALE)

    init {
        heroCombatant.setPosition(HERO_X, GROUND_Y)
        addActor(heroCombatant)
    }

    fun close(onClosed: () -> Unit) {
        addAction(Actions.sequence(
            Actions.delay(PAUSE_BEFORE_FADE_OUT),
            Actions.fadeOut(SCENE_FADE_DURATION),
            Actions.run { onClosed.invoke() },
            Actions.removeActor()
        ))
    }

    // the scene stays on the stage after it has played, until it is closed.
    protected fun fadeInAndPlay(stage: Stage, content: Action, onFinished: () -> Unit) {
        stage.addActor(this)
        addAction(Actions.sequence(
            Actions.alpha(0f),
            Actions.fadeIn(SCENE_FADE_DURATION),
            Actions.delay(PAUSE_AFTER_FADE_IN),
            content,
            Actions.run { onFinished.invoke() }
        ))
    }

    protected fun createNumber(text: String, font: BitmapFont, color: Color, label: String?): Table {
        val number = Table()
        number.add(Label(text, LabelStyle(font, color))).row()
        label?.let { number.add(createLabel(it, color)) }
        number.pack()
        number.isTransform = true
        number.setOrigin(Align.center)
        return number
    }

    protected fun showNumberRisingFrom(combatant: CombatantActor, number: Table) {
        showFloatingNumber(combatant, number, offsetX = 0f, startHeightAboveHead = 0f, distanceY = NUMBER_FLOAT_DISTANCE)
    }

    protected fun showNumberSinkingOnto(combatant: CombatantActor, number: Table, offsetX: Float) {
        showFloatingNumber(combatant, number, offsetX,
                           startHeightAboveHead = NUMBER_FLOAT_DISTANCE,
                           distanceY = -(NUMBER_FLOAT_DISTANCE + NUMBER_SINK_DEPTH_INTO_HEAD))
    }

    protected fun createWaitForNumber(numberDelay: Float): Action {
        return Actions.delay(numberDelay + NUMBER_DURATION)
    }

    protected fun createHeroWait(): Action {
        return Actions.run { heroCombatant.playMotion(CombatantMotion.WAIT) }
    }

    private fun createLabel(text: String, color: Color): Label {
        return Label(text, LabelStyle(FontProvider.barlowOutlined24, color))
    }

    private fun showFloatingNumber(combatant: CombatantActor,
                                   number: Table,
                                   offsetX: Float,
                                   startHeightAboveHead: Float,
                                   distanceY: Float) {
        val headY: Float = combatant.y + combatant.height
        number.setPosition(combatant.centerX + offsetX - (number.width / 2f), headY + startHeightAboveHead)
        // the fade-in of the float starts from the current alpha, so the number has to start invisible.
        number.color.a = 0f
        addActor(number)
        number.addAction(createNumberFloat(distanceY))
    }

    private fun createNumberFloat(distanceY: Float): Action {
        return Actions.sequence(
            Actions.parallel(Actions.fadeIn(NUMBER_FADE_DURATION), Actions.scaleTo(1f, 1f, NUMBER_POP_DURATION)),
            Actions.moveBy(0f, distanceY, NUMBER_DURATION - NUMBER_POP_DURATION - NUMBER_FADE_DURATION),
            Actions.fadeOut(NUMBER_FADE_DURATION),
            Actions.removeActor()
        )
    }

}
