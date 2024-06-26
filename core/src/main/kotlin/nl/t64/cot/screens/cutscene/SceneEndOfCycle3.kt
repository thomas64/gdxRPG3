package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import nl.t64.cot.Utils
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.loot.ReceiveCutsceneScreen


class SceneEndOfCycle3 : CutsceneScreen() {

    private lateinit var mozes: CutsceneActor
    private lateinit var mozesDead: Image
    private lateinit var ylarus: Image

    override fun prepare() {
        mozes = CutsceneActor.createCharacter("mozes")
        mozesDead = Utils.createImage("sprites/characters/damage1.png", 0, 0, 48, 48)
        ylarus = CutsceneActor.createCharacter("ylarus")

        actorsStage.addActor(mozes)
        actorsStage.addActor(mozesDead)
        actorsStage.addActor(ylarus)

        actions = listOf(goneToHeaven(),
                         receiveCrystal())
    }

    private fun goneToHeaven(): Action {
        return Actions.sequence(
            Actions.run {
                mozes.isVisible = false
                mozesDead.setPosition(456f, 246f)
                mozesDead.isVisible = true
                ylarus.setPosition(456f, 360f)
                setMapWithBgmBgs("ylarus_place")
                setFixedCameraPosition(0f, 0f)
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

    private fun receiveCrystal(): Action {
        return Actions.sequence(
            Actions.delay(2f),
            Actions.addAction(Actions.fadeOut(3f), ylarus),
            Actions.delay(5f),
            Actions.run { exitScreen() }
        )
    }

    override fun exitScreen() {
        val crystal = Loot.createSingleItem("crystal_of_time")
        endCutsceneAnd { ReceiveCutsceneScreen.load(crystal, ScreenType.SCENE_USE_CRYSTAL_OF_TIME) }
    }

}
