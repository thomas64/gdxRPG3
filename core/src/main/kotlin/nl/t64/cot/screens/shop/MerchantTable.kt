package nl.t64.cot.screens.shop

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import nl.t64.cot.Utils.createTopBorder
import nl.t64.cot.Utils.getFaceImage
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.inventory.BaseTable
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val TABLE_WIDTH = 390f
private const val TABLE_HEIGHT = 501f
private const val PADDING = 20f
private val TEXT = """
    Please be welcome to my shop.
    
    On the left you will find my wares, and on the right is your inventory.
    
    You can buy or sell 1 item, half a stack, or the full stack at once.
    
    Payment will happen immediately.""".trimIndent()

internal class MerchantTable(npcId: String) : BaseTable(PersonalityTooltip()) {

    init {
        table.defaults().reset()
        table.defaults().size(TABLE_WIDTH, TABLE_HEIGHT)
        table.pad(PADDING)
        table.add(getFaceImage(npcId)).left().size(Constant.FACE_SIZE)
        table.row()
        val text = Label(TEXT, table.skin)
        text.wrap = true
        text.setAlignment(Align.topLeft)
        table.add(text).padTop(PADDING)
        table.background = createTopBorder()
    }

    override fun fillRows() {
        // empty
    }

}
