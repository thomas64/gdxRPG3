package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import nl.t64.cot.Utils
import nl.t64.cot.Utils.scenario
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playBgs
import nl.t64.cot.audio.playSe
import nl.t64.cot.audio.stopSe
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState
import nl.t64.cot.sfx.TransitionAction
import nl.t64.cot.sfx.TransitionImage
import nl.t64.cot.sfx.TransitionType
import kotlin.random.Random


class SceneEndOfCycle1 : CutsceneScreen() {

    var areGeneralsAlive: Boolean = true

    private lateinit var mozes: CutsceneActor
    private lateinit var mozesDead: Image
    private lateinit var grace: CutsceneActor
    private lateinit var graceDead: Image
    private lateinit var ardor: CutsceneActor
    private lateinit var guard1: CutsceneActor
    private lateinit var guard2: CutsceneActor
    private lateinit var magic: Image
    private lateinit var bloodFlash: Actor
    private lateinit var flames: List<CutsceneActor>

    override fun prepare() {
        mozes = CutsceneActor.createCharacter("mozes")
        mozesDead = Utils.createImage("sprites/characters/damage1.png", 0, 0, 48, 48)
        graceDead = Utils.createImage("sprites/characters/damage2.png", 288, 240, 48, 48)
        grace = CutsceneActor.createCharacter("girl01")
        ardor = CutsceneActor.createCharacter("ardor")
        if (areGeneralsAlive) {
            guard1 = CutsceneActor.createCharacter("orc_general")
            guard2 = CutsceneActor.createCharacter("orc_general")
        }
        magic = Utils.createImage("sprites/objects/magic_inside_d2.png", 0, 96, 144, 144)
        bloodFlash = TransitionImage(color = Color.RED)
        flames = List(800) { CutsceneActor.createFlame() }

        actorsStage.addActor(mozes)
        actorsStage.addActor(magic)
        actorsStage.addActor(grace)
        actorsStage.addActor(graceDead)
        actorsStage.addActor(ardor)
        actorsStage.addActor(mozesDead)
        if (areGeneralsAlive) {
            actorsStage.addActor(guard2)
            actorsStage.addActor(guard1)
        }
        transitionStage.addActor(bloodFlash)
        flames.forEach { transitionStage.addActor(it) }

        actions = listOf(mozesIsDefeated(),
                         ardorContinuesToPray(),
                         graceDies(),
                         if (areGeneralsAlive) everythingWentWrong() else everythingWentWrongWithoutGuards(),
                         fireKillsAll(),
                         mozesWakesUpAgain(),
                         stepOutOfBed(),
                         startSecondCycle()
        )
    }

    private fun mozesIsDefeated(): Action {
        return Actions.sequence(
            Actions.run {
                setMapWithBgsOnly("honeywood_forest_ardor_3")
                setFixedCameraPosition(0f, 0f)
                mozes.isVisible = false
                ardor.setPosition(456f, 240f)
                ardor.direction = Direction.SOUTH
                ardor.isVisible = true
                grace.setPosition(456f, 315f)
                grace.direction = Direction.SOUTH
                grace.isVisible = true
                graceDead.setPosition(456f, 305f)
                graceDead.isVisible = false
                mozesDead.setPosition(456f, 160f)
                mozesDead.isVisible = true
                if (areGeneralsAlive) {
                    guard2.setPosition(430f, 210f)
                    guard2.direction = Direction.SOUTH
                    guard2.isVisible = true
                    guard1.setPosition(482f, 210f)
                    guard1.direction = Direction.SOUTH
                    guard1.isVisible = true
                }
                magic.setScale(0.9f)
                magic.setPosition(414f, 262f)
                magic.isVisible = false
                bloodFlash.isVisible = false
                flames.forEachIndexed { i, it ->
                    it.setPosition(i % 12 * 100f - 100f,
                                   Random.nextInt(800, 2000).toFloat())
                }
            },

            actionFadeIn(),

            Actions.delay(1f),
            Actions.run { showConversationDialog("mozes_last_words", "mozes") }
        )
    }

    private fun ardorContinuesToPray(): Action {
        return Actions.sequence(
            Actions.delay(0.5f),
            Actions.run { ardor.direction = Direction.NORTH },
            Actions.delay(2f),
            Actions.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.visible(true),
                Actions.delay(1f),
                Actions.run { playSe(AudioEvent.SE_MAGIC, true) },
                Actions.fadeIn(8f)
            ), magic),
            Actions.addAction(Actions.sequence(
                Actions.delay(2f),
                Actions.run { if (areGeneralsAlive) guard1.direction = Direction.NORTH },
            )),
            Actions.addAction(Actions.sequence(
                Actions.delay(3f),
                Actions.run { if (areGeneralsAlive) guard2.direction = Direction.NORTH },
            )),
            Actions.run { showConversationDialog("ardor_continues_to_pray", "ardor") },
        )
    }

    private fun graceDies(): Action {
        return Actions.sequence(
            Actions.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.visible(true),
                Actions.delay(0.5f),
                Actions.run { playSe(AudioEvent.SE_MAGIC_BANG) },
                Actions.parallel(
                    Actions.repeat(6, Actions.sequence(
                        Actions.addAction(TransitionAction(TransitionType.FADE_OUT, 0.05f), bloodFlash),
                        Actions.delay(0.05f),
                        Actions.addAction(TransitionAction(TransitionType.FADE_IN, 0.05f), bloodFlash),
                        Actions.delay(0.05f),
                    )),
                    Actions.run {
                        grace.isVisible = false
                        graceDead.isVisible = true
                    },
                )
            ), bloodFlash),
            Actions.delay(3f),
            Actions.run { showConversationDialog("grace_dies", "ardor") },
        )
    }

    private fun everythingWentWrong(): Action {
        return Actions.sequence(
            Actions.run { stopSe(AudioEvent.SE_MAGIC) },
            Actions.run { playBgs(AudioEvent.BGS_QUAKE) },
            Actions.addAction(Actions.sequence(
                Actions.delay(4.5f),
                Actions.run { ardor.entityState = EntityState.WALKING },
                Actions.moveBy(0f, -30f, 4f),
                Actions.run { ardor.entityState = EntityState.IDLE }
            ), ardor),
            Actions.addAction(Actions.sequence(
                Actions.delay(4f),
                Actions.run { guard1.direction = Direction.SOUTH },
                Actions.run { guard1.entityState = EntityState.RUNNING },
                Actions.moveBy(0f, -300f, 3f),
            ), guard1),
            Actions.addAction(Actions.sequence(
                Actions.delay(5f),
                Actions.run { guard2.direction = Direction.SOUTH },
                Actions.run { guard2.entityState = EntityState.RUNNING },
                Actions.moveBy(0f, -300f, 3f),
            ), guard2),
            Actions.delay(8.6f),
            Actions.run { showConversationDialog("what_went_wrong", "ardor") }
        )
    }

    private fun everythingWentWrongWithoutGuards(): Action {
        return Actions.sequence(
            Actions.run { stopSe(AudioEvent.SE_MAGIC) },
            Actions.run { playBgs(AudioEvent.BGS_QUAKE) },
            Actions.addAction(Actions.sequence(
                Actions.delay(4.5f),
                Actions.run { ardor.entityState = EntityState.WALKING },
                Actions.moveBy(0f, -30f, 4f),
                Actions.run { ardor.entityState = EntityState.IDLE }
            ), ardor),
            Actions.delay(8.6f),
            Actions.run { showConversationDialog("what_went_wrong", "ardor") }
        )
    }

    private fun fireKillsAll(): Action {
        return Actions.sequence(
            Actions.run {
                flames.forEach {
                    val scale = Random.nextInt(2, 5).toFloat()
                    it.addAction(Actions.sequence(
                        Actions.scaleBy(scale, scale),
                        Actions.moveBy(0f, -2600f, 10f)
                    ))
                }
            },
            Actions.delay(6f),

            actionFadeOutWithoutBgmFading(),
            Actions.delay(3f),
            actionFadeOut(),

            Actions.delay(1f),
            Actions.run {
                mozesDead.setPosition(456f, 246f)
                graceDead.isVisible = false
                magic.isVisible = false
                ardor.isVisible = false
                setMapWithBgmBgs("ylarus_place")
                setFixedCameraPosition(0f, 0f)
            },

            actionFadeIn(),

            Actions.delay(4f),
            Actions.run { showConversationDialog("another_chance", "ylarus", Color.BLACK) }
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
            Actions.run { showConversationDialog("mozes_wakes_up_cycle_2", "mozes") }
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
            Actions.run { showConversationDialog("out_of_bed_cycle_2", "mozes") }
        )
    }

    private fun startSecondCycle(): Action {
        return Actions.sequence(
            Actions.delay(0.5f),
            Actions.run { exitScreen() }
        )
    }

    override fun exitScreen() {
        endCutsceneAndOpenMapAnd("honeywood_house_mozes") { scenario.startSecondCycle() }
    }

}
