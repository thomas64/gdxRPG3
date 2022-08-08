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
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState
import nl.t64.cot.sfx.TransitionAction
import nl.t64.cot.sfx.TransitionImage
import nl.t64.cot.sfx.TransitionType


class SceneSantinoMurdered : CutsceneScreen() {

    private lateinit var santino: CutsceneActor
    private lateinit var santinoDead: Image
    private lateinit var garrin: CutsceneActor
    private lateinit var guard1: CutsceneActor
    private lateinit var guard2: CutsceneActor
    private lateinit var bloodFlash: Actor

    override fun prepare() {
        santino = CutsceneActor.createCharacter("santino")
        santinoDead = Utils.createImage("sprites/characters/damage3.png", 144, 240, 48, 48)
        garrin = CutsceneActor.createCharacter("man12")
        guard1 = CutsceneActor.createCharacter("soldier01")
        guard2 = CutsceneActor.createCharacter("soldier01")
        bloodFlash = TransitionImage(color = Color.RED)

        actorsStage.addActor(santino)
        actorsStage.addActor(santinoDead)
        actorsStage.addActor(garrin)
        actorsStage.addActor(guard1)
        actorsStage.addActor(guard2)
        transitionStage.addActor(bloodFlash)

        actions = listOf(start(),
                         conversation("2"),
                         conversation("3"),
                         conversation("4"),
                         guardsWalkDown(),
                         conversation("6"),
                         santinoDies(),
                         guardsSeeGarrin(),
                         end())
    }

    private fun start(): Action {
        return Actions.sequence(
            Actions.run {
                setMapWithBgsOnly("lastdenn")
                playBgm(AudioEvent.BGM_MURDER)
                setCameraPosition(768f, 1296f)
                santinoDead.isVisible = false
                santinoDead.setPosition(576f, 1248f)
                santino.isVisible = true
                santino.setPosition(816f, 1272f)
                santino.direction = Direction.EAST
                garrin.isVisible = true
                garrin.setPosition(864f, 1272f)
                garrin.direction = Direction.WEST
                guard1.isVisible = true
                guard1.setPosition(792f, 1584f)
                guard1.direction = Direction.SOUTH
                guard2.isVisible = true
                guard2.setPosition(840f, 1584f)
                guard2.direction = Direction.SOUTH
                bloodFlash.isVisible = false
            },

            actionFadeIn(),

            Actions.delay(1f),

            Actions.run { showConversationDialog("garrin_vs_santino_1", "") }
        )
    }

    private fun conversation(conversationNumber: String): Action {
        return Actions.sequence(
            Actions.delay(0.4f),
            Actions.addAction(Actions.sequence(
                Actions.run { garrin.entityState = EntityState.WALKING },
                Actions.moveBy(-48f, 0f, 1f),
                Actions.run { garrin.entityState = EntityState.IDLE }
            ), garrin),
            Actions.delay(0.2f),
            Actions.addAction(Actions.sequence(
                Actions.run { santino.entityState = EntityState.WALKING },
                Actions.moveBy(-48f, 0f, 1f),
                Actions.run { santino.entityState = EntityState.IDLE }
            ), santino),
            actionWalkSound(garrin, 1f, NORMAL_STEP),

            Actions.delay(0.4f),

            Actions.run { showConversationDialog("garrin_vs_santino_$conversationNumber", "") }
        )
    }

    private fun guardsWalkDown(): Action {
        return Actions.sequence(
            Actions.addAction(Actions.sequence(
                Actions.run { guard1.entityState = EntityState.WALKING },
                Actions.moveTo(792f, 1272f, 5f),
                Actions.run { guard1.entityState = EntityState.IDLE },
                Actions.run { guard1.direction = Direction.WEST },
            ), guard1),
            Actions.addAction(Actions.sequence(
                Actions.run { guard2.entityState = EntityState.WALKING },
                actionMoveTo(guard2, 840f, 1248f, 5.2f, NORMAL_STEP),
                Actions.run { guard2.entityState = EntityState.IDLE },
                Actions.run { guard2.direction = Direction.WEST },
            ), guard2),
            Actions.delay(0.4f),
            Actions.addAction(Actions.sequence(
                Actions.run { garrin.entityState = EntityState.WALKING },
                Actions.moveBy(-48f, 0f, 1f),
                Actions.run { garrin.entityState = EntityState.IDLE }
            ), garrin),
            Actions.delay(0.2f),
            Actions.addAction(Actions.sequence(
                Actions.run { santino.entityState = EntityState.WALKING },
                Actions.moveBy(-48f, 0f, 1f),
                Actions.run { santino.entityState = EntityState.IDLE }
            ), santino),
            actionWalkSound(garrin, 1f, NORMAL_STEP),

            Actions.delay(0.4f),

            Actions.run { showConversationDialog("garrin_vs_santino_5", "") }
        )
    }

    private fun santinoDies(): Action {
        return Actions.sequence(
            Actions.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.visible(true),
                Actions.delay(0.2f),
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
            Actions.delay(1.5f),
            Actions.run { showConversationDialog("garrin_vs_santino_dead", "") },
        )
    }

    private fun guardsSeeGarrin(): Action {
        return Actions.sequence(
            Actions.delay(1f),
            Actions.run { garrin.direction = Direction.EAST },
            Actions.run { showConversationDialog("guards_see_garrin", "") },
        )
    }

    private fun end(): Action {
        return Actions.sequence(
            Actions.delay(0.5f),
            Actions.run { exitScreen() }
        )
    }


    override fun exitScreen() {
        endCutsceneAndOpenMapAnd("lastdenn", "scene_santino_murdered") {
            gameData.clock.setTimeOfDay("14:00")
        }
    }

}
