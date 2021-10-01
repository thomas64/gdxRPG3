package nl.t64.cot.screens.inventory

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import nl.t64.cot.Utils
import nl.t64.cot.components.party.skills.SkillItem
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val FIRST_COLUMN_WIDTH = 48f
private const val SECOND_COLUMN_WIDTH = 136f
private const val THIRD_COLUMN_WIDTH = 40f
private const val FOURTH_COLUMN_WIDTH = 35f
private const val CONTAINER_HEIGHT = 704f
private const val ROW_HEIGHT = 48f
private const val SECOND_COLUMN_PAD_LEFT = 15f

internal class SkillsTable(tooltip: PersonalityTooltip) : BaseTable(tooltip) {

    private val scrollPane: ScrollPane

    init {
        table.columnDefaults(0).width(FIRST_COLUMN_WIDTH)
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH)
        table.columnDefaults(2).width(THIRD_COLUMN_WIDTH)
        table.columnDefaults(3).width(FOURTH_COLUMN_WIDTH)
        table.top()
        table.defaults().height(ROW_HEIGHT)
        scrollPane = ScrollPane(table)
        container.add(scrollPane).height(CONTAINER_HEIGHT)
        container.background = Utils.createTopBorder()
        container.addListener(ListenerKeyVertical { updateIndex(it, selectedHero.getAllSkillsAboveZero().size) })
    }

    override fun selectCurrentSlot() {
        super.selectCurrentSlot()
        if (selectedIndex >= selectedHero.getAllSkillsAboveZero().size) {
            selectedIndex = selectedHero.getAllSkillsAboveZero().size - 1
        }
    }

    override fun fillRows() {
        val skillItemList = selectedHero.getAllSkillsAboveZero()
        skillItemList.indices.forEach { fillRow(skillItemList[it], it) }
    }

    private fun fillRow(skillItem: SkillItem, index: Int) {
        table.add(createImageOf(skillItem.id.name))
        val skillName = Label(skillItem.name, LabelStyle(font, Color.BLACK))
        table.add(skillName).padLeft(SECOND_COLUMN_PAD_LEFT)
        if (table.hasKeyboardFocus() && index == selectedIndex) {
            setSelected(skillName, skillItem)
        }
        table.add(skillItem.rank.toString())
        val totalExtra = selectedHero.getExtraSkillForVisualOf(skillItem)
        addExtraToTable(totalExtra)
        scrollScrollPane()
    }

    private fun setSelected(skillName: Label, skillItem: SkillItem) {
        skillName.style.fontColor = Constant.DARK_RED
        if (hasJustUpdated) {
            hasJustUpdated = false
            tooltip.setPosition(FIRST_COLUMN_WIDTH + SECOND_COLUMN_WIDTH) { getTooltipY() }
            tooltip.refresh(skillName, skillItem)
        }
    }

    private fun getTooltipY(): Float =
        CONTAINER_HEIGHT - (ROW_HEIGHT * selectedIndex) - (ROW_HEIGHT * 0.5f)

    private fun scrollScrollPane() {
        val selectedY = CONTAINER_HEIGHT - (ROW_HEIGHT * selectedIndex)
        scrollPane.scrollTo(0f, selectedY, 0f, 0f, false, true)
    }

}
