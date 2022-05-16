package nl.t64.cot.screens.school

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
    Please be welcome to my school.
    
    On the left you see which spells I can teach you, and on the right are the spells you already know.
    
    The numbers on the left represent to which rank I can teach you. The numbers on the right represent your current ranks.
    
    To learn a spell will cost you XP and gold.""".trimIndent()

internal class TeacherTable(npcId: String) : BaseTable(PersonalityTooltip()) {

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
