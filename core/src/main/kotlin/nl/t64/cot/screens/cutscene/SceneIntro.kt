package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playBgm
import nl.t64.cot.audio.playSe
import nl.t64.cot.audio.stopAllBgm
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState
import nl.t64.cot.sfx.TransitionAction
import nl.t64.cot.sfx.TransitionType


class SceneIntro : CutsceneScreen() {

    private lateinit var door1: CutsceneActor
    private lateinit var mozes: CutsceneActor
    private lateinit var grace: CutsceneActor
    private lateinit var oldWoman: CutsceneActor

    override fun prepare() {
        door1 = CutsceneActor.createDoor("door_simple_left3")
        mozes = CutsceneActor.createCharacter("mozes")
        grace = CutsceneActor.createCharacter("girl01")
        oldWoman = CutsceneActor.createCharacter("oldwoman01")
        actorsStage.addActor(door1)
        actorsStage.addActor(mozes)
        actorsStage.addActor(grace)
        actorsStage.addActor(oldWoman)
        actions = listOf(callGraceToBed(),
                         walkIntoTheHouse(),
                         graceSneaksOutside(),
                         startGame())
    }

    private fun callGraceToBed(): Action {
        return Actions.sequence(
            Actions.run {
                setMapWithBgmBgs("honeywood")
                setFixedCameraPosition(0f, 1380f)
                title.setText("""
                    The land of Adan

                    Honeywood Village

                    582 AD""".trimIndent())
                title.setPosition(camera.position.x - (title.width / 2f), camera.position.y)
                door1.setPosition(528f, 1344f)
                mozes.setPosition(528f, 1344f)
                grace.setPosition(200f, 1310f)
                grace.entityState = EntityState.RUNNING
                grace.isVisible = true
            },
            Actions.delay(1f),
            Actions.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.visible(true),
                Actions.fadeIn(Constant.FADE_DURATION),
                Actions.delay(5f),
                Actions.fadeOut(Constant.FADE_DURATION),
                Actions.visible(false),
                Actions.alpha(1f)
            ), title),
            Actions.delay(7f),

            Actions.addAction(TransitionAction(TransitionType.FADE_IN), transition),

            Actions.addAction(Actions.sequence(
                Actions.parallel(
                    Actions.repeat(6, Actions.sequence(
                        Actions.moveBy(0f, -48f, 0.5f),
                        Actions.run { grace.direction = Direction.EAST },
                        Actions.moveBy(48f, 0f, 0.5f),
                        Actions.run { grace.direction = Direction.NORTH },
                        Actions.moveBy(0f, 48f, 0.5f),
                        Actions.run { grace.direction = Direction.WEST },
                        Actions.moveBy(-48f, 0f, 0.5f),
                        Actions.run { grace.direction = Direction.SOUTH }
                    )),
                    actionWalkSound(grace, 12f, FAST_STEP)
                ),
                Actions.run { grace.entityState = EntityState.IDLE },
                Actions.run { grace.direction = Direction.EAST }
            ), grace),
            Actions.addAction(Actions.sequence(
                Actions.delay(5f),
                Actions.run {
                    door1.entityState = EntityState.OPENED
                    playSe(AudioEvent.SE_SMALL_DOOR)
                }
            ), door1),
            Actions.addAction(Actions.sequence(
                Actions.delay(6f),
                Actions.alpha(0f),
                Actions.visible(true),
                Actions.fadeIn(0.3f),
                Actions.delay(0.5f),
                Actions.run { mozes.entityState = EntityState.WALKING },
                Actions.moveBy(0f, -24f, 1f),
                Actions.run { mozes.entityState = EntityState.IDLE },
                Actions.delay(0.3f),
                Actions.run { mozes.direction = Direction.EAST },
                Actions.delay(1f),
                Actions.run { mozes.direction = Direction.SOUTH },
                Actions.delay(1f),
                Actions.run { mozes.direction = Direction.WEST },
                Actions.delay(1f),
                Actions.run { showConversationDialog("mozes_calls_grace_inside", "mozes") }
            ), mozes)
        )
    }

    private fun walkIntoTheHouse(): Action {
        return Actions.sequence(
            Actions.delay(1f),
            Actions.addAction(Actions.sequence(
                Actions.run { grace.entityState = EntityState.WALKING },
                actionMoveBy(grace, 100f, 0f, 5f, NORMAL_STEP),
                Actions.run { grace.entityState = EntityState.IDLE },
                Actions.delay(0.5f),
                Actions.run { grace.direction = Direction.WEST },
                Actions.delay(1f),
                Actions.run { grace.direction = Direction.EAST },
                Actions.delay(1f),
                Actions.parallel(
                    Actions.sequence(
                        Actions.run { grace.entityState = EntityState.WALKING },
                        Actions.moveTo(528f, 1310f, 6f),
                        Actions.run { grace.direction = Direction.NORTH },
                        Actions.moveTo(528f, 1344f, 1f)
                    ),
                    actionWalkSound(grace, 7f, NORMAL_STEP)
                ),
                Actions.fadeOut(0.3f)
            ), grace),
            Actions.addAction(Actions.sequence(
                Actions.delay(9f),
                Actions.run { mozes.direction = Direction.NORTH },
                Actions.run { mozes.entityState = EntityState.WALKING },
                Actions.moveBy(0f, 24f, 1f),
                Actions.fadeOut(0.3f)
            ), mozes),
            Actions.delay(16f),

            actionFadeOut(),

            Actions.delay(1f),
            Actions.run {
                setMapWithBgmBgs("honeywood_house_mozes")
                setFixedCameraPosition(0f, 720f)
                door1.isVisible = false
                mozes.setPosition(360f, 528f)
                mozes.entityState = EntityState.IDLE
                mozes.direction = Direction.WEST
                grace.setPosition(312f, 534f)
                grace.entityState = EntityState.IDLE
                grace.direction = Direction.EAST
            },
            Actions.addAction(Actions.alpha(1f), mozes),
            Actions.addAction(Actions.alpha(1f), grace),

            actionFadeIn(),

            Actions.delay(1f),
            Actions.run { showConversationDialog("mozes_puts_grace_to_bed", "mozes") }
        )
    }

    private fun graceSneaksOutside(): Action {
        return Actions.sequence(
            Actions.delay(1.5f),

            actionFadeOut(),

            Actions.run {
                mozes.setPosition(456f, 524f)
                mozes.direction = Direction.SOUTH
                grace.direction = Direction.SOUTH
            },
            Actions.delay(2f),
            Actions.addAction(Actions.alpha(0.7f), transition),
            Actions.run {
                stopAllBgm()
                playBgm(AudioEvent.BGM_CELLAR)
            },
            Actions.addAction(Actions.sequence(
                Actions.delay(4f),
                Actions.run { grace.direction = Direction.EAST },
                Actions.delay(4f),
                Actions.run { grace.entityState = EntityState.WALKING },
                Actions.moveBy(48f, 0f, 2f),
                Actions.run { grace.entityState = EntityState.IDLE },
                Actions.delay(2f),
                Actions.run { grace.direction = Direction.SOUTH },
                Actions.run { grace.entityState = EntityState.WALKING },
                Actions.moveBy(0f, -72f, 2f),
                Actions.run { grace.direction = Direction.EAST },
                Actions.moveBy(144f, 0f, 3.5f),
                Actions.run { grace.entityState = EntityState.IDLE },
                Actions.run { grace.direction = Direction.NORTH },
                Actions.delay(3f),
                Actions.run { grace.direction = Direction.SOUTH },
                Actions.delay(0.5f),
                Actions.run { grace.entityState = EntityState.WALKING },
                Actions.moveBy(-48f, -360f, 7f),
                Actions.run { playSe(AudioEvent.SE_SMALL_DOOR) },
                Actions.delay(3f),
                Actions.visible(false)
            ), grace),
            Actions.delay(32f),

            actionFadeOut(),

            Actions.delay(1f),
            Actions.run {
                playBgm(AudioEvent.BGM_HOUSE)
                mozes.setPosition(456f, 534f)
            },

            actionFadeIn(),

            Actions.addAction(Actions.sequence(
                Actions.delay(2f),
                Actions.run { mozes.direction = Direction.EAST },
                Actions.run { mozes.entityState = EntityState.WALKING },
                Actions.moveBy(48f, 0f, 2f),
                Actions.run { mozes.entityState = EntityState.IDLE },
                Actions.run { mozes.direction = Direction.WEST },
                Actions.run { isBgmFading = true },
                Actions.run { showConversationDialog("mozes_finds_grace_missing", "mozes") }
            ), mozes)
        )
    }

    private fun startGame(): Action {
        return Actions.sequence(
            Actions.run { mozes.direction = Direction.NORTH },
            Actions.delay(0.5f),
            Actions.run { playSe(AudioEvent.SE_CHEST) },
            Actions.delay(2f),
            Actions.run { mozes.direction = Direction.SOUTH },
            Actions.delay(1f),
            Actions.run { exitScreen() }
        )
    }

    override fun exitScreen() {

        // ------ these 2 lines are by far not a solution, but a workaround.
        mapManager.loadMap(Constant.STARTING_MAP) // loading the same map as the map in which the game starts. it needs to be the same map for some reason for BGM_TENSION to be able to play solo after the cutscene, or else BGM_HOUSE will play simultaneously.
        grace.clearActions()    // so that her walking sound stops. so that they won't change from grass into wood because of the change of map where the default is wood.
        // actorsStage.clear()  // this actually isn't such a bad line. it clears the stage when exiting. maybe some day I will use it.
        // ------

        endCutsceneAndOpenMap(Constant.STARTING_MAP, "scene_intro")
    }

}
