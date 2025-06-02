package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import nl.t64.cot.Utils
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playBgs
import nl.t64.cot.audio.playSe
import nl.t64.cot.audio.stopSe
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState
import nl.t64.cot.sfx.TransitionAction
import nl.t64.cot.sfx.TransitionImage
import nl.t64.cot.sfx.TransitionType
import kotlin.random.Random


class SceneArdorKillingGrace : CutsceneScreen() {

    var areGeneralsAlive: Boolean = true
    var nextScreen: ScreenType? = null

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

    private lateinit var fairy1: CutsceneActor
    private lateinit var fairy2: CutsceneActor
    private lateinit var fairy3: CutsceneActor
    private lateinit var fairy4: CutsceneActor
    private lateinit var fairy5: CutsceneActor
    private lateinit var fairy6: CutsceneActor
    private lateinit var fairy7: CutsceneActor
    private lateinit var fairy8: CutsceneActor

    private lateinit var johanna: CutsceneActor
    private lateinit var lennor: CutsceneActor

    private lateinit var priest: CutsceneActor
    private lateinit var malina: CutsceneActor
    private lateinit var lynette: CutsceneActor
    private lateinit var kaidan: CutsceneActor
    private lateinit var tobin: CutsceneActor

    override fun prepare() {
        isSkippable = true

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

        fairy1 = CutsceneActor.createCharacter("fairy1")
        fairy2 = CutsceneActor.createCharacter("fairy2")
        fairy3 = CutsceneActor.createCharacter("fairy3")
        fairy4 = CutsceneActor.createCharacter("fairy4")
        fairy5 = CutsceneActor.createCharacter("fairy5")
        fairy6 = CutsceneActor.createCharacter("fairy6")
        fairy7 = CutsceneActor.createCharacter("fairy7")
        fairy8 = CutsceneActor.createCharacter("fairy8")

        johanna = CutsceneActor.createCharacter("oldwoman01")
        lennor = CutsceneActor.createCharacter("man13")

        priest = CutsceneActor.createCharacter("priest01")
        malina = CutsceneActor.createCharacter("youngwoman01")
        lynette = CutsceneActor.createCharacter("girl03")
        kaidan = CutsceneActor.createCharacter("boy01")
        tobin = CutsceneActor.createCharacter("boy02")

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

        actorsStage.addActor(fairy1)
        actorsStage.addActor(fairy2)
        actorsStage.addActor(fairy3)
        actorsStage.addActor(fairy4)
        actorsStage.addActor(fairy5)
        actorsStage.addActor(fairy6)
        actorsStage.addActor(fairy7)
        actorsStage.addActor(fairy8)

        actorsStage.addActor(johanna)
        actorsStage.addActor(lennor)

        actorsStage.addActor(priest)
        actorsStage.addActor(malina)
        actorsStage.addActor(lynette)
        actorsStage.addActor(kaidan)
        actorsStage.addActor(tobin)

        actions = listOf(mozesIsDefeated(),
                         ardorContinuesToPray(),
                         graceDies(),
                         if (areGeneralsAlive) everythingWentWrong() else everythingWentWrongWithoutGuards(),
                         fireKillsAll()
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
                Actions.run { stopSe(AudioEvent.SE_MAGIC) },
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

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////

            Actions.run {
                setMapWithNoSound("honeywood_great_tree")
                setFixedCameraPosition(600f, 768f)
                flames.forEachIndexed { i, it ->
                    it.setPosition(i % 12 * 100f - 100f,
                                   Random.nextInt(1568, 2768).toFloat())
                }
                magic.isVisible = false
                graceDead.isVisible = false
                mozesDead.isVisible = false
                ardor.isVisible = false

                fairy1.setPosition(576f, 792f)
                fairy1.direction = Direction.SOUTH
                fairy1.entityState = EntityState.WALKING
                fairy1.isVisible = true

                fairy2.setPosition(528f, 576f)
                fairy2.direction = Direction.NORTH
                fairy2.entityState = EntityState.WALKING
                fairy2.isVisible = true

                fairy3.setPosition(408f, 864f)
                fairy3.direction = Direction.SOUTH
                fairy3.entityState = EntityState.WALKING
                fairy3.isVisible = true

                fairy4.setPosition(816f, 816f)
                fairy4.direction = Direction.SOUTH
                fairy4.entityState = EntityState.WALKING
                fairy4.isVisible = true

                fairy5.setPosition(336f, 480f)
                fairy5.direction = Direction.NORTH
                fairy5.entityState = EntityState.WALKING
                fairy5.isVisible = true

                fairy6.setPosition(882f, 672f)
                fairy6.direction = Direction.WEST
                fairy6.entityState = EntityState.WALKING
                fairy6.isVisible = true

                fairy7.setPosition(768f, 456f)
                fairy7.direction = Direction.WEST
                fairy7.entityState = EntityState.WALKING
                fairy7.isVisible = true

                fairy8.setPosition(408f, 432f)
                fairy8.direction = Direction.NORTH
                fairy8.entityState = EntityState.WALKING
                fairy8.isVisible = true
            },
            Actions.delay(1f),
            actionFadeIn(),

            Actions.delay(1f),
            Actions.run {
                flames.forEach {
                    it.addAction(Actions.sequence(
                        Actions.moveBy(0f, -2600f, 10f)
                    ))
                }
            },
            Actions.delay(6f),
            actionFadeOutWithoutBgmFading(),
            Actions.delay(3f),

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////

            Actions.run {
                setMapWithNoSound("honeywood")
                setFixedCameraPosition(1824f, 1488f)
                flames.forEachIndexed { i, it ->
                    it.setPosition(1300f + (i % 12 * 100f - 100f),
                                   Random.nextInt(2288, 3488).toFloat())
                }
                fairy1.isVisible = false
                fairy2.isVisible = false
                fairy3.isVisible = false
                fairy4.isVisible = false
                fairy5.isVisible = false
                fairy6.isVisible = false
                fairy7.isVisible = false
                fairy8.isVisible = false

                johanna.setPosition(1800f, 1344f)
                johanna.direction = Direction.NORTH
                johanna.entityState = EntityState.IDLE
                johanna.isVisible = true

                lennor.setPosition(2016f, 1392f)
                lennor.direction = Direction.WEST
                lennor.entityState = EntityState.IDLE
                lennor.isVisible = true
            },
            Actions.delay(1f),
            actionFadeIn(),

            Actions.delay(1f),
            Actions.run {
                flames.forEach {
                    it.addAction(Actions.sequence(
                        Actions.moveBy(0f, -2600f, 10f)
                    ))
                }
            },
            Actions.delay(6f),
            actionFadeOutWithoutBgmFading(),
            Actions.delay(3f),

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////

            Actions.run {
                setFixedCameraPosition(1008f, 624f)
                flames.forEachIndexed { i, it ->
                    it.setPosition(500f + (i % 12 * 100f - 100f),
                                   Random.nextInt(1424, 2624).toFloat())
                }
                johanna.isVisible = false
                lennor.isVisible = false

                priest.setPosition(1056f, 600f)
                priest.direction = Direction.SOUTH
                priest.entityState = EntityState.IDLE
                priest.isVisible = true

                malina.setPosition(1152f, 720f)
                malina.direction = Direction.NORTH
                malina.entityState = EntityState.IDLE
                malina.isVisible = true

                lynette.setPosition(828f, 396f)
                lynette.direction = Direction.EAST
                lynette.entityState = EntityState.IDLE
                lynette.isVisible = true

                kaidan.setPosition(864f, 348f)
                kaidan.direction = Direction.NORTH
                kaidan.entityState = EntityState.IDLE
                kaidan.isVisible = true

                tobin.setPosition(900f, 396f)
                tobin.direction = Direction.WEST
                tobin.entityState = EntityState.IDLE
                tobin.isVisible = true
            },
            Actions.delay(1f),
            actionFadeIn(),

            Actions.delay(1f),
            Actions.run {
                flames.forEach {
                    it.addAction(Actions.sequence(
                        Actions.moveBy(0f, -2600f, 10f)
                    ))
                }
            },
            Actions.delay(6f),
            actionFadeOutWithoutBgmFading(),
            Actions.delay(3f),

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////

            actionFadeOut(),

            Actions.delay(1f),
            Actions.run { exitScreen() }
        )
    }

    override fun exitScreen() {
        endCutsceneAnd {
            screenManager.setScreen(nextScreen!!)
            nextScreen = null
        }
    }

}
