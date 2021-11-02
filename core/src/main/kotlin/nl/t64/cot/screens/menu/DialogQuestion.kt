package nl.t64.cot.screens.menu

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.utils.Align
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.constants.Constant


private const val FONT = "fonts/spectral_regular_24.ttf"
private const val FONT_SIZE = 24
private const val LINE_HEIGHT = 26f

private const val DIALOG_YES = "Yes"
private const val DIALOG_NO = "No"

private const val DIALOG_INIT_HEIGHT = 150L
private const val DIALOG_PAD_TOP = 20f
private const val DIALOG_PAD_BOTTOM = 40f
private const val BUTTON_SPACE_RIGHT = 100f

private const val NUMBER_OF_ITEMS = 2
private const val EXIT_INDEX = 1

class DialogQuestion(
    private val yesFunction: () -> Unit,
    private val message: String
) {
    private val dialogHeight: Float = ((message.lines().count() * FONT_SIZE) + DIALOG_INIT_HEIGHT).toFloat()
    private val font: BitmapFont = resourceManager.getTrueTypeAsset(FONT, FONT_SIZE).apply {
        data.setLineHeight(LINE_HEIGHT)
    }
    private val dialog: Dialog = createDialog()
    private var selectedIndex = 0

    fun show(stage: Stage, event: AudioEvent, startIndex: Int = EXIT_INDEX) {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, event)
        show(stage, startIndex)
    }

    fun show(stage: Stage, startIndex: Int = EXIT_INDEX) {
        dialog.show(stage)
        updateIndex(startIndex)
        applyListeners(startIndex)
    }

    private fun updateIndex(newIndex: Int) {
        selectedIndex = newIndex
        setAllTextButtonsToBlack()
        setCurrentTextButtonToRed()
    }

    private fun selectDialogItem() {
        when (selectedIndex) {
            0 -> processYesButton()
            1 -> processNoButton()
            else -> throw IllegalArgumentException("SelectedIndex not found.")
        }
    }

    private fun processYesButton() {
        dialog.hide()
        yesFunction.invoke()
    }

    private fun processNoButton() {
        audioManager.handle(AudioCommand.SE_STOP_ALL)
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_MENU_BACK)
        dialog.hide()
    }

    private fun setAllTextButtonsToBlack() {
        dialog.buttonTable.children.forEach { (it as TextButton).style.fontColor = Color.BLACK }
    }

    private fun setCurrentTextButtonToRed() {
        (dialog.buttonTable.getChild(selectedIndex) as TextButton).style.fontColor = Constant.DARK_RED
    }

    private fun createDialog(): Dialog {
        val label = Label(message, LabelStyle(font, Color.BLACK))
        label.setAlignment(Align.center)

        val buttonStyle = TextButtonStyle()
        buttonStyle.font = font
        buttonStyle.fontColor = Color.BLACK

        val yesButton = TextButton(DIALOG_YES, TextButtonStyle(buttonStyle))
        val noButton = TextButton(DIALOG_NO, TextButtonStyle(buttonStyle))

        return Utils.createParchmentDialog(font).apply {
            padTop(DIALOG_PAD_TOP)
            padBottom(DIALOG_PAD_BOTTOM)
            contentTable.defaults().width(label.prefWidth + BUTTON_SPACE_RIGHT)
            background.minHeight = dialogHeight
            text(label)
            buttonTable.add(yesButton).spaceRight(BUTTON_SPACE_RIGHT)
            buttonTable.add(noButton)
        }
    }

    private fun applyListeners(startIndex: Int) {
        val listenerKeyHorizontal = ListenerKeyHorizontal({ updateIndex(it) }, NUMBER_OF_ITEMS)
        listenerKeyHorizontal.updateSelectedIndex(startIndex)
        val listenerKeyConfirm = ListenerKeyConfirmDialog { selectDialogItem() }
        val listenerKeyCancel = ListenerKeyCancel({ updateIndex(it) }, { selectDialogItem() }, EXIT_INDEX)
        dialog.addListener(listenerKeyHorizontal)
        dialog.addListener(listenerKeyConfirm)
        dialog.addListener(listenerKeyCancel)
    }

}
