package nl.t64.cot.screens.dialog

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import nl.t64.cot.screens.FontProvider


private const val FONT_SIZE = 24
private const val DIALOG_INIT_HEIGHT = 150L

class TwoColumnsQuestionDialog(
    message: Triple<String, String, String>,
    yesFunction: () -> Unit
) : BaseQuestionDialog(
    dialogHeight = message.toDialogHeight(),
    content = message.toDialogTable(),
    yesFunction = yesFunction
) {

    init {
        if (message.second.isBlank()) {
            // see the three padding lines from QuestionDialog.kt
                                                // padTop(20f) from below
            dialog.contentTable.padLeft(5f)     // + 15f from below = padTop(20f)
            dialog.contentTable.padRight(20f)   // padRight(20f)
            dialog.background.minWidth = 0f
        } else {
            val label1 = dialog.contentTable.findActor("label1") as Label
            (label1.parent as Table).getCell(label1).padRight(6f)
        }
    }

}

private fun Triple<String, String, String>.toDialogHeight(): Float {
    val firstLinesCount: Int = this.first.lines().count() * FONT_SIZE
    val thirdLinesCount: Int = this.third.lines().count() * FONT_SIZE
    return (firstLinesCount + thirdLinesCount + DIALOG_INIT_HEIGHT).toFloat()
}

private fun Triple<String, String, String>.toDialogTable(): Table {
    val font: BitmapFont = FontProvider.inconsolata24
    val label1 = Label("[BLACK]${this.first}", LabelStyle(font, null))
    val label2 = Label("[BLACK]${this.second}", LabelStyle(font, null))
    val label3 = Label("[BLACK]${this.third}", LabelStyle(font, null))
    label1.setAlignment(Align.left)
    label2.setAlignment(Align.left)
    label3.setAlignment(Align.center)

    label1.name = "label1"

    return Table().apply {
        padTop(20f)
        padLeft(15f)
        add(label1).top()
        add(label2).top().row()
        add(label3).colspan(2).padTop(-10f)
    }
}
