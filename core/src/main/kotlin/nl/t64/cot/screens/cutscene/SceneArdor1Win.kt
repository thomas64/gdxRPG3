package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.battle.BattleScreen
import nl.t64.cot.screens.world.entity.Direction


class SceneArdor1Win : CutsceneScreen() {

    private lateinit var mozes: CutsceneActor
    private lateinit var grace: CutsceneActor
    private lateinit var ardor: CutsceneActor

    override fun prepare() {
        mozes = CutsceneActor.createCharacter("mozes")
        grace = CutsceneActor.createCharacter("girl01")
        ardor = CutsceneActor.createCharacter("ardor")
        actorsStage.addActor(grace)
        actorsStage.addActor(ardor)
        actorsStage.addActor(mozes)
        actions = listOf(endBattleWithGenerals(),
                         startBattleWithArdor())
    }

    private fun endBattleWithGenerals(): Action {
        return Actions.sequence(
            Actions.run {
                setMapWithBgsOnly("honeywood_forest_ardor_3")
                setCameraPosition(0f, 0f)
                ardor.setPosition(456f, 240f)
                ardor.direction = Direction.SOUTH
                ardor.isVisible = true
                grace.setPosition(456f, 315f)
                grace.direction = Direction.SOUTH
                grace.isVisible = true
                mozes.setPosition(456f, 186f)
                mozes.direction = Direction.NORTH
                mozes.isVisible = true
            },

            actionFadeIn(),

            Actions.delay(2f),
            Actions.run { showConversationDialog("mozes_wins_battle_from_generals", "ardor") }
        )
    }

    private fun startBattleWithArdor(): Action {
        return Actions.run { exitScreen() }
    }

    override fun onNotifyBattleWon(battleId: String, spoils: Loot) {
        screenManager.setScreen(ScreenType.SCENE_GAME_ENDING)
    }

    override fun onNotifyBattleLost() {
        (screenManager.getScreen(ScreenType.SCENE_ARDOR_1_LOSE) as SceneArdor1Lose).apply { areGeneralsAlive = false }
        screenManager.setScreen(ScreenType.SCENE_ARDOR_1_LOSE)
    }

    override fun exitScreen() {
        endCutsceneAnd { BattleScreen.load("ardor", this) }
    }

}
