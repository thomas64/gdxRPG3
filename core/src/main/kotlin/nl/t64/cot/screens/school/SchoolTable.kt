package nl.t64.cot.screens.school

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import nl.t64.cot.Utils
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.party.spells.SpellDatabase
import nl.t64.cot.components.party.spells.SpellItem
import nl.t64.cot.screens.inventory.BaseTable
import nl.t64.cot.screens.inventory.ListenerKeyVertical


private const val FIRST_COLUMN_WIDTH = 48f
private const val SECOND_COLUMN_WIDTH = 210f
private const val THIRD_COLUMN_WIDTH = 40f
private const val FOURTH_COLUMN_WIDTH = 30f
private const val CONTAINER_HEIGHT = 704f
private const val ROW_HEIGHT = 48f
private const val SECOND_COLUMN_PAD_LEFT = 15f
private const val SCHOOL_TITLE_PAD_TOP = 10f
private const val SCHOOL_TITLE_PAD_BOTTOM = 20f

class SchoolTable(
    schoolId: String,
    tooltip: SchoolTooltip
) : BaseTable(tooltip) {

    private val spellsToLearn: List<SpellItem> = resourceManager.getSchoolInventory(schoolId)
        .map { SpellDatabase.createSpellItem(it.key, it.value) }
    private val scrollPane: ScrollPane

    init {
        table.columnDefaults(0).width(FIRST_COLUMN_WIDTH)
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH)
        table.columnDefaults(2).width(THIRD_COLUMN_WIDTH)
        table.columnDefaults(3).width(FOURTH_COLUMN_WIDTH)
        table.top()
        table.defaults().height(ROW_HEIGHT)
        table.padTop(SCHOOL_TITLE_PAD_TOP)
        scrollPane = ScrollPane(table)
        container.add(scrollPane).height(CONTAINER_HEIGHT)
        container.background = Utils.createTopBorder()
        container.addListener(ListenerKeyVertical { updateIndex(it, spellsToLearn.size) })
    }

    fun upgradeSpell() {
        val spellToUpgrade = spellsToLearn[selectedIndex]
        SpellUpgrader.upgradeSpell(spellToUpgrade, table.stage) { setHasJustUpdate(true) }
    }

    override fun selectAnotherSlotWhenIndexBecameOutOfBounds() {
        if (selectedIndex >= spellsToLearn.size) {
            selectedIndex = spellsToLearn.size - 1
        }
    }

    override fun fillRows() {
        fillSchoolRow()
        fillSpellRows()
    }

    private fun fillSchoolRow() {
        val lastSpell = spellsToLearn.last()
        table.add(createImageOf(lastSpell.school.name))
        table.add(lastSpell.school.title + " School").padLeft(SECOND_COLUMN_PAD_LEFT)
        table.add("")
        table.add("").padBottom(SCHOOL_TITLE_PAD_BOTTOM).row()
    }

    private fun fillSpellRows() {
        spellsToLearn.indices.forEach { fillSpellRow(spellsToLearn[it], it) }
    }

    private fun fillSpellRow(spellItem: SpellItem, index: Int) {
        table.add(createImageOf(spellItem.id))
        val spellName = Label(spellItem.name, Label.LabelStyle(font, Color.BLACK))
        table.add(spellName).padLeft(SECOND_COLUMN_PAD_LEFT)
        super.possibleSetSelected(index, spellName, spellItem)
        table.add(spellItem.rank.toString())
        table.add("").row()
        scrollScrollPane()
    }

    private fun scrollScrollPane() {
        val selectedY = CONTAINER_HEIGHT - (ROW_HEIGHT * selectedIndex)
        scrollPane.scrollTo(0f, selectedY, 0f, 0f, false, true)
    }

    override fun getTooltipPosition(): Vector2 {
        val x = SECOND_COLUMN_WIDTH - FIRST_COLUMN_WIDTH
        val y = CONTAINER_HEIGHT - (ROW_HEIGHT * selectedIndex) - (ROW_HEIGHT * 2f) - (ROW_HEIGHT * 0.4f)
        return Vector2(x, y)
    }

}
