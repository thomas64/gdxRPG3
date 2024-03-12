package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils.scenario
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState


class SceneStartOfCycle3 : CutsceneScreen() {

    private lateinit var mozes: CutsceneActor

    override fun prepare() {
        mozes = CutsceneActor.createCharacter("mozes")

        actorsStage.addActor(mozes)

        actions = listOf(mozesWakesUpAgain(),
                         toLastdennThen(),
                         startThirdCycle())
    }

    private fun mozesWakesUpAgain(): Action {
        return Actions.sequence(
            Actions.run {
                setMapWithBgsOnly("honeywood_house_mozes")
                setCameraPosition(0f, 720f)
                mozes.isVisible = true
                mozes.setPosition(192f, 534f)
                mozes.entityState = EntityState.IDLE
                mozes.direction = Direction.SOUTH
            },
            Actions.delay(0.5f),

            actionFadeIn(),

            Actions.delay(0.5f),
            Actions.run { playSe(AudioEvent.SE_SAVE_GAME) },
            Actions.delay(1f),
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
