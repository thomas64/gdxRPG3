package nl.t64.cot.screens.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Null
import ktx.assets.disposeSafely
import nl.t64.cot.Utils
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.FontProvider
import nl.t64.cot.screens.dialog.MessageDialogListener


private const val DIALOG_INIT_HEIGHT = 100L
private const val DIALOG_PAD = 60f

private const val INPUT_DELAY = 1f

class MessageDialog(
    private val multiplexer: InputMultiplexer
) {
    private val stage = Stage()
    private val font: BitmapFont = FontProvider.inconsolata24
    private lateinit var label: Label
    private val dialog: Dialog = createDialog()

    @Null
    private var actionAfterHide: (() -> Unit)? = null

    init {
        applyListeners()
    }

    fun dispose() {
        stage.dispose()
        font.disposeSafely()
    }

    fun setActionAfterHide(actionAfterHide: () -> Unit) {
        this.actionAfterHide = actionAfterHide
    }

    fun show(message: String, audioEvent: AudioEvent) {
        fillDialog(message)
        playSe(audioEvent)
        dialog.show(stage)
        if (Utils.preferenceManager.isInDebugMode) {
            setInputToStage()
        } else {
            Utils.runWithDelay(INPUT_DELAY) { setInputToStage() }
        }
    }

    fun update(dt: Float) {
        stage.act(dt)
        stage.draw()
    }

    private fun createDialog(): Dialog {
        label = Label("[BLACK]no message", LabelStyle(font, null))
        label.setAlignment(Align.center)
        return Utils.createParchmentDialog(font).apply {
            padLeft(DIALOG_PAD)
            padRight(DIALOG_PAD)
        }
    }

    private fun fillDialog(message: String) {
        val fontSize: Int = font.data.name.takeLast(2).toInt()
        val dialogHeight = ((message.lines().count() * fontSize) + DIALOG_INIT_HEIGHT).toFloat()
        label.setText("[BLACK]$message")
        dialog.contentTable.clear()
        dialog.contentTable.defaults().width(label.prefWidth)
        dialog.background.minHeight = dialogHeight
        dialog.text(label)
    }

    private fun applyListeners() {
        dialog.addListener(MessageDialogListener { hide() })
    }

    private fun setInputToStage() {
        Gdx.input.inputProcessor = stage
        Utils.setGamepadInputProcessor(stage)
    }

    private fun hide() {
        Gdx.input.inputProcessor = multiplexer
        Utils.setGamepadInputProcessor(multiplexer)
        playSe(AudioEvent.SE_CONVERSATION_NEXT)
        actionAfterHide?.let { hideWithAction(it) } ?: dialog.hide()
    }

    private fun hideWithAction(action: () -> Unit) {
        stage.addAction(Actions.sequence(
            Actions.run { dialog.hide() },
            Actions.delay(Constant.DIALOG_FADE_OUT_DURATION),
            Actions.run {
                action.invoke()
                actionAfterHide = null
            }
        ))
    }

}
