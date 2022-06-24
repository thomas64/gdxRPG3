package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.battle.BattleScreen
import nl.t64.cot.screens.loot.SpoilsCutsceneScreen
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState


class SceneArdor2 : CutsceneScreen() {

    private lateinit var mozes: CutsceneActor
    private lateinit var grace: CutsceneActor
    private lateinit var ardor: CutsceneActor
    private lateinit var guard1: CutsceneActor
    private lateinit var guard2: CutsceneActor

    override fun prepare() {
        mozes = CutsceneActor.createCharacter("mozes")
        grace = CutsceneActor.createCharacter("girl01")
        ardor = CutsceneActor.createCharacter("ardor")
        guard1 = CutsceneActor.createCharacter("orc_general")
        guard2 = CutsceneActor.createCharacter("orc_general")
        actorsStage.addActor(ardor)
        actorsStage.addActor(guard2)
        actorsStage.addActor(grace)
        actorsStage.addActor(guard1)
        actorsStage.addActor(mozes)
        actions = listOf(mozesEntersTheScene(),
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
            },

            actionFadeIn(),

            Actions.delay(1f),
            Actions.addAction(Actions.sequence(
                Actions.run { mozes.entityState = EntityState.WALKING },
                actionMoveBy(mozes, 0f, 282f, 3f, NORMAL_STEP),
                Actions.run { mozes.entityState = EntityState.IDLE },
                Actions.delay(1f),
                Actions.run { showConversationDialog("mozes_returns_to_ardor", "mozes") },
            ), mozes)
        )
    }

    private fun ardorCommandsToKillMozes(): Action {
        return Actions.sequence(
            Actions.delay(2f),
            Actions.run { ardor.direction = Direction.SOUTH },
            Actions.delay(3f),
            Actions.run { showConversationDialog("ardor_turns_around", "ardor") }
        )
    }

    private fun guardsWalkToMozes(): Action {
        return Actions.sequence(
            Actions.delay(0.5f),
            Actions.addAction(Actions.sequence(
                Actions.run { grace.entityState = EntityState.RUNNING },
                Actions.moveBy(-20f, 0f, 0.3f),
                Actions.run { grace.entityState = EntityState.IDLE },
                Actions.run { grace.direction = Direction.SOUTH },
            ), grace),
            Actions.delay(0.2f),
            Actions.addAction(Actions.sequence(
                Actions.run { guard1.direction = Direction.SOUTH },
                Actions.run { guard1.entityState = EntityState.RUNNING },
                Actions.moveBy(0f, -90f, 0.8f),
                Actions.run { guard1.direction = Direction.WEST },
                Actions.moveBy(-175f, 0f, 1.2f),
                Actions.run { guard1.entityState = EntityState.IDLE },
                Actions.run { guard1.direction = Direction.SOUTH },
            ), guard1),
            Actions.delay(0.1f),
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

    override fun exitScreen() {
        endCutsceneAnd { BattleScreen.load("ardor_orc_generals", this) }
    }

    override fun onNotifyBattleWon(battleId: String, spoils: Loot) {
        screenManager.setScreen(ScreenType.SCENE_ARDOR_2_WIN)
        SpoilsCutsceneScreen.load(spoils, ScreenType.SCENE_ARDOR_2_WIN)
    }

    override fun onNotifyBattleLost() {
        screenManager.setScreen(ScreenType.SCENE_DEATH)
    }

}
