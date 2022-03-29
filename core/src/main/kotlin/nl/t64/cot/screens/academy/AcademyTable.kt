package nl.t64.cot.screens.academy

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import nl.t64.cot.Utils
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.party.skills.SkillDatabase
import nl.t64.cot.components.party.skills.SkillItem
import nl.t64.cot.screens.inventory.BaseTable
import nl.t64.cot.screens.inventory.ListenerKeyVertical


private const val FIRST_COLUMN_WIDTH = 48f
private const val SECOND_COLUMN_WIDTH = 205f
private const val THIRD_COLUMN_WIDTH = 40f
private const val FOURTH_COLUMN_WIDTH = 35f
private const val CONTAINER_HEIGHT = 704f
private const val ROW_HEIGHT = 48f
private const val SECOND_COLUMN_PAD_LEFT = 15f

internal class AcademyTable(
    academyId: String,
    tooltip: AcademyTooltip
) : BaseTable(tooltip) {

    private val skillsToTrain: List<SkillItem> = resourceManager.getAcademyInventory(academyId)
        .map { SkillDatabase.createSkillItem(it.key, it.value) }
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
        container.addListener(ListenerKeyVertical { updateIndex(it, skillsToTrain.size) })
    }

    fun upgradeSkill() {
        val skillToUpgrade = skillsToTrain[selectedIndex]
        SkillUpgrader.upgradeSkill(skillToUpgrade, table.stage) { setHasJustUpdate(true) }
    }

    override fun selectAnotherSlotWhenIndexBecameOutOfBounds() {
        if (selectedIndex >= skillsToTrain.size) {
            selectedIndex = skillsToTrain.size - 1
        }
    }

    override fun fillRows() {
        skillsToTrain.indices.forEach { fillRow(skillsToTrain[it], it) }
    }

    private fun fillRow(skillItem: SkillItem, index: Int) {
        table.add(createImageOf(skillItem.id.name))
        val skillName = Label(skillItem.name, LabelStyle(font, Color.BLACK))
        table.add(skillName).padLeft(SECOND_COLUMN_PAD_LEFT)
        table.add(skillItem.rank.toString())
        table.add("").row()
        scrollScrollPane()
        super.possibleSetSelected(index, skillName, skillItem)
    }

    private fun scrollScrollPane() {
        val selectedY = CONTAINER_HEIGHT - (ROW_HEIGHT * selectedIndex)
        scrollPane.scrollTo(0f, selectedY, 0f, 0f)
    }

}
