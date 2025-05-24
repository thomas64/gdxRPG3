package nl.t64.cot.screens.dialog

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Null
import nl.t64.cot.Utils
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.screens.FontProvider


private const val DIALOG_INIT_HEIGHT = 100L
private const val DIALOG_PAD = 60f

private const val INPUT_DELAY = 0.5f

class MessageDialog(
    private val message: String
) {
    private val font: BitmapFont = FontProvider.inconsolata24
    private val fontSize: Int = font.data.name.takeLast(2).toInt()
    private val dialogHeight: Float = (message.lines().count() * fontSize + DIALOG_INIT_HEIGHT).toFloat()
    private val dialog: Dialog = createDialog()

    @Null
    private var actionAfterHide: (() -> Unit)? = null
    private var playClosingSound: Boolean = true

    fun setActionAfterHide(actionAfterHide: () -> Unit) {
        this.actionAfterHide = actionAfterHide
    }

    fun disableClosingSound() {
        playClosingSound = false
    }

    fun show(stage: Stage, event: AudioEvent? = null, confirmDelay: Float = 0f) {
        event?.let { playSe(it) }
        dialog.show(stage)
        Utils.runWithDelay(confirmDelay) { applyListeners() }
    }

    fun setLeftAlignment() {
        (dialog.contentTable.getChild(0) as Label).setAlignment(Align.left)
    }

    fun setWidthToMinimum() {
        dialog.background.minWidth = 0f // now it will be as wide as the padding left and right
    }

    private fun createDialog(): Dialog {
        val label = Label("[BLACK]$message", LabelStyle(font, null))
        label.setAlignment(Align.center)
        return Utils.createParchmentDialog(font).apply {
            padLeft(DIALOG_PAD)
            padRight(DIALOG_PAD)
            background.minHeight = dialogHeight
            text(label)
        }
    }

    private fun applyListeners() {
        Utils.runWithDelay(INPUT_DELAY) {
            dialog.addListener(MessageDialogListener { hide() })
        }
    }

    private fun hide() {
        dialog.clearListeners()
        if (playClosingSound) {
            playSe(AudioEvent.SE_CONVERSATION_NEXT)
        }
        actionAfterHide?.let { hideWithAction(it) } ?: dialog.hide()
    }

    private fun hideWithAction(action: () -> Unit) {
        dialog.hide()
        action.invoke()
        actionAfterHide = null
    }

}
