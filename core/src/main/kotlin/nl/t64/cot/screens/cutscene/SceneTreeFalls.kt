package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils
import nl.t64.cot.Utils.profileManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState


class SceneTreeFalls : CutsceneScreen() {

    private lateinit var mozes: CutsceneActor

    override fun prepare() {
        mozes = CutsceneActor.createCharacter("mozes")
        actorsStage.addActor(mozes)
        actions = listOf(searchNorth(),
                         moveToFallenTree(),
                         continueGame())
    }

    private fun searchNorth(): Action {
        return Actions.sequence(
            Actions.run {
                setMapWithNoSound("honeywood_forest_path")
                mozes.setPosition(480f, -48f)
                followActor(mozes)
                mozes.isVisible = true
                mozes.entityState = EntityState.RUNNING
                mozes.direction = Direction.NORTH
            },
            Actions.delay(1f),

            actionFadeIn(),

            Actions.addAction(Actions.sequence(
                actionMoveBy(mozes, 0f, 500f, 3f, FAST_STEP),
                Actions.run { mozes.entityState = EntityState.IDLE },
                Actions.delay(1f),
                Actions.run { mozes.direction = Direction.SOUTH },
                Actions.delay(1f),
                Actions.run { showConversationDialog("tree_has_fallen_1", "mozes") }
            ), mozes),
            Actions.addAction(Actions.sequence(
                Actions.delay(2.3f),
                Actions.run { isBgmFading = true },
                Actions.run { playSe(AudioEvent.SE_BANG) },
                Actions.delay(0.5f),
                Actions.run { camera.startShaking() },
                Actions.run { Utils.mapManager.updateConditionLayers() },
                Actions.run { isBgmFading = false }
            ))
        )
    }

    private fun moveToFallenTree(): Action {
        return Actions.sequence(
            Actions.addAction(Actions.sequence(
                Actions.delay(1f),
                Actions.run { mozes.entityState = EntityState.WALKING },
                actionMoveTo(mozes, 480f, 60f, 5f, NORMAL_STEP),
                Actions.run { mozes.entityState = EntityState.IDLE },
                Actions.delay(1f),
                Actions.run { showConversationDialog("tree_has_fallen_2", "mozes") }
            ), mozes)
        )
    }

    private fun continueGame(): Action {
        return Actions.sequence(
            Actions.delay(0.5f),
            Actions.run { exitScreen() }
        )
    }

    override fun exitScreen() {
        endCutsceneAndOpenMapAnd("honeywood_forest_path", "scene_tree_falls") { profileManager.saveProfile() }
    }

}
