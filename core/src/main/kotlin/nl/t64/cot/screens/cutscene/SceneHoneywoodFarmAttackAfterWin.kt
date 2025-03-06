package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils.gameData
import nl.t64.cot.screens.world.entity.Direction


class SceneHoneywoodFarmAttackAfterWin : CutsceneScreen() {

    private lateinit var mozes: CutsceneActor
    private lateinit var luana: CutsceneActor
    private lateinit var isembert: CutsceneActor
    private lateinit var miriel: CutsceneActor
    private lateinit var horse1: CutsceneActor
    private lateinit var horse2: CutsceneActor
    private lateinit var horse3: CutsceneActor
    private lateinit var horse4: CutsceneActor

    override fun prepare() {
        mozes = CutsceneActor.createCharacter("mozes")
        luana = CutsceneActor.createCharacter("luana")
        isembert = CutsceneActor.createCharacter("oldman02")
        miriel = CutsceneActor.createCharacter("woman05")
        horse1 = CutsceneActor.createCharacter("horse1")
        horse2 = CutsceneActor.createCharacter("horse1")
        horse3 = CutsceneActor.createCharacter("horse1")
        horse4 = CutsceneActor.createCharacter("horse1")

        actorsStage.addActor(horse1)
        actorsStage.addActor(horse2)
        actorsStage.addActor(horse3)
        actorsStage.addActor(horse4)
        actorsStage.addActor(isembert)
        actorsStage.addActor(miriel)
        actorsStage.addActor(luana)
        actorsStage.addActor(mozes)

        actions = listOf(everybodyIsHappy(),
                         endScene())
    }

    private fun everybodyIsHappy(): Action {
        return Actions.sequence(
            Actions.run {
                setMapWithHardBgmBgs("honeywood_stable")
                setFixedCameraPosition(570f, 504f)
                horse1.isVisible = true
                horse1.setPosition(432f, 672f)
                horse1.direction = Direction.SOUTH
                horse2.isVisible = true
                horse2.setPosition(624f, 732f)
                horse2.direction = Direction.WEST
                horse3.isVisible = true
                horse3.setPosition(864f, 696f)
                horse3.direction = Direction.NORTH
                luana.isVisible = true
                luana.setPosition(192f, 528f)
                luana.direction = Direction.SOUTH
                mozes.isVisible = true
                mozes.setPosition(196f, 492f)
                mozes.direction = Direction.NORTH
                isembert.isVisible = true
                isembert.setPosition(96f, 290f)
                isembert.direction = Direction.NORTH
                miriel.isVisible = true
                miriel.setPosition(192f, 290f)
                miriel.direction = Direction.NORTH
            },
            actionFadeIn(),
            Actions.delay(2f),
            Actions.run { showConversationDialog("farm_battle_after_win", "luana") }
        )
    }

    private fun endScene(): Action {
        return Actions.sequence(
            Actions.delay(1f),
            Actions.run { exitScreen() }
        )
    }

    override fun exitScreen() {
        gameData.quests.getQuestById("quest_honeywood_elder").setTaskComplete("7") // "_7_"
        gameData.quests.getQuestById("quest_luana_before_10").setTaskComplete("9") // "_9_"
        gameData.clock.setTimeOfDay("11:00")

        // todo, endcutscene without music fade. but even if it's not fading, loading worldscreen always restarts bgm.
        // at this moment, I don't know how to fix that.
        endCutsceneAndOpenMap("honeywood_stable", "scene_honeywood_farm_attack")
    }

}
