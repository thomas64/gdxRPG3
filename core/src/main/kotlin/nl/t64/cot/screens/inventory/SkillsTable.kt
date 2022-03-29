package nl.t64.cot.screens.inventory

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import nl.t64.cot.Utils
import nl.t64.cot.components.party.skills.SkillItem
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val FIRST_COLUMN_WIDTH = 48f
private const val SECOND_COLUMN_WIDTH = 205f
private const val THIRD_COLUMN_WIDTH = 40f
private const val FOURTH_COLUMN_WIDTH = 35f
private const val CONTAINER_HEIGHT = 313f
private const val ROW_HEIGHT = 48f
private const val SECOND_COLUMN_PAD_LEFT = 15f

internal class SkillsTable(
    skillFilter: (SkillItem) -> Boolean,
    tooltip: PersonalityTooltip,
    private val containerHeight: Float = CONTAINER_HEIGHT
) : BaseTable(tooltip) {

    private val scrollPane: ScrollPane
    private val filteredSkills: () -> List<SkillItem> = { selectedHero.getAllSkillsAboveZero().filter(skillFilter) }

    init {
        table.columnDefaults(0).width(FIRST_COLUMN_WIDTH)
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH)
        table.columnDefaults(2).width(THIRD_COLUMN_WIDTH)
        table.columnDefaults(3).width(FOURTH_COLUMN_WIDTH)
        table.top()
        table.defaults().height(ROW_HEIGHT)
        scrollPane = ScrollPane(table)
        container.add(scrollPane).height(containerHeight)
        container.background = Utils.createTopBorder()
        container.addListener(ListenerKeyVertical { updateIndex(it, filteredSkills().size) })
    }

    override fun selectAnotherSlotWhenIndexBecameOutOfBounds() {
        if (selectedIndex >= filteredSkills().size) {
            selectedIndex = filteredSkills().size - 1
        } else if (selectedIndex == -1) {
            selectedIndex = 0
        }
    }

    override fun fillRows() {
        val skillItemList = filteredSkills()
        skillItemList.indices.forEach { fillRow(skillItemList[it], it) }
        if (skillItemList.isEmpty()) {
            table.add(""); table.add("").padLeft(SECOND_COLUMN_PAD_LEFT); table.add(""); table.add("").row()
        }
    }

    private fun fillRow(skillItem: SkillItem, index: Int) {
        table.add(createImageOf(skillItem.id.name))
        val skillName = Label(skillItem.name, LabelStyle(font, Color.BLACK))
        table.add(skillName).padLeft(SECOND_COLUMN_PAD_LEFT)
        table.add(skillItem.rank.toString())
        val totalExtra = selectedHero.getExtraSkillForVisualOf(skillItem)
        addExtraToTable(totalExtra)
        scrollScrollPane()
        super.possibleSetSelected(index, skillName, skillItem)
    }

    private fun scrollScrollPane() {
        val selectedY = containerHeight - (ROW_HEIGHT * selectedIndex)
        scrollPane.scrollTo(0f, selectedY, 0f, 0f)
    }

}
