package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playBgm
import nl.t64.cot.components.condition.isTrue
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.battle.BattleScreen
import nl.t64.cot.screens.loot.SpoilsCutsceneScreen
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState


class SceneHoneywoodFarmAttack1 : CutsceneScreen() {

    private lateinit var mozes: CutsceneActor
    private lateinit var luana: CutsceneActor
    private lateinit var isembert: CutsceneActor
    private lateinit var miriel: CutsceneActor
    private lateinit var horse1: CutsceneActor
    private lateinit var horse2: CutsceneActor
    private lateinit var horse3: CutsceneActor
    private lateinit var horse4: CutsceneActor
    private lateinit var orc1: CutsceneActor
    private lateinit var orc2: CutsceneActor
    private lateinit var orc3: CutsceneActor

    override fun prepare() {
        mozes = CutsceneActor.createCharacter("mozes")
        luana = CutsceneActor.createCharacter("luana")
        isembert = CutsceneActor.createCharacter("oldman02")
        miriel = CutsceneActor.createCharacter("woman05")
        horse1 = CutsceneActor.createCharacter("horse1")
        horse2 = CutsceneActor.createCharacter("horse1")
        horse3 = CutsceneActor.createCharacter("horse1")
        horse4 = CutsceneActor.createCharacter("horse1")
        orc1 = CutsceneActor.createCharacter("orc_scout")
        orc2 = CutsceneActor.createCharacter("orc_scout")
        orc3 = CutsceneActor.createCharacter("orc_scout")

        actorsStage.addActor(horse1)
        actorsStage.addActor(horse2)
        actorsStage.addActor(horse3)
        actorsStage.addActor(horse4)
        actorsStage.addActor(isembert)
        actorsStage.addActor(miriel)
        actorsStage.addActor(luana)
        actorsStage.addActor(mozes)
        actorsStage.addActor(orc3)
        actorsStage.addActor(orc1)
        actorsStage.addActor(orc2)

        actions = listOf(luanaWantToJoin(),
                         firstOrcsWalksIn(),
                         otherOrcsWalkIn())
    }

    private fun luanaWantToJoin(): Action {
        return Actions.sequence(
            Actions.run {
                setMapWithBgmBgs("honeywood_stable")
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
                isembert.direction = Direction.EAST
                miriel.isVisible = true
                miriel.setPosition(192f, 290f)
                miriel.direction = Direction.WEST
            },
            Actions.delay(2f),
            actionFadeIn(),
            Actions.delay(1f),
            Actions.run { showConversationDialog("luana_joins_party", "luana") }
        )
    }

    private fun firstOrcsWalksIn(): Action {
        return Actions.sequence(
            Actions.delay(1f),
            Actions.run {
                orc1.isVisible = true
                orc1.setPosition(912f, 336f)
                orc1.direction = Direction.NORTH
                orc1.entityState = EntityState.RUNNING
                orc2.isVisible = true
                orc2.setPosition(912f, 336f)
                orc2.direction = Direction.NORTH
                orc2.entityState = EntityState.RUNNING
                orc3.isVisible = true
                orc3.setPosition(912f, 336f)
                orc3.direction = Direction.NORTH
                orc3.entityState = EntityState.RUNNING

                isBgmFading = true
            },
            Actions.addAction(Actions.sequence(
                actionMoveBy(orc1, 0f, 168f, 2f, FAST_STEP),
                Actions.run { orc1.entityState = EntityState.IDLE },
                Actions.run { orc1.direction = Direction.WEST },
                Actions.delay(0.5f),
                Actions.run { orc1.direction = Direction.EAST },
                Actions.delay(0.5f),
                Actions.run { orc1.direction = Direction.SOUTH },
                Actions.delay(0.5f),
                Actions.run { showConversationDialog("farm_orc_calls_others", "orc_scout") }
            ), orc1),
            Actions.addAction(Actions.sequence(
                Actions.delay(2.5f),
                Actions.run { mozes.direction = Direction.EAST },
            ), mozes),
            Actions.addAction(Actions.sequence(
                Actions.delay(3f),
                Actions.run { luana.direction = Direction.EAST },
            ), luana),
            Actions.addAction(Actions.sequence(
                Actions.delay(4.5f),
                Actions.run { isembert.direction = Direction.NORTH },
            ), isembert),
            Actions.addAction(Actions.sequence(
                Actions.delay(5f),
                Actions.run { miriel.direction = Direction.NORTH },
            ), miriel),
        )
    }

    private fun otherOrcsWalkIn(): Action {
        return Actions.sequence(
            Actions.delay(0.5f),
            Actions.run {
                isBgmFading = false
                playBgm(AudioEvent.BGM_ARDOR)
            },
            Actions.addAction(Actions.sequence(
                Actions.moveBy(0f, 120f, 1.5f),
                Actions.run { orc2.direction = Direction.WEST },
                Actions.moveBy(-96f, 110f, 1f),
                Actions.run { orc2.direction = Direction.NORTH },
                Actions.run { orc2.entityState = EntityState.IDLE }
            ), orc2),
            Actions.addAction(Actions.parallel(
                Actions.sequence(
                    Actions.delay(1f),
                    Actions.moveBy(0f, 120f, 1.5f),
                    Actions.run { orc3.direction = Direction.WEST },
                    Actions.moveBy(-288f, 110f, 2f),
                    Actions.run { orc3.direction = Direction.NORTH },
                    Actions.run { orc3.entityState = EntityState.IDLE }
                ),
                actionWalkSound(orc3, 7.1f, FAST_STEP),
            ), orc3),
            Actions.addAction(Actions.sequence(
                Actions.delay(2.5f),
                Actions.run { orc1.direction = Direction.WEST },
                Actions.delay(2f),
                Actions.run { orc1.entityState = EntityState.RUNNING },
                Actions.moveBy(-480f, 62f, 3f),
                Actions.run { orc1.direction = Direction.NORTH },
                Actions.run { orc1.entityState = EntityState.IDLE },
                Actions.delay(1.5f),
                Actions.run { orc1.direction = Direction.WEST },
                Actions.delay(1f),
                Actions.run { orc1.direction = Direction.EAST },
                Actions.delay(1f),
                Actions.run { orc1.direction = Direction.WEST },
                Actions.delay(1f),
                Actions.run { orc1.entityState = EntityState.RUNNING },
                Actions.parallel(
                    actionMoveBy(orc1, -192f, -48f, 1.5f, FAST_STEP),
                    Actions.sequence(
                        Actions.delay(1f),
                        Actions.run { exitScreen() }
                    )
                )
            ), orc1)
        )
    }

    override fun exitScreen() {
        endCutsceneAnd {
            if ("alone_in_party".isTrue()) {
                gameData.quests.getQuestById("quest_luana_before_10").accept()
                val luana = gameData.heroes.getCertainHero("luana")
                gameData.heroes.removeHero("luana")
                gameData.party.addHero(luana)
            }

            BattleScreen.load("farm_battle", this)
        }
    }

    override fun onNotifyBattleWon(battleId: String, spoils: Loot) {
        screenManager.setScreen(ScreenType.SCENE_HONEYWOOD_FARM_ATTACK_2)
        SpoilsCutsceneScreen.load(spoils, ScreenType.SCENE_HONEYWOOD_FARM_ATTACK_2)
    }

    override fun onNotifyBattleLost() {
        screenManager.setScreen(ScreenType.MENU_MAIN)
    }

}
