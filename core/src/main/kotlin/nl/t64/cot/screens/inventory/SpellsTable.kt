package nl.t64.cot.screens.inventory

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import nl.t64.cot.Utils
import nl.t64.cot.components.party.spells.SpellItem
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val FIRST_COLUMN_WIDTH = 48f
private const val SECOND_COLUMN_WIDTH = 205f
private const val THIRD_COLUMN_WIDTH = 40f
private const val FOURTH_COLUMN_WIDTH = 35f
private const val CONTAINER_HEIGHT = 313f
private const val ROW_HEIGHT = 48f
private const val SECOND_COLUMN_PAD_LEFT = 15f
private const val SCHOOL_TITLE_PAD_TOP = 10f
private const val SCHOOL_TITLE_PAD_BOTTOM = 20f

internal class SpellsTable(
    tooltip: PersonalityTooltip,
    private val containerHeight: Float = CONTAINER_HEIGHT
) : BaseTable(tooltip) {

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
        container.add(scrollPane).height(containerHeight)
        container.background = Utils.createTopBorder()
        container.addListener(ListenerKeyVertical { updateIndex(it, selectedHero.getAllSpells().size) })
    }

    override fun selectAnotherSlotWhenIndexBecameOutOfBounds() {
        if (selectedIndex >= selectedHero.getAllSpells().size) {
            selectedIndex = selectedHero.getAllSpells().size - 1
        } else if (selectedIndex == -1) {
            selectedIndex = 0
        }
    }

    override fun fillRows() {
        fillSchoolRow()
        fillSpellRows()
    }

    private fun fillSchoolRow() {
        table.add(createImageOf(selectedHero.school.name))
        table.add(selectedHero.school.title + " School").padLeft(SECOND_COLUMN_PAD_LEFT)
        table.add("")
        table.add("").padBottom(SCHOOL_TITLE_PAD_BOTTOM).row()
    }

    private fun fillSpellRows() {
        val spellList = selectedHero.getAllSpells()
        spellList.indices.forEach { fillSpellRow(spellList[it], it) }
    }

    private fun fillSpellRow(spellItem: SpellItem, index: Int) {
        table.add(createImageOf(spellItem.id))
        val spellName = Label(spellItem.name, LabelStyle(font, Color.BLACK))
        table.add(spellName).padLeft(SECOND_COLUMN_PAD_LEFT)
        super.possibleSetSelected(index, spellName, spellItem)
        table.add(spellItem.rank.toString())
        table.add("").row()
        scrollScrollPane()
    }

    private fun scrollScrollPane() {
        val selectedY = containerHeight - (ROW_HEIGHT * selectedIndex)
        scrollPane.scrollTo(0f, selectedY, 0f, 0f, false, true)
    }

    override fun getTooltipPosition(): Vector2 {
        val x = SECOND_COLUMN_WIDTH - FIRST_COLUMN_WIDTH
        val y = containerHeight - (ROW_HEIGHT * selectedIndex) - (ROW_HEIGHT * 2f) - (ROW_HEIGHT * 0.4f)
        return Vector2(x, y)
    }

}
