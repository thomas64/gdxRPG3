package nl.t64.cot.screens.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.AddAction
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.cutscene.CutsceneActor
import nl.t64.cot.toTexture
import kotlin.random.Random


class EndOfTime(
    private val stage: Stage,
    private val camera: Camera
) {
    private val originalViewport = stage.viewport


    fun fadeWithFlamesAnd(actionAfter: () -> Unit) {
        val background: Image = createBlackBackground()
        stage.addActor(background)

        val title: Label = createTitle()
        stage.addActor(title)

        val flames: List<CutsceneActor> = createFlames()
        flames.forEach { stage.addActor(it) }

        if (gameData.numberOfCycles !in listOf(2)) {
            stage.addAction(
                Actions.sequence(
                    createDefaultActions(flames, background),
                    Actions.delay(7f),
                    titleAction(title),
                    Actions.delay(8f),
                    Actions.run {
                        screenManager.setScreen(ScreenType.MENU_MAIN)
                        actionAfter.invoke()
                    }
                )
            )
        } else if (gameData.numberOfCycles == 2) {
            stage.addAction(
                Actions.sequence(
                    createDefaultActions(flames, background),
                    Actions.delay(7f),
                    Actions.run {
                        screenManager.setScreen(ScreenType.SCENE_END_OF_CYCLE_2)
                        actionAfter.invoke()
                    }
                )
            )
        }
    }

    private fun createDefaultActions(flames: List<CutsceneActor>, background: Image): Action {
        return Actions.sequence(
            Actions.delay(1f),
            Actions.run { audioManager.handle(AudioCommand.BGS_FADE_IN, AudioEvent.BGS_QUAKE) },
            Actions.delay(3f),
            flamesAction(flames),
            Actions.delay(5f),
            Actions.run { background.addAction(Actions.alpha(1f)) }
        )
    }

    private fun createBlackBackground(): Image {
        val blackTexture = Color.BLACK.toTexture()
        return Image(blackTexture).apply {
            color.a = 0f
            setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        }.also { blackTexture.dispose() }
    }

    private fun createTitle(): Label {
        val font = resourceManager.getTrueTypeAsset("fonts/spectral_regular_24.ttf", 24)
        val style = LabelStyle(font, Color.WHITE)
        return Label("", style).apply {
            setAlignment(Align.center)
            isVisible = false
            setText("Is this the end of Adan...?")
            setPosition(camera.position.x - (width / 2f), camera.position.y)
        }
    }

    private fun createFlames(): List<CutsceneActor> {
        return List(1200) { CutsceneActor.createFlame() }
            .onEachIndexed { i, it ->
                it.setPosition(i % 19 * 100f - 150f,
                               Random.nextInt(1200, 4400).toFloat())
            }
    }

    private fun flamesAction(flames: List<CutsceneActor>): RunnableAction {
        return Actions.run {
            flames.forEach {
                val scale = Random.nextInt(4, 10).toFloat()
                it.addAction(Actions.sequence(
                    Actions.scaleBy(scale, scale),
                    Actions.delay(1f),
                    Actions.moveBy(0f, -5000f, 10f),
                    Actions.removeActor()
                ))
            }
        }
    }

    private fun titleAction(title: Label): AddAction {
        return Actions.addAction(Actions.sequence(
            Actions.run { stage.viewport = camera.viewport },
            Actions.alpha(0f),
            Actions.visible(true),
            Actions.fadeIn(Constant.FADE_DURATION),
            Actions.delay(4f),
            Actions.run { audioManager.fadeBgsInThread() },
            Actions.delay(1f),
            Actions.fadeOut(Constant.FADE_DURATION),
            Actions.visible(false),
            Actions.alpha(1f),
            Actions.run { stage.viewport = originalViewport }
        ), title)
    }

}
