package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils.scenario
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState
import nl.t64.cot.sfx.TransitionAction
import nl.t64.cot.sfx.TransitionImage
import nl.t64.cot.sfx.TransitionType


class SceneStartOfCycle3 : CutsceneScreen() {

    private lateinit var mozes: CutsceneActor
    private lateinit var grayScreen: Actor

    override fun prepare() {
        mozes = CutsceneActor.createCharacter("mozes")
        grayScreen = TransitionImage(color = Color.GRAY)

        actorsStage.addActor(mozes)
        transitionStage.addActor(grayScreen)

        actions = listOf(mozesWakesUpAgain(),
                         toLastdennThen(),
                         startThirdCycle())
    }

    private fun mozesWakesUpAgain(): Action {
        return Actions.sequence(
            Actions.run {
                grayScreen.setPosition(-600f, 0f)
                grayScreen.isVisible = true
                setMapWithBgsOnly("honeywood_house_mozes")
                setFixedCameraPosition(0f, 720f)
                mozes.isVisible = true
                mozes.setPosition(456f, 534f)
                mozes.entityState = EntityState.IDLE
                mozes.direction = Direction.SOUTH
            },
            Actions.delay(0.5f),

            actionFadeIn(),
            Actions.addAction(TransitionAction(TransitionType.FADE_IN), grayScreen),

            Actions.delay(0.5f),
            Actions.run { playSe(AudioEvent.SE_SAVE_GAME) },
            Actions.delay(1.5f),
            Actions.run { showConversationDialog("mozes_wakes_up_again", "mozes") }
        )
    }

    private fun toLastdennThen(): Action {
        return Actions.sequence(
            Actions.delay(2f),
            Actions.run { mozes.direction = Direction.EAST },
            Actions.run { mozes.entityState = EntityState.WALKING },
            Actions.moveBy(48f, 0f, 2f),
            Actions.run { mozes.entityState = EntityState.IDLE },
            Actions.delay(0.5f),
            Actions.run { mozes.direction = Direction.SOUTH },
            Actions.delay(2f),
            Actions.run { showConversationDialog("to_lastdenn_then", "mozes") }
        )
    }

    private fun startThirdCycle(): Action {
        return Actions.sequence(
            Actions.delay(0.5f),
            Actions.run { exitScreen() }
        )
    }

    override fun exitScreen() {
        endCutsceneAndOpenMapAnd("honeywood_house_mozes") { scenario.startThirdCycle() }
    }

}
