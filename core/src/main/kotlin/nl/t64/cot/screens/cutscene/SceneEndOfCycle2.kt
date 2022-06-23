package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import nl.t64.cot.Utils
import nl.t64.cot.Utils.scenario
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playBgs
import nl.t64.cot.audio.playSe
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState
import kotlin.random.Random


class SceneEndOfCycle2 : CutsceneScreen() {

    private lateinit var mozes: CutsceneActor
    private lateinit var mozesDead: Image
    private lateinit var ylarus: Image
    private lateinit var flames: List<CutsceneActor>

    override fun prepare() {
        mozes = CutsceneActor.createCharacter("mozes")
        mozesDead = Utils.createImage("sprites/characters/damage1.png", 0, 0, 48, 48)
        ylarus = CutsceneActor.createCharacter("ylarus")
        flames = List(800) { CutsceneActor.createFlame() }

        actorsStage.addActor(mozes)
        actorsStage.addActor(mozesDead)
        actorsStage.addActor(ylarus)
        flames.forEach { transitionStage.addActor(it) }

        actions = listOf(mozesWalksUp(),
                         fireKillsAll(),
                         mozesWakesUpAgain(),
                         toLastdennThen(),
                         startThirdCycle())
    }

    private fun mozesWalksUp(): Action {
        return Actions.sequence(
            Actions.run {
                setMapWithNoSound("honeywood_forest_cave")
                setCameraPosition(0f, 0f)
                mozes.setPosition(456f, -96f)
                mozes.direction = Direction.NORTH
                flames.forEachIndexed { i, it ->
                    it.setPosition(i % 12 * 100f - 100f,
                                   Random.nextInt(800, 2000).toFloat())
                }
                mozes.isVisible = true
                mozesDead.isVisible = false
                ylarus.isVisible = false
            },
            Actions.delay(1f),
            Actions.run { playBgs(AudioEvent.BGS_QUAKE) },

            actionFadeIn(),

            Actions.addAction(Actions.sequence(
                Actions.run { mozes.entityState = EntityState.WALKING },
                actionMoveBy(mozes, 0f, 320f, 4f, NORMAL_STEP),
                Actions.run { mozes.entityState = EntityState.IDLE },
                Actions.delay(1f),
                Actions.run { showConversationDialog("to_late_again", "mozes") }
            ), mozes)
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
            Actions.delay(5f),

            actionFadeOutWithoutBgmFading(),
            Actions.delay(3f),
            actionFadeOut(),

            Actions.delay(1f),
            Actions.run {
                mozes.isVisible = false
                mozesDead.setPosition(456f, 246f)
                mozesDead.isVisible = true
                ylarus.setPosition(456f, 360f)
                setMapWithBgmBgs("ylarus_place")
                setCameraPosition(0f, 0f)
            },

            actionFadeIn(),

            Actions.delay(3f),
            Actions.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.visible(true),
                Actions.fadeIn(3f),
                Actions.delay(3f),
                Actions.run { showConversationDialog("receive_crystal_of_time", "ylarus") }
            ), ylarus)
        )
    }

    private fun mozesWakesUpAgain(): Action {
        return Actions.sequence(
            Actions.delay(2f),
            Actions.addAction(Actions.fadeOut(3f), ylarus),
            Actions.delay(5f),

            actionFadeOut(),

            Actions.run {
                mozesDead.isVisible = false
                setMapWithBgsOnly("honeywood_house_mozes")
                setCameraPosition(0f, 720f)
                mozes.isVisible = true
                mozes.setPosition(192f, 534f)
                mozes.entityState = EntityState.IDLE
                mozes.direction = Direction.SOUTH
            },
            Actions.delay(1f),
            Actions.run { playSe(AudioEvent.SE_SAVE_GAME) },

            actionFadeIn(),

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
