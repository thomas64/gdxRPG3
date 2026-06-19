package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playBgm
import nl.t64.cot.audio.playSe
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState


class SceneGhostPossessesGarrin : CutsceneScreen() {

    private lateinit var garrin: CutsceneActor
    private lateinit var garrinLying: CutsceneActor
    private lateinit var paton: CutsceneActor
    private lateinit var ghost: CutsceneActor

    override fun prepare() {
        garrin = CutsceneActor.createCharacter("man12")
        garrinLying = CutsceneActor.createCharacter("man12_dead")
        paton = CutsceneActor.createCharacter("boy17")
        ghost = CutsceneActor.createCharacter("ghost1")

        actorsStage.addActor(garrin)
        actorsStage.addActor(garrinLying)
        actorsStage.addActor(paton)
        actorsStage.addActor(ghost)

        actions = listOf(start(),
                         ghostContinues(),
                         garrinWalksBack(),
                         patonRunsToGarrin(),
                         garrinPossessed(),
                         patonRunsOutAndEnd())
    }

    private fun start(): Action {
        return Actions.sequence(
            Actions.run {
                setMapWithBgsOnly("lastdenn_house_garrin")
                playBgm(AudioEvent.BGM_GHOST)
                setFixedCameraPosition(0f, 460f)

                garrinLying.isVisible = false
                garrinLying.setPosition(276f, 444f)
                garrinLying.direction = Direction.WEST

                garrin.isVisible = true
                garrin.setPosition(324f, 456f)
                garrin.direction = Direction.EAST

                paton.isVisible = true
                paton.setPosition(240f, 120f)
                paton.direction = Direction.NORTH

                ghost.isVisible = true
                ghost.setPosition(528f, 456f)
                ghost.direction = Direction.WEST
                ghost.entityState = EntityState.CRAWLING
            },

            actionFadeIn(),

            Actions.delay(1f),

            Actions.run { showConversationDialog("garrin_takes_a_stand", "man12") }
        )
    }

    private fun ghostContinues(): Action {
        return Actions.sequence(
            Actions.addAction(Actions.sequence(
                Actions.moveBy(-120f, 0f, 7f),
            ), ghost),

            Actions.delay(7f),

            Actions.run { showConversationDialog("garrin_steps_back", "man12") },
        )
    }

    private fun garrinWalksBack(): Action {
        return Actions.sequence(
            Actions.addAction(Actions.sequence(
                Actions.run { garrin.entityState = EntityState.WALKING },
                Actions.moveBy(-48f, 0f, 6f),
            ), garrin),
            Actions.addAction(Actions.sequence(
                Actions.moveBy(-132f, 0f, 6f),
            ), ghost),
            Actions.delay(5.5f),
            Actions.run {
                camera.startShaking()
                playSe(AudioEvent.SE_MAGIC_BANG)
            },
            Actions.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.fadeIn(0.05f),
                Actions.delay(0.05f),
                Actions.run {
                    garrin.isVisible = false
                    ghost.isVisible = false
                    garrinLying.isVisible = true
                },
                Actions.alpha(0f),
            ), transition),

            Actions.delay(1f),

            Actions.run { showConversationDialog("paton_shouts_nooo", "boy17") }
        )
    }

    private fun patonRunsToGarrin(): Action {
        return Actions.sequence(
            Actions.addAction(Actions.sequence(
                Actions.run { paton.entityState = EntityState.RUNNING },
                Actions.parallel(
                    Actions.moveBy(0f, 324f, 3f),
                    Actions.sequence(
                        Actions.delay(2.95f),
                        Actions.run { paton.direction = Direction.EAST }
                    )
                ),
                Actions.run { paton.entityState = EntityState.IDLE }
            ), paton),
            Actions.addAction(actionWalkSound(paton, 3f, 0.25f), paton),

            Actions.delay(4f),

            Actions.run { showConversationDialog("paton_runs_to_garrin", "boy17") }
        )
    }

    private fun garrinPossessed(): Action {
        return Actions.sequence(
            Actions.delay(1f),
            Actions.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.fadeIn(0.2f),
                Actions.run {
                    garrinLying.isVisible = false
                    garrin.isVisible = true
                    garrin.setPosition(276f, 444f)
                    garrin.direction = Direction.NORTH
                    garrin.entityState = EntityState.IDLE
                },
                Actions.delay(0.2f),
                Actions.fadeOut(0.2f),
            ), transition),
            Actions.delay(3f),
            Actions.run { showConversationDialog("ghost_has_garrin", "ghost1") }
        )
    }

    private fun patonRunsOutAndEnd(): Action {
        return Actions.sequence(
            Actions.addAction(Actions.sequence(
                Actions.run {
                    paton.direction = Direction.SOUTH
                    paton.entityState = EntityState.RUNNING
                },
                Actions.moveBy(0f, -324f, 3f),
            ), paton),
            Actions.addAction(actionWalkSound(paton, 3f, 0.25f), paton),
            Actions.delay(5f),
            Actions.run { exitScreen() }
        )
    }

    override fun exitScreen() {
        gameData.clock.setTimeOfDay("11:00")
        gameData.quests.getQuestById("quest_lastdenn_garrin").setTaskComplete("2") // "_2_"
        endCutsceneAndOpenMap("lastdenn_house_garrin", "scene_ghost_possesses_garrin")
    }

}
