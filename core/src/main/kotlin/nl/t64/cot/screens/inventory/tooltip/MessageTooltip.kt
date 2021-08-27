package nl.t64.cot.screens.inventory.tooltip

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import nl.t64.cot.constants.Constant


private const val PAD = 50f
private const val DELAY = 1f
private const val SHOW_DURATION = 5f

class MessageTooltip : BaseTooltip() {

    private val label: Label

    init {
        super.window.padLeft(PAD)
        super.window.padRight(PAD)
        val labelStyle = LabelStyle(BitmapFont(), Color.WHITE)
        label = Label(null, labelStyle)
        label.setAlignment(Align.center)
        super.window.add(label)
    }

    fun show(message: String, stage: Stage) {
        stage.addActor(window)
        showWindow(message)
    }

    private fun showWindow(message: String) {
        window.addAction(Actions.after(Actions.sequence(Actions.run { setupWindow(message) },
                                                        Actions.alpha(0f),
                                                        Actions.visible(true),
                                                        Actions.delay(DELAY),
                                                        Actions.fadeIn(Constant.FADE_DURATION),
                                                        Actions.delay(SHOW_DURATION),
                                                        Actions.fadeOut(Constant.FADE_DURATION),
                                                        Actions.visible(false))))
    }

    private fun setupWindow(message: String) {
        label.setText(message)
        window.pack()
        window.setPosition(Gdx.graphics.width - window.width - PAD,
                           Gdx.graphics.height - window.height - PAD)
    }

}
