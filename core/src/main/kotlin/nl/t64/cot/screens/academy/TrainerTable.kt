package nl.t64.cot.screens.academy

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import nl.t64.cot.Utils
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.inventory.BaseTable
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val TABLE_WIDTH = 390f
private const val TABLE_HEIGHT = 501f
private const val PADDING = 20f
private val TEXT = """
    Welcome to my academy!

    On the left, you'll find the skills I can train you in, and on the right, the skills you've already acquired.

    The numbers on the left indicate the ranks at which I can instruct you, while those on the right represent your current proficiency levels.

    Training a skill requires both XP and gold.""".trimIndent()

internal class TrainerTable(npcId: String) : BaseTable(PersonalityTooltip()) {

    init {
        table.defaults().reset()
        table.defaults().size(TABLE_WIDTH, TABLE_HEIGHT)
        table.pad(PADDING)
        table.background = Utils.createTopBorder()
        table.add(Utils.getFaceImage(npcId)).left().size(Constant.FACE_SIZE)
        table.row()
        val text = Label(TEXT, table.skin).apply {
            wrap = true
            setAlignment(Align.topLeft)
        }
        table.add(text).padTop(PADDING).row()
    }

}
