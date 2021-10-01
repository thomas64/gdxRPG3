package nl.t64.cot.screens.academy

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.inventory.BaseTable
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip

private const val TEXT_FONT = "fonts/spectral_extra_bold_28.ttf"
private const val TEXT_SIZE = 28

private const val TABLE_WIDTH = 390f
private const val TABLE_HEIGHT_EQUALIZER = 103f
private const val PADDING = 20f
private val TEXT = """
    Please be welcome to my academy.
    
    On the left you see in which skills and to what rank I can train you, and on the right are the skills with the ranks you already have been trained in.
    You cannot train a skill here above the rank I can train you.
    
    To train a skill will cost you gold and 'XP to Invest'.""".trimIndent()

internal class TrainerTable(private val npcId: String) : BaseTable(PersonalityTooltip()) {

    init {
        table.defaults().reset()
        table.defaults().width(TABLE_WIDTH)
        table.pad(PADDING)
        table.background = Utils.createTopBorder()
    }

    override fun fillRows() {
        table.add(Utils.getFaceImage(npcId)).left().size(Constant.FACE_SIZE)
        table.row()
        val text = Label(TEXT, table.skin).apply {
            wrap = true
            setAlignment(Align.topLeft)
        }
        table.add(text).padTop(PADDING).row()
        val font = resourceManager.getTrueTypeAsset(TEXT_FONT, TEXT_SIZE)
        val style = LabelStyle(font, Color.BLACK)
        table.add(Label(getGoldText(), style)).padTop(PADDING + TABLE_HEIGHT_EQUALIZER)
    }

    private fun getGoldText(): String {
        val goldAmount = gameData.inventory.getTotalOfItem("gold")
        return "Your amount of Gold: $goldAmount"
    }

}
