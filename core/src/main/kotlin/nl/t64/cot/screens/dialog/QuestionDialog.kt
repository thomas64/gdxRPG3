package nl.t64.cot.screens.dialog

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align


private const val FONT_SIZE = 24
private const val DIALOG_INIT_HEIGHT = 150L
private const val DIALOG_PAD = 20f

class QuestionDialog(
    message: String,
    yesFunction: () -> Unit
) : BaseQuestionDialog(
    dialogHeight = message.toDialogHeight(),
    content = message.toDialogContent(),
    yesFunction = yesFunction
) {

    fun setLeftAlignment() {
        ((dialog.contentTable.getChild(0) as Table).getChild(0) as Label).setAlignment(Align.left)
        dialog.contentTable.padTop(DIALOG_PAD)
        dialog.contentTable.padLeft(DIALOG_PAD)
        dialog.contentTable.padRight(DIALOG_PAD)
        dialog.background.minWidth = 0f
    }

}

private fun String.toDialogHeight(): Float {
    return ((this.lines().count() * FONT_SIZE) + DIALOG_INIT_HEIGHT).toFloat()
}

private fun String.toDialogContent(): Table {
    val font: BitmapFont = FontSpectralRegular24Provider.font
    val label = Label("[BLACK]$this", LabelStyle(font, null))
    label.setAlignment(Align.center)

    return Table().apply {
        add(label)
    }
}
