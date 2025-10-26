package nl.t64.cot.screens.school

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import nl.t64.cot.Utils
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.party.abilities.AbilityDatabase
import nl.t64.cot.components.party.abilities.AbilityItem
import nl.t64.cot.screens.inventory.BaseTable
import nl.t64.cot.screens.inventory.ListenerKeyVertical


private const val FIRST_COLUMN_WIDTH = 48f
private const val SECOND_COLUMN_WIDTH = 210f
private const val THIRD_COLUMN_WIDTH = 40f
private const val FOURTH_COLUMN_WIDTH = 30f
private const val CONTAINER_HEIGHT = 704f
private const val ROW_HEIGHT = 48f
private const val SECOND_COLUMN_PAD_LEFT = 15f

class SchoolTable(
    schoolId: String,
    tooltip: SchoolTooltip
) : BaseTable(tooltip) {

    private val spellsToLearn: List<AbilityItem> = resourceManager.getSchoolInventory(schoolId)
        .map { AbilityDatabase.createAbilityItem(it) }
    private val verticalKeyListener = ListenerKeyVertical { updateIndex(it, spellsToLearn.size) }

    init {
        table.columnDefaults(0).width(FIRST_COLUMN_WIDTH)
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH)
        table.columnDefaults(2).width(THIRD_COLUMN_WIDTH)
        table.columnDefaults(3).width(FOURTH_COLUMN_WIDTH)
        table.defaults().height(ROW_HEIGHT)

        container.add(scrollPane).height(CONTAINER_HEIGHT)
        container.background = Utils.createTopBorder()
        container.addListener(verticalKeyListener)
    }

    fun learnSpell() {
        hideTooltip()
        val spellToLearn = spellsToLearn[selectedIndex]
        SpellLearner.learnSpell(spellToLearn, table.stage) { hasJustUpdated = true }
    }

    override fun selectAnotherSlotWhenIndexBecameOutOfBounds() {
        if (selectedIndex >= spellsToLearn.size) {
            selectedIndex = spellsToLearn.size - 1
        }
    }

    override fun fillRows() {
        spellsToLearn.forEachIndexed { index, spellItem -> fillRow(spellItem, index) }
    }

    fun stopScrolling() {
        verticalKeyListener.cleanup()
    }

    private fun fillRow(abilityItem: AbilityItem, index: Int) {
        table.add(createImageOf(abilityItem.id.name))
        val spellName = Label(abilityItem.name, LabelStyle(font, Color.BLACK))
        table.add(spellName).padLeft(SECOND_COLUMN_PAD_LEFT)
        table.add("")
        table.add("").row()
        scrollScrollPane()
        super.possibleSetSelected(index, spellName, abilityItem)
    }

    private fun scrollScrollPane() {
        if (!table.hasKeyboardFocus()) return
        val selectedY = CONTAINER_HEIGHT - (ROW_HEIGHT * selectedIndex)
        scrollPane.scrollTo(0f, selectedY, 0f, 0f)
    }

}
