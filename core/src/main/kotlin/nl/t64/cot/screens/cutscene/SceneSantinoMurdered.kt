package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playBgm
import nl.t64.cot.audio.playSe
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState
import nl.t64.cot.sfx.TransitionAction
import nl.t64.cot.sfx.TransitionImage
import nl.t64.cot.sfx.TransitionType


class SceneSantinoMurdered : CutsceneScreen() {

    private lateinit var garrin: CutsceneActor
    private lateinit var ghost: CutsceneActor
    private lateinit var santino: CutsceneActor
    private lateinit var santinoDead: Image
    private lateinit var guard1: CutsceneActor
    private lateinit var guard2: CutsceneActor
    private lateinit var bloodFlash: Actor

    override fun prepare() {
        garrin = CutsceneActor.createCharacter("man12")
        ghost = CutsceneActor.createCharacter("ghost1")
        santino = CutsceneActor.createCharacter("santino")
        santinoDead = Utils.createImage("sprites/characters/damage3.png", 144, 240, 48, 48)
        guard1 = CutsceneActor.createCharacter("soldier01")
        guard2 = CutsceneActor.createCharacter("soldier12")
        bloodFlash = TransitionImage(color = Color.RED)

        actorsStage.addActor(garrin)
        actorsStage.addActor(ghost)
        actorsStage.addActor(santino)
        actorsStage.addActor(santinoDead)
        actorsStage.addActor(guard1)
        actorsStage.addActor(guard2)
        transitionStage.addActor(bloodFlash)

        actions = listOf(start(),
                         guardsWalkDown(),
                         santinoDies(),
                         guardsSeeGarrin(),
                         end())
    }

    private fun start(): Action {
        return Actions.sequence(
            Actions.run {
                setMapWithBgsOnly("lastdenn")
                playBgm(AudioEvent.BGM_MURDER)
                setFixedCameraPosition(840f, 960f)
                garrin.isVisible = true
                garrin.setPosition(816f, 984f)
                garrin.direction = Direction.SOUTH
                ghost.isVisible = false
                ghost.setPosition(816f, 936f)
                ghost.direction = Direction.SOUTH
                ghost.entityState = EntityState.CRAWLING
                santinoDead.isVisible = false
                santinoDead.setPosition(816f, 900f)
                santino.isVisible = true
                santino.setPosition(816f, 936f)
                santino.direction = Direction.NORTH
                guard1.isVisible = true
                guard1.setPosition(792f, 1296f)
                guard1.direction = Direction.SOUTH
                guard2.isVisible = true
                guard2.setPosition(840f, 1296f)
                guard2.direction = Direction.SOUTH
                bloodFlash.isVisible = false
            },

            actionFadeIn(),

            Actions.delay(2f),

            Actions.run { showConversationDialog("garrin_vs_santino_1", "santino") }
        )
    }

    private fun guardsWalkDown(): Action {
        return Actions.sequence(
            Actions.addAction(Actions.sequence(
                Actions.delay(3f),
                Actions.run { guard1.entityState = EntityState.WALKING },
                Actions.moveBy(0f, -240f, 9f),
                Actions.run { guard1.entityState = EntityState.IDLE },
            ), guard1),
            Actions.addAction(Actions.sequence(
                Actions.delay(3f),
                Actions.run { guard2.entityState = EntityState.WALKING },
                Actions.moveBy(0f, -216f, 9.2f),
                Actions.run { guard2.entityState = EntityState.IDLE },
            ), guard2),
            Actions.delay(0.4f),
            Actions.addAction(Actions.sequence(
                Actions.run { garrin.entityState = EntityState.WALKING },
                Actions.moveBy(0f, -48f, 1f),
                Actions.run { garrin.entityState = EntityState.IDLE }
            ), garrin),
            Actions.delay(0.2f),
            Actions.addAction(Actions.sequence(
                Actions.run { santino.entityState = EntityState.WALKING },
                Actions.moveBy(0f, -24f, 1f),
                Actions.run { santino.entityState = EntityState.IDLE }
            ), santino),
            actionWalkSound(garrin, 1f, NORMAL_STEP),

            Actions.delay(0.4f),

            Actions.run { showConversationDialog("garrin_vs_santino_2", "ghost1") },

            Actions.delay(3f),

            actionWalkSound(guard2, 7f, NORMAL_STEP),
        )
    }

    private fun santinoDies(): Action {
        return Actions.sequence(
            Actions.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.visible(true),
                Actions.delay(0.2f),
                Actions.run { playSe(AudioEvent.SE_MAGIC_BANG) },
                Actions.parallel(
                    Actions.repeat(6, Actions.sequence(
                        Actions.addAction(TransitionAction(TransitionType.FADE_OUT, 0.05f), bloodFlash),
                        Actions.delay(0.05f),
                        Actions.addAction(TransitionAction(TransitionType.FADE_IN, 0.05f), bloodFlash),
                        Actions.delay(0.05f),
                    )),
                    Actions.sequence(
                        Actions.delay(0.1f),
                        Actions.run {
                            santino.isVisible = false
                            santinoDead.isVisible = true
                        }
                    )
                )
            ), bloodFlash),

            Actions.delay(4f),

            Actions.run { ghost.isVisible = true },

            Actions.delay(3f),

            Actions.addAction(Actions.sequence(
                Actions.run { ghost.direction = Direction.WEST },
                Actions.moveBy(-500f, 0f, 20f),
            ), ghost),

            Actions.delay(1f),

            Actions.run { showConversationDialog("garrin_vs_santino_dead", "man12") },
        )
    }

    private fun guardsSeeGarrin(): Action {
        return Actions.sequence(
            Actions.delay(1f),
            Actions.run { showConversationDialog("guards_see_garrin", "soldier01") },
            Actions.delay(1f),
            Actions.run { garrin.direction = Direction.NORTH },
        )
    }

    private fun end(): Action {
        return Actions.sequence(
            Actions.delay(2f),
            Actions.run { exitScreen() }
        )
    }


    override fun exitScreen() {
        gameData.clock.setTimeOfDay("14:00")
        endCutsceneAndOpenMap("lastdenn", "scene_santino_murdered", delay = 2f)
    }

}
