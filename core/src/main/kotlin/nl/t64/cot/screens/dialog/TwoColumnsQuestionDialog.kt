package nl.t64.cot.screens.dialog

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align


private const val FONT_SIZE = 24
private const val DIALOG_INIT_HEIGHT = 150L

class TwoColumnsQuestionDialog(
    message: Triple<String, String, String>,
    yesFunction: () -> Unit
) : BaseQuestionDialog(
    dialogHeight = message.toDialogHeight(),
    content = message.toDialogTable(),
    yesFunction = yesFunction
)

private fun Triple<String, String, String>.toDialogHeight(): Float {
    val firstLinesCount: Int = this.first.lines().count() * FONT_SIZE
    val thirdLinesCount: Int = this.third.lines().count() * FONT_SIZE
    return (firstLinesCount + thirdLinesCount + DIALOG_INIT_HEIGHT).toFloat()
}

private fun Triple<String, String, String>.toDialogTable(): Table {
    val font: BitmapFont = FontSpectralRegular24Provider.font
    val label1 = Label("[BLACK]${this.first}", LabelStyle(font, null))
    val label2 = Label("[BLACK]${this.second}", LabelStyle(font, null))
    val label3 = Label("[BLACK]${this.third}", LabelStyle(font, null))
    label1.setAlignment(Align.left)
    label2.setAlignment(Align.left)
    label3.setAlignment(Align.center)

    return Table().apply {
        add(label1)
        add(label2).row()
        add(label3).colspan(2)
    }
}


