package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playBgm
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState


class SceneGameEnding : CutsceneScreen() {

    private lateinit var mozes: CutsceneActor
    private lateinit var grace: CutsceneActor

    override fun prepare() {
        mozes = CutsceneActor.createCharacter("mozes")
        grace = CutsceneActor.createCharacter("girl01")
        actorsStage.addActor(mozes)
        actorsStage.addActor(grace)
        actions = listOf(danceToTheMusic())
    }

    private fun danceToTheMusic(): Action {
        return Actions.sequence(
            Actions.run {
                setMapWithBgsOnly("death_scene")
                setFixedCameraPosition(0f, 0f)
                title.setText("You are the Hero of Adan!")
                title.setPosition(camera.position.x - (title.width / 2f), camera.position.y)
                mozes.setPosition(580f, 300f)
                mozes.direction = Direction.SOUTH
                mozes.entityState = EntityState.WALKING
                mozes.isVisible = true
                grace.setPosition(628f, 300f)
                grace.direction = Direction.SOUTH
                grace.entityState = EntityState.WALKING
                grace.isVisible = true
            },
            Actions.delay(2f),
            Actions.run { playBgm(AudioEvent.BGM_VICTORY, false) },
            Actions.delay(1.7f),

            actionFadeIn(),

            Actions.delay(72f),

            actionFadeOutWithoutBgmFading(),

            Actions.delay(2f),
            Actions.addAction(Actions.sequence(
                Actions.alpha(0f),
                Actions.visible(true),
                Actions.fadeIn(Constant.FADE_DURATION),
                Actions.delay(5f),
                Actions.run { isBgmFading = true },
                Actions.fadeOut(Constant.FADE_DURATION),
                Actions.visible(false),
                Actions.alpha(1f)
            ), title),
            Actions.delay(7f),
            Actions.run { exitScreen() }
        )
    }

    override fun exitScreen() {
        endCutsceneAnd {
            val mainMenu = screenManager.getMenuScreen(ScreenType.MENU_MAIN)
            mainMenu.processButton(ScreenType.MENU_CREDITS)
            playBgm(AudioEvent.BGM_TITLE)
        }
    }

}
