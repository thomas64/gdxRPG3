package nl.t64.cot.screens.school

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
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

class SchoolTable(schoolId: String, tooltip: SchoolTooltip) : BaseTable(tooltip) {

    private val spellsToLearn: List<SpellItem> = resourceManager.getSchoolInventory(schoolId)
        .map { SpellDatabase.createSpellItem(it.key, it.value) }

    init {
        table.columnDefaults(0).width(FIRST_COLUMN_WIDTH)
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH)
        table.columnDefaults(2).width(THIRD_COLUMN_WIDTH)
        table.columnDefaults(3).width(FOURTH_COLUMN_WIDTH)
        table.defaults().height(ROW_HEIGHT)

        container.add(scrollPane).height(CONTAINER_HEIGHT)
        container.background = Utils.createTopBorder()
        container.addListener(ListenerKeyVertical { updateIndex(it, spellsToLearn.size) })
    }

    fun upgradeSpell() {
        val spellToUpgrade = spellsToLearn[selectedIndex]
        SpellUpgrader.upgradeSpell(spellToUpgrade, table.stage) { hasJustUpdated = true }
    }

    override fun selectAnotherSlotWhenIndexBecameOutOfBounds() {
        if (selectedIndex >= spellsToLearn.size) {
            selectedIndex = spellsToLearn.size - 1
        }
    }

    override fun fillRows() {
        spellsToLearn.forEachIndexed { index, spellItem -> fillRow(spellItem, index) }
    }

    private fun fillRow(spellItem: SpellItem, index: Int) {
        table.add(createImageOf(spellItem.id))
        val spellName = Label(spellItem.name, LabelStyle(font, Color.BLACK))
        table.add(spellName).padLeft(SECOND_COLUMN_PAD_LEFT)
        table.add(spellItem.rank.toString())
        table.add("").row()
        scrollScrollPane()
        super.possibleSetSelected(index, spellName, spellItem)
    }

    private fun scrollScrollPane() {
        val selectedY = CONTAINER_HEIGHT - (ROW_HEIGHT * selectedIndex)
        scrollPane.scrollTo(0f, selectedY, 0f, 0f)
    }

}
