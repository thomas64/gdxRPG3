package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playBgm
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState


class SceneHoneywoodFarmTooLate : CutsceneScreen() {

    private lateinit var mozes: CutsceneActor
    private lateinit var isembert: CutsceneActor
    private lateinit var miriel: CutsceneActor
    private lateinit var orc1: CutsceneActor

    override fun prepare() {
        mozes = CutsceneActor.createCharacter("mozes")
        isembert = CutsceneActor.createCharacter("oldman02")
        miriel = CutsceneActor.createCharacter("woman05")
        orc1 = CutsceneActor.createCharacter("orc_scout")

        actorsStage.addActor(isembert)
        actorsStage.addActor(miriel)
        actorsStage.addActor(mozes)
        actorsStage.addActor(orc1)

        actions = listOf(orcVictory(),
                         orcsEscape())
    }

    private fun orcVictory(): Action {
        return Actions.sequence(
            Actions.run {
                setMapWithNoSound("honeywood_stable")
                playBgm(AudioEvent.BGM_ARDOR)
                setFixedCameraPosition(570f, 432f)
                mozes.isVisible = true
                mozes.setPosition(288f, 48f)
                mozes.direction = Direction.NORTH
                isembert.isVisible = true
                isembert.setPosition(342f, 408f)
                isembert.direction = Direction.NORTH
                miriel.isVisible = true
                miriel.setPosition(342f, 380f)
                miriel.direction = Direction.NORTH
                orc1.isVisible = true
                orc1.setPosition(912f, 504f)
                orc1.direction = Direction.NORTH
            },
            actionFadeIn(),
            Actions.addAction(Actions.sequence(
                Actions.run { mozes.entityState = EntityState.WALKING },
                actionMoveBy(mozes, 0f, 288f, 3f, NORMAL_STEP),
                Actions.run { mozes.entityState = EntityState.IDLE }
            ), mozes),
            Actions.delay(4f),
            Actions.run { showConversationDialog("farm_orcs_are_leaving", "orc_scout") }
        )
    }

    private fun orcsEscape(): Action {
        return Actions.sequence(
            Actions.addAction(Actions.sequence(
                Actions.run { orc1.direction = Direction.EAST },
                Actions.delay(0.5f),
                Actions.run { orc1.direction = Direction.WEST },
                Actions.delay(0.5f),
                Actions.run { orc1.direction = Direction.SOUTH },
                Actions.delay(0.5f),
                Actions.run { orc1.entityState = EntityState.RUNNING },
                actionMoveBy(orc1, 0f, -144f, 1.5f, FAST_STEP)
            ), orc1),
            Actions.delay(4f),
            Actions.run { exitScreen() }
        )
    }

    override fun exitScreen() {
        gameData.clock.setTimeOfDay("11:00")

        endCutsceneAndOpenMap("honeywood_stable", "scene_honeywood_too_late")
    }

}
