package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import nl.t64.cot.Utils
import nl.t64.cot.Utils.scenario
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState


class SceneEndOfCycle2 : CutsceneScreen() {

    private lateinit var mozes: CutsceneActor
    private lateinit var mozesDead: Image

    override fun prepare() {
        mozes = CutsceneActor.createCharacter("mozes")
        mozesDead = Utils.createImage("sprites/characters/damage1.png", 0, 0, 48, 48)

        actorsStage.addActor(mozes)
        actorsStage.addActor(mozesDead)

        actions = listOf(goneToHeaven(),
                         mozesWakesUpAgain(),
                         stepOutOfBed(),
                         startThirdCycle())
    }

    private fun goneToHeaven(): Action {
        return Actions.sequence(
            Actions.run {
                mozes.isVisible = false
                mozesDead.setPosition(456f, 246f)
                mozesDead.isVisible = true
                setMapWithBgmBgs("ylarus_place")
                setFixedCameraPosition(0f, 0f)
            },

            actionFadeIn(),

            Actions.delay(4f),
            Actions.run { showConversationDialog("final_chance", "ylarus", Color.BLACK) }
        )
    }

    private fun mozesWakesUpAgain(): Action {
        return Actions.sequence(
            Actions.delay(4f),

            actionFadeOut(),

            Actions.run {
                mozesDead.isVisible = false
                setMapWithBgsOnly("honeywood_house_mozes")
                setFixedCameraPosition(0f, 720f)
                mozes.isVisible = true
                mozes.setPosition(456f, 534f)
                mozes.entityState = EntityState.IDLE
                mozes.direction = Direction.SOUTH
            },
            Actions.delay(1f),
            Actions.run { playSe(AudioEvent.SE_SAVE_GAME) },

            actionFadeIn(),

            Actions.delay(1f),
            Actions.run { showConversationDialog("mozes_wakes_up_cycle_3", "mozes") }
        )
    }

    private fun stepOutOfBed(): Action {
        return Actions.sequence(
            Actions.delay(2f),
            Actions.run { mozes.direction = Direction.EAST },
            Actions.run { mozes.entityState = EntityState.WALKING },
            Actions.moveBy(48f, 0f, 2f),
            Actions.run { mozes.entityState = EntityState.IDLE },
            Actions.run { mozes.direction = Direction.SOUTH },
            Actions.run { showConversationDialog("out_of_bed_cycle_3", "mozes") }
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
