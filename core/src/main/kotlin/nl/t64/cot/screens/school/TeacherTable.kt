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
    
    On the left you see in which spells and to what rank I can teach you, and on the right are the spells with the ranks you already have been taught in.
    
    You cannot learn a spell here above the spell ranks I have myself.
    
    To learn a spell will cost you 'XP to Invest' and gold.""".trimIndent()

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

    override fun fillRows() {
        // empty
    }

}
