package nl.t64.cot.screens.inventory

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import nl.t64.cot.Utils
import nl.t64.cot.components.party.spells.SpellItem
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val FIRST_COLUMN_WIDTH = 48f
private const val SECOND_COLUMN_WIDTH = 205f
private const val THIRD_COLUMN_WIDTH = 40f
private const val FOURTH_COLUMN_WIDTH = 35f
private const val CONTAINER_HEIGHT = 704f
private const val ROW_HEIGHT = 48f
private const val SECOND_COLUMN_PAD_LEFT = 15f

internal class SpellsTable(tooltip: PersonalityTooltip) : BaseTable(tooltip) {

    init {
        table.columnDefaults(0).width(FIRST_COLUMN_WIDTH)
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH)
        table.columnDefaults(2).width(THIRD_COLUMN_WIDTH)
        table.columnDefaults(3).width(FOURTH_COLUMN_WIDTH)
        table.defaults().height(ROW_HEIGHT)

        container.add(scrollPane).height(CONTAINER_HEIGHT)
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
        val spellList = selectedHero.getAllSpells()
        if (spellList.isEmpty()) {
            table.add("")
            table.add("").padLeft(SECOND_COLUMN_PAD_LEFT)
            table.add("")
            table.add("").row()
        } else {
            spellList.forEachIndexed { index, spellItem -> fillRow(spellItem, index) }
        }
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
