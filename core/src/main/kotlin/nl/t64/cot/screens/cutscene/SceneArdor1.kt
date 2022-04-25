package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.battle.BattleScreen
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState


class SceneArdor1 : CutsceneScreen() {

    private lateinit var mozes: CutsceneActor
    private lateinit var grace: CutsceneActor
    private lateinit var ardor: CutsceneActor
    private lateinit var guard1: CutsceneActor
    private lateinit var guard2: CutsceneActor
    private lateinit var magic: Image

    override fun prepare() {
        mozes = CutsceneActor.createCharacter("mozes")
        grace = CutsceneActor.createCharacter("girl01")
        ardor = CutsceneActor.createCharacter("ardor")
        guard1 = CutsceneActor.createCharacter("orc_general")
        guard2 = CutsceneActor.createCharacter("orc_general")
        magic = Utils.createImage("sprites/objects/magic_inside_d2.png", 0, 96, 144, 144)
        actorsStage.addActor(magic)
        actorsStage.addActor(ardor)
        actorsStage.addActor(guard2)
        actorsStage.addActor(grace)
        actorsStage.addActor(guard1)
        actorsStage.addActor(mozes)
        actions = listOf(mozesEntersTheScene(),
                         graceIsCalledInTheCircle(),
                         ardorStartsToPray(),
                         mozesStepsIn(),
                         ardorCommandsToKillMozes(),
                         guardsWalkToMozes())
    }

    private fun mozesEntersTheScene(): Action {
        return Actions.sequence(
            Actions.run {
                setMapWithBgsOnly("honeywood_forest_ardor_3")
                setCameraPosition(0f, 0f)
                ardor.setPosition(456f, 240f)
                ardor.direction = Direction.NORTH
                ardor.isVisible = true
                guard2.setPosition(612f, 330f)
                guard2.direction = Direction.WEST
                guard2.isVisible = true
                grace.setPosition(600f, 315f)
                grace.direction = Direction.WEST
                grace.isVisible = true
                guard1.setPosition(612f, 300f)
                guard1.direction = Direction.WEST
                guard1.isVisible = true
                mozes.setPosition(456f, -96f)
                mozes.direction = Direction.NORTH
                mozes.isVisible = true
                magic.setScale(0.9f)
                magic.setPosition(414f, 262f)
                magic.isVisible = false
            },

            actionFadeIn(),

            Actions.delay(1f),
            Actions.addAction(Actions.sequence(
                Actions.run { mozes.entityState = EntityState.WALKING },
                actionMoveBy(mozes, 0f, 162f, 2f, NORMAL_STEP),
                Actions.run { mozes.entityState = EntityState.IDLE },
                Actions.delay(1f),
                Actions.run { mozes.direction = Direction.EAST },
                Actions.run { mozes.entityState = EntityState.RUNNING },
                actionMoveBy(mozes, 72f, 0f, 0.5f, FAST_STEP),
                Actions.run { mozes.entityState = EntityState.IDLE },
                Actions.run { mozes.direction = Direction.NORTH },
                Actions.delay(1f),
                Actions.run { showConversationDialog("mozes_finds_grace", "mozes") },
                Actions.delay(1f),
                Actions.run { audioManager.handle(AudioCommand.BGM_PLAY_LOOP, AudioEvent.BGM_ARDOR) }
            ), mozes)
        )
    }

    private fun graceIsCalledInTheCircle(): Action {
        return Actions.sequence(
            Actions.delay(1f),
            Actions.addAction(Actions.sequence(
                Actions.run { grace.entityState = EntityState.WALKING },
                actionMoveTo(grace, 456f, 315f, 3f, NORMAL_STEP),
                Actions.run { grace.entityState = EntityState.IDLE },
                Actions.delay(1.5f),
                Actions.run { grace.direction = Direction.SOUTH },
            ), grace),
            Actions.addAction(Actions.sequence(
                Actions.run { guard1.entityState = EntityState.WALKING },
                Actions.moveTo(468f, 300f, 3f),
                Actions.run { guard1.entityState = EntityState.IDLE },
                Actions.delay(0.8f),
                Actions.run { guard1.direction = Direction.EAST },
                Actions.delay(0.5f),
                Actions.run { guard1.entityState = EntityState.WALKING },
                actionMoveTo(guard1, 612f, 300f, 3f, NORMAL_STEP),
                Actions.run { guard1.entityState = EntityState.IDLE },
                Actions.delay(0.5f),
                Actions.run { guard1.direction = Direction.WEST },
            ), guard1),
            Actions.addAction(Actions.sequence(
                Actions.run { guard2.entityState = EntityState.WALKING },
                Actions.moveTo(468f, 330f, 3f),
                Actions.run { guard2.entityState = EntityState.IDLE },
                Actions.delay(1f),
                Actions.run { guard2.direction = Direction.EAST },
                Actions.delay(0.5f),
                Actions.run { guard2.entityState = EntityState.WALKING },
                Actions.moveTo(612f, 330f, 3f),
                Actions.run { guard2.entityState = EntityState.IDLE },
                Actions.delay(0.5f),
                Actions.run { guard2.direction = Direction.WEST },
            ), guard2),
            Actions.addAction(Actions.sequence(
                Actions.delay(3f),
                Actions.run { showConversationDialog("grace_is_no_princess", "mozes") }
            ), mozes)
        )
    }

    private fun ardorStartsToPray(): Action {
        return Actions.sequence(
            Actions.delay(3f),
            Actions.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.visible(true),
                Actions.run { audioManager.handle(AudioCommand.SE_PLAY_LOOP, AudioEvent.SE_MAGIC) },
                Actions.fadeIn(8f)
            ), magic),
            Actions.run { showConversationDialog("ardor_starts_to_pray", "ardor") }
        )
    }

    private fun mozesStepsIn(): Action {
        return Actions.sequence(
            Actions.addAction(Actions.sequence(
                Actions.run { mozes.direction = Direction.WEST },
                Actions.run { mozes.entityState = EntityState.RUNNING },
                Actions.parallel(
                    Actions.sequence(
                        Actions.moveBy(-72f, 0f, 0.5f),
                        Actions.run { mozes.direction = Direction.NORTH },
                        Actions.moveBy(0f, 120f, 0.8f)
                    ),
                    actionWalkSound(mozes, 1.3f, FAST_STEP)
                ),
                Actions.run { mozes.entityState = EntityState.IDLE },
                Actions.run { showConversationDialog("mozes_steps_in", "mozes") }
            ), mozes)
        )
    }

    private fun ardorCommandsToKillMozes(): Action {
        return Actions.sequence(
            Actions.addAction(Actions.sequence(
                Actions.delay(3f),
                Actions.run { isBgmFading = true },
                Actions.run { audioManager.handle(AudioCommand.SE_STOP, AudioEvent.SE_MAGIC) },
                Actions.fadeOut(1f)
            ), magic),
            Actions.addAction(Actions.sequence(
                Actions.delay(3f),
                Actions.run { ardor.direction = Direction.SOUTH },
                Actions.delay(3f),
                Actions.run { showConversationDialog("ardor_commands_to_kill_mozes", "ardor") }
            ), ardor)
        )
    }

    private fun guardsWalkToMozes(): Action {
        return Actions.sequence(
            Actions.delay(0.5f),
            Actions.addAction(Actions.sequence(
                Actions.run { guard1.direction = Direction.SOUTH },
                Actions.run { guard1.entityState = EntityState.RUNNING },
                Actions.moveBy(0f, -90f, 0.8f),
                Actions.run { guard1.direction = Direction.WEST },
                Actions.moveBy(-175f, 0f, 1.2f),
                Actions.run { guard1.entityState = EntityState.IDLE },
                Actions.run { guard1.direction = Direction.SOUTH },
            ), guard1),
            Actions.addAction(Actions.sequence(
                Actions.run { guard2.direction = Direction.SOUTH },
                Actions.run { guard2.entityState = EntityState.RUNNING },
                Actions.moveBy(0f, -120f, 1f),
                Actions.run { guard2.direction = Direction.WEST },
                Actions.moveBy(-140f, 0f, 1f),
                Actions.run { guard2.entityState = EntityState.IDLE },
                Actions.run { guard2.direction = Direction.SOUTH },
            ), guard2),
            actionWalkSound(guard2, 2f, FAST_STEP),
            Actions.delay(0.5f),
            startBattleWithGuards()
        )
    }

    private fun startBattleWithGuards(): Action {
        return Actions.run { exitScreen() }
    }

    override fun onNotifyBattleWon(battleId: String, spoils: Loot) {
        screenManager.setScreen(ScreenType.SCENE_ARDOR_1_WIN)
    }

    override fun onNotifyBattleLost() {
        (screenManager.getScreen(ScreenType.SCENE_ARDOR_1_LOSE) as SceneArdor1Lose).apply { areGeneralsAlive = true }
        screenManager.setScreen(ScreenType.SCENE_ARDOR_1_LOSE)
    }

    override fun exitScreen() {
        endCutsceneAnd { BattleScreen.load("ardor_orc_generals", this) }
    }

}
