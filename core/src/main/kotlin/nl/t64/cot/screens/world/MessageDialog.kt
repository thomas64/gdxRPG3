package nl.t64.cot.screens.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Null
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.inventory.messagedialog.MessageDialogListener


private const val FONT = "fonts/spectral_regular_24.ttf"
private const val FONT_SIZE = 24
private const val LINE_HEIGHT = 26f

private const val DIALOG_INIT_HEIGHT = 100L
private const val DIALOG_PAD = 60f

class MessageDialog(private val multiplexer: InputMultiplexer) {

    private val stage = Stage()
    private val font: BitmapFont = resourceManager.getTrueTypeAsset(FONT, FONT_SIZE).apply {
        data.setLineHeight(LINE_HEIGHT)
    }
    private lateinit var label: Label
    private val dialog: Dialog = createDialog()

    @Null
    private var actionAfterHide: (() -> Unit)? = null

    init {
        applyListeners()
    }

    fun dispose() {
        stage.dispose()
        try {
            font.dispose()
        } catch (e: GdxRuntimeException) {
            // font is already exposed.
        }
    }

    fun setActionAfterHide(actionAfterHide: () -> Unit) {
        this.actionAfterHide = actionAfterHide
    }

    fun show(message: String, audioEvent: AudioEvent) {
        fillDialog(message)
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, audioEvent)
        dialog.show(stage)
        Gdx.input.inputProcessor = stage
        Utils.setGamepadInputProcessor(stage)
    }

    fun update(dt: Float) {
        stage.act(dt)
        stage.draw()
    }

    private fun createDialog(): Dialog {
        label = Label("no message", LabelStyle(font, Color.BLACK))
        label.setAlignment(Align.center)
        return Utils.createParchmentDialog(font).apply {
            padLeft(DIALOG_PAD)
            padRight(DIALOG_PAD)
        }
    }

    private fun fillDialog(message: String) {
        val dialogHeight = ((message.lines().count() * FONT_SIZE) + DIALOG_INIT_HEIGHT).toFloat()
        label.setText(message)
        dialog.contentTable.clear()
        dialog.contentTable.defaults().width(label.prefWidth)
        dialog.background.minHeight = dialogHeight
        dialog.text(label)
    }

    private fun applyListeners() {
        dialog.addListener(MessageDialogListener { hide() })
    }

    private fun hide() {
        Gdx.input.inputProcessor = multiplexer
        Utils.setGamepadInputProcessor(multiplexer)
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_CONVERSATION_NEXT)
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
