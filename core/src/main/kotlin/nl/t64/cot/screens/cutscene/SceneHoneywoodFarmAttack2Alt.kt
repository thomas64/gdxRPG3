package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playBgm
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.battle.BattleScreen
import nl.t64.cot.screens.loot.SpoilsCutsceneScreen
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState


class SceneHoneywoodFarmAttack2Alt : CutsceneScreen() {

    private lateinit var mozes: CutsceneActor
    private lateinit var isembert: CutsceneActor
    private lateinit var miriel: CutsceneActor
    private lateinit var horse1: CutsceneActor
    private lateinit var horse2: CutsceneActor
    private lateinit var horse3: CutsceneActor
    private lateinit var horse4: CutsceneActor
    private lateinit var orc2: CutsceneActor
    private lateinit var orc3: CutsceneActor

    override fun prepare() {
        mozes = CutsceneActor.createCharacter("mozes")
        isembert = CutsceneActor.createCharacter("oldman02")
        miriel = CutsceneActor.createCharacter("woman05")
        horse1 = CutsceneActor.createCharacter("horse1")
        horse2 = CutsceneActor.createCharacter("horse1")
        horse3 = CutsceneActor.createCharacter("horse1")
        horse4 = CutsceneActor.createCharacter("horse1")
        orc2 = CutsceneActor.createCharacter("orc_scout")
        orc3 = CutsceneActor.createCharacter("orc_scout")

        actorsStage.addActor(horse1)
        actorsStage.addActor(horse2)
        actorsStage.addActor(horse3)
        actorsStage.addActor(horse4)
        actorsStage.addActor(isembert)
        actorsStage.addActor(miriel)
        actorsStage.addActor(mozes)
        actorsStage.addActor(orc3)
        actorsStage.addActor(orc2)

        actions = listOf(secondOrcAttacks())
    }

    private fun secondOrcAttacks(): Action {
        return Actions.sequence(
            Actions.run {
                setMapWithNoSound("honeywood_stable")
                playBgm(AudioEvent.BGM_ARDOR)
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
                mozes.isVisible = true
                mozes.setPosition(196f, 492f)
                mozes.direction = Direction.EAST
                isembert.isVisible = true
                isembert.setPosition(96f, 290f)
                isembert.direction = Direction.NORTH
                miriel.isVisible = true
                miriel.setPosition(192f, 290f)
                miriel.direction = Direction.NORTH

                orc2.isVisible = true
                orc2.setPosition(624f, 566f)
                orc2.direction = Direction.NORTH
                orc3.isVisible = true
                orc3.setPosition(816f, 566f)
                orc3.direction = Direction.NORTH
            },
            actionFadeIn(),
            Actions.delay(2f),

            Actions.addAction(Actions.sequence(
                Actions.run { orc2.direction = Direction.WEST },
                Actions.delay(1f),
                Actions.run { orc2.direction = Direction.EAST },
                Actions.delay(1f),
                Actions.run { orc2.direction = Direction.WEST },
                Actions.delay(1f),
                Actions.run { orc2.entityState = EntityState.RUNNING },
                Actions.parallel(
                    actionMoveBy(orc2, -384f, -72f, 2.5f, FAST_STEP),
                    Actions.sequence(
                        Actions.delay(2f),
                        Actions.run { exitScreen() }
                    )
                )
            ), orc2)

        )
    }

    override fun exitScreen() {
        endCutsceneAnd { BattleScreen.load("farm_battle2", this) }
    }

    override fun onNotifyBattleWon(battleId: String, spoils: Loot) {
        screenManager.setScreen(ScreenType.SCENE_HONEYWOOD_FARM_ATTACK_3_ALT)
        SpoilsCutsceneScreen.load(spoils, ScreenType.SCENE_HONEYWOOD_FARM_ATTACK_3_ALT)
    }

    override fun onNotifyBattleLost() {
        screenManager.setScreen(ScreenType.MENU_MAIN)
    }

}
